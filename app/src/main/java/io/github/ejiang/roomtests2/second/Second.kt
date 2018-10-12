package io.github.ejiang.roomtests2.second

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.ejiang.roomtests2.R
import io.github.ejiang.roomtests2.db.RestTrackDB
import io.github.ejiang.roomtests2.db.RestaurantDB
import io.github.ejiang.roomtests2.db.VisitDB
import io.github.ejiang.roomtests2.search.Search
import kotlinx.android.synthetic.main.activity_second.*

class Second : AppCompatActivity() {

    private lateinit var vm: SecondViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        setupVM()

        addVisitButton.setOnClickListener {
            val i = Intent(this, Search::class.java)
            startActivity(i)
        }
    }

    private fun setupVM() {
        val wdb = RestTrackDB.getInstance(applicationContext)
        vm = ViewModelProviders.of(this).get(SecondViewModel::class.java)
        vm.start(wdb!!)

        val rObs : Observer<List<RestaurantDB>> = Observer { it ->
            val sb = StringBuilder()
            for (s in it!!.reversed()) {
                sb.append("${s.name} ${s.rid}\n")
            }
            tvRest.text = sb.toString()
        }

        val vObs : Observer<List<VisitDB>> = Observer { it ->
            val sb = StringBuilder()
            for (s in it!!.reversed()) {
                sb.append("${s.vid} ${s.timestamp} ${s.rid} ${s.note}\n")
            }
            tvVisits.text = sb.toString()
        }

        vm.restaurantsData.observe(this, rObs)
        vm.visitsData.observe(this, vObs)
    }
}
