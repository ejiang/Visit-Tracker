package io.github.ejiang.roomtests2

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import io.github.ejiang.roomtests2.browserestaurants.BrowseRestaurants
import io.github.ejiang.roomtests2.browsevisits.BrowseVisits
import io.github.ejiang.roomtests2.second.Second
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        buttonStart.setOnClickListener {
            val i = Intent(this, Second::class.java)
            startActivity(i)
        }

        buttonRest.setOnClickListener {
            val i = Intent(this, BrowseRestaurants::class.java)
            startActivity(i)
        }

        buttonVisits.setOnClickListener {
            val i = Intent(this, BrowseVisits::class.java)
            startActivity(i)
        }
    }
}
