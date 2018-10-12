package io.github.ejiang.roomtests2.browserestaurants

import androidx.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.ejiang.roomtests2.R
import io.github.ejiang.roomtests2.addvisit.WriterThread
import io.github.ejiang.roomtests2.db.RestTrackDB
import io.github.ejiang.roomtests2.interfaces.DeleteListener
import io.github.ejiang.roomtests2.search.GlideApp
import io.github.ejiang.roomtests2.search.GlideRequest
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

class BrowseRestaurants : AppCompatActivity(), DeleteListener {
    private lateinit var vm : BrowseRestaurantsViewModel
    private lateinit var req : GlideRequest<Drawable>
    private lateinit var popup : PopupMenu

    private lateinit var writerThread: WriterThread

    // UI stuff
    private var itemSelection = 0
    private var regularOrder = true

    private val menuMap : HashMap<Pair<Boolean, Int>, String> =
            hashMapOf(Pair(true, 0) to "Most Recent",
                      Pair(true, 1) to "Most Spent",
                      Pair(true, 2) to "Most Visited",
                      Pair(false, 0) to "Least Recent",
                      Pair(false, 1) to "Least Spent",
                      Pair(false, 2) to "Least Visited")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_restaurants)

        writerThread = WriterThread("writer")
        writerThread.start()

        val vdb = RestTrackDB.getInstance(this)
        vm = ViewModelProviders.of(this).get(BrowseRestaurantsViewModel::class.java)
        vm.start(vdb!!)

        req = GlideApp.with(this).asDrawable()

        setupRV()
    }

    private fun setupRV() {
        rvRestaurants.layoutManager = LinearLayoutManager(this)
        val adapter = BrowseRestaurantsAdapter(req, ArrayList(), this)
        rvRestaurants.adapter = adapter

        vm.getList().observe(this, Observer {
            adapter.setList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_browse_restaurants, menu)

        val swapButton : ImageView? = menu.findItem(R.id.menu_swap).actionView as ImageView
        val rotation = AnimationUtils.loadAnimation(this, R.anim.rotation)
        if (swapButton != null) {
            swapButton.setImageResource(R.drawable.ic_swap_vert_white_24dp)
            swapButton.setPadding(36,12,36,12) // same as other
            swapButton.setOnClickListener { view ->
                view.startAnimation(rotation)
                flip()
            }
        }
        return true
    }

    private fun flip() {
        vm.flipOrder()
        regularOrder = !regularOrder
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort -> showSortPopupMenu()
            R.id.menu_swap -> { /* taken care of in onCreateOptionsMenu */ }
        }
        return true
    }

    private fun showSortPopupMenu() {
        popup = PopupMenu(this, findViewById(R.id.menu_sort))
        popup.menuInflater.inflate(R.menu.sort_predicates, popup.menu)

        popup.menu.getItem(0).title = menuMap[Pair(regularOrder,0)]
        popup.menu.getItem(1).title = menuMap[Pair(regularOrder,1)]
        popup.menu.getItem(2).title = menuMap[Pair(regularOrder,2)]

        popup.menu.getItem(itemSelection).isChecked = true

        popup.setOnMenuItemClickListener(fun(item: MenuItem) : Boolean {
            when (item.itemId) {
                R.id.sortDaysAgo -> {
                    if (itemSelection != 0) {
                        itemSelection = 0
                        vm.changeSelector(Selector.DAYS_AGO, regularOrder)
                    }
                }
                R.id.sortSpent -> {
                    if (itemSelection != 1) {
                        itemSelection = 1
                        vm.changeSelector(Selector.SPENT, regularOrder)
                    }
                }
                R.id.sortVisits -> {
                    if (itemSelection != 2) {
                        itemSelection = 2
                        vm.changeSelector(Selector.VISITS, regularOrder)
                    }
                }
                else -> itemSelection = 0
            }
            item.isChecked = true
            return true
        })
        popup.show()
    }

    override fun deleteId(rid: String) {
        writerThread.postTask(Runnable {
            Thread.sleep(200)
            vm.deleteRid(rid)
            Toast.makeText(this@BrowseRestaurants, "Restaurant deleted", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onDestroy() {
        writerThread.quitSafely()
        super.onDestroy()
    }
}
