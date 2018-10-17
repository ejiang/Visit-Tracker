package io.github.ejiang.visittracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.ejiang.visittracker.browserestaurants.Stores
import io.github.ejiang.visittracker.browsevisits.History
import io.github.ejiang.visittracker.search.Search
import kotlinx.android.synthetic.main.activity_main.*

enum class Frag {
    HISTORY, STORES
}

interface TitleListener {
    fun setTopTitle(s: String)
}

/*
* Mostly about handling the fragments
* */
class MainActivity : AppCompatActivity(), TitleListener {
    private var current : Frag = Frag.STORES
    private val stores: Stores = Stores()
    private val history: History = History()
    private lateinit var fm : FragmentManager

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.store -> {
                storesFrag()
                return@OnNavigationItemSelectedListener true
            }
            R.id.history -> {
                historyFrag()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "History"

        fab.setOnClickListener {
            val i = Intent(this, Search::class.java)
            startActivity(i)
        }

        fm = supportFragmentManager

        bottom_nav.setOnNavigationItemSelectedListener(navListener)

        showStores()
    }

    // the initial state
    private fun showStores() {
        var f = fm.findFragmentById(R.id.container)
        if (f == null) {
            f = stores
            fm.beginTransaction().add(R.id.container, f).commit()
        }
    }

    private fun storesFrag() {
        if (current != Frag.STORES) {
            fm.beginTransaction().replace(R.id.container, stores).commit()
            current = Frag.STORES
        }
    }

    private fun historyFrag() {
        if (current != Frag.HISTORY) {
            fm.beginTransaction().replace(R.id.container, history).commit()
            current = Frag.HISTORY
        }
    }

    override fun setTopTitle(s: String) {
        title = s
    }
}
