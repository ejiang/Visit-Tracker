package io.github.ejiang.visittracker.browsevisits

import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import io.github.ejiang.visittracker.R
import io.github.ejiang.visittracker.TitleListener
import io.github.ejiang.visittracker.addvisit.WriterThread
import io.github.ejiang.visittracker.db.RestTrackDB
import io.github.ejiang.visittracker.db.RestaurantDB
import io.github.ejiang.visittracker.interfaces.DeleteVidListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_history.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [History.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [History.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class History : Fragment(), DeleteVidListener, ReadRestaurantListener {
    private var listener: TitleListener? = null

    private lateinit var vm : BrowseVisitsViewModel
    private lateinit var writerThread: WriterThread

    // keep a map of restaurants
    private val restMap : HashMap<String, RestaurantDB> = HashMap()
    private lateinit var dispose : Disposable

    private var rid = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        writerThread = WriterThread("writer")
        writerThread.start()

        if (arguments != null && arguments!!.containsKey("rid")) {
            // this is a list of visits for a particular restaurant
            // the enclosing activity will set the title
            rid = arguments!!.getString("rid")!!
        } else {
            // we have to set the title ourselves
            listener?.setTopTitle("History")
        }

        setup()
    }

    private fun setup() {
        // RV
        rvVisits.layoutManager = LinearLayoutManager(context)
        val adapter = BrowseVisitsAdapter(requireContext(),
                                            lis = LinkedList(),
                                            deleteListener = this,
                                            readRestaurantListener =  this,
                                            showName = rid.isEmpty())
        rvVisits.adapter = adapter

        val vdb = RestTrackDB.getInstance(requireContext())
        this.vm = ViewModelProviders.of(this).get(BrowseVisitsViewModel::class.java)

        if (rid.isNotBlank()) {
            vm.start(vdb!!, rid)
        } else {
            vm.start(vdb!!)
        }

        vm.getList().observe(this, androidx.lifecycle.Observer { adapter.setList(it) })

        // restMap
        dispose = vdb.rtDao().readAllRestaurantsRx()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { restList ->
                    for (r in restList) restMap[r.rid] = r
                }
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
        dispose.dispose()
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun delete(vid: String, rid: String) {
        writerThread.postTask(Runnable{
            Thread.sleep(200)
            vm.deleteVid(vid, rid)
            Toast.makeText(requireContext(), "Visit deleted", Toast.LENGTH_SHORT).show()
        })
    }

    override fun readRestaurant(rid: String) : RestaurantDB {
        return restMap[rid]!!
    }

    /*
    * The following is for say, after returning from an edit
    * */
    /* not needed
    private var stopped = false
    override fun onStop() {
        stopped = true
        super.onStop()
    }
    override fun onResume() {
        // refresh adapter after editing, but only on return from activity
        if (stopped && rid.isNotBlank()) {
            vm.loadList(rid)
        } else if (stopped) {
            vm.loadList()
        }

        stopped = false
        super.onResume()
    }
    */
}
