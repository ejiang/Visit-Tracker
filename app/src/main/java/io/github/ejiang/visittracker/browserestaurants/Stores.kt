package io.github.ejiang.visittracker.browserestaurants

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import io.github.ejiang.visittracker.R
import io.github.ejiang.visittracker.TitleListener
import io.github.ejiang.visittracker.addvisit.WriterThread
import io.github.ejiang.visittracker.db.RestTrackDB
import io.github.ejiang.visittracker.interfaces.DeleteListener
import io.github.ejiang.visittracker.search.GlideApp
import io.github.ejiang.visittracker.search.GlideRequest
import kotlinx.android.synthetic.main.activity_browse_restaurants.*


data class RestCard(
        val rid: String,
        val img_url: String,
        val address: String,
        val name: String,
        val category: String,
        val daysAgo: String,
        val spent: String,
        val visits: String
)

val selectors = arrayOf(Selector.DAYS_AGO, Selector.SPENT, Selector.VISITS)

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Stores.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Stores.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Stores : Fragment(), DeleteListener {
    private var listener: TitleListener? = null

    private lateinit var vm : BrowseRestaurantsViewModel
    private lateinit var req : GlideRequest<Drawable>
    private lateinit var popup : PopupMenu

    private lateinit var writerThread: WriterThread

    private val menuMap : HashMap<Pair<Boolean, Int>, String> =
            hashMapOf(Pair(true, 0) to "Most Recent",
                    Pair(true, 1) to "Most Spent",
                    Pair(true, 2) to "Most Visited",
                    Pair(false, 0) to "Least Recent",
                    Pair(false, 1) to "Least Spent",
                    Pair(false, 2) to "Least Visited")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_stores, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listener?.setTopTitle("Stores")

        writerThread = WriterThread("writer")
        writerThread.start()

        val vdb = RestTrackDB.getInstance(requireContext())
        vm = ViewModelProviders.of(this).get(BrowseRestaurantsViewModel::class.java)

        vm.start(vdb!!)

        req = GlideApp.with(this).asDrawable()

        setupRV()
    }

    private fun setupRV() {
        rvRestaurants.layoutManager = LinearLayoutManager(context)
        val adapter = BrowseRestaurantsAdapter(requireContext(), req, ArrayList(), this)
        rvRestaurants.adapter = adapter

        vm.getList().observe(this, androidx.lifecycle.Observer {
            adapter.setList(it)
        })
    }

    override fun deleteId(rid: String) {
        writerThread.postTask(Runnable {
            //Thread.sleep(200)
            vm.deleteRid(rid)
            Toast.makeText(requireContext(), "Restaurant deleted", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_browse_restaurants, menu)

        val swapButton : ImageView? = menu.findItem(R.id.menu_swap).actionView as ImageView
        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotation)
        if (swapButton != null) {
            swapButton.setImageResource(R.drawable.ic_swap_vert_white_24dp)
            swapButton.setPadding(36,12,36,12) // same as other
            swapButton.setOnClickListener { view ->
                view.startAnimation(rotation)
                flip()
            }
        }
    }

    private fun flip() {
        vm.flipOrder()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort -> showSortPopupMenu()
            R.id.menu_swap -> { /* taken care of in onCreateOptionsMenu */ }
        }
        return true
    }

    private fun showSortPopupMenu() {
        val regularOrder = vm.regularOrder
        val itemSelection = selectors.indexOf(vm.selector)

        popup = PopupMenu(requireContext(), activity?.findViewById(R.id.menu_sort))
        popup.menuInflater.inflate(R.menu.sort_predicates, popup.menu)

        popup.menu.getItem(0).title = menuMap[Pair(regularOrder,0)]
        popup.menu.getItem(1).title = menuMap[Pair(regularOrder,1)]
        popup.menu.getItem(2).title = menuMap[Pair(regularOrder,2)]

        popup.menu.getItem(itemSelection).isChecked = true

        popup.setOnMenuItemClickListener(fun(item: MenuItem) : Boolean {
            when (item.itemId) {
                R.id.sortDaysAgo -> {
                    vm.changeSelector(Selector.DAYS_AGO, regularOrder)
                }
                R.id.sortSpent -> {
                    vm.changeSelector(Selector.SPENT, regularOrder)
                }
                R.id.sortVisits -> {
                    vm.changeSelector(Selector.VISITS, regularOrder)
                }
                else -> { }
            }
            item.isChecked = true
            return true
        })
        popup.show()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TitleListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement TitleListener")
        }
    }

    override fun onDestroy() {
        writerThread.quitSafely()
        super.onDestroy()
    }

    // when coming back
    // unnecessary. flowable always watching
    /*
    private var stopped = false
    override fun onStop() {
        stopped = true
        super.onStop()
    }
    override fun onResume() {
        if (stopped) {
            vm.loadList()
            stopped = false
        }
        super.onResume()
    }*/

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
