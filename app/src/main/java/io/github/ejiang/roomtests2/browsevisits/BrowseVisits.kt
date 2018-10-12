package io.github.ejiang.roomtests2.browsevisits

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.ejiang.roomtests2.R
import io.github.ejiang.roomtests2.addvisit.WriterThread
import io.github.ejiang.roomtests2.db.RestTrackDB
import io.github.ejiang.roomtests2.interfaces.DeleteVidListener
import kotlinx.android.synthetic.main.activity_browse_visits.*
import java.util.*

class BrowseVisits : AppCompatActivity(), DeleteVidListener {
    private lateinit var vm : BrowseVisitsViewModel
    private lateinit var writerThread: WriterThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_visits)

        writerThread = WriterThread("writer")
        writerThread.start()

        setupRV()
    }

    private fun setupRV() {
        rvVisits.layoutManager = LinearLayoutManager(this)
        val adapter = BrowseVisitsAdapter(LinkedList(), this)
        rvVisits.adapter = adapter

        val vdb = RestTrackDB.getInstance(this)
        this.vm = ViewModelProviders.of(this).get(BrowseVisitsViewModel::class.java)
        vm.start(vdb!!)

        vm.getList().observe(this, Observer { adapter.setList(it) })
    }

    override fun delete(vid: String, rid: String) {
        writerThread.postTask(Runnable{
            Thread.sleep(200)
            vm.deleteVid(vid, rid)
            Toast.makeText(this@BrowseVisits, "Visit deleted", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onDestroy() {
        writerThread.quitSafely()
        super.onDestroy()
    }

    private fun edit() {
        ;
    }
}
