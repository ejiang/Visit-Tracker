package io.github.ejiang.visittracker.browsevisits

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.ejiang.visittracker.R
import io.github.ejiang.visittracker.TitleListener

class BrowseVisits : AppCompatActivity(), TitleListener {
    private var rid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_visits)

        if (intent.hasExtra("rid")) {
            rid = intent.getStringExtra("rid")
        }
        if (intent.hasExtra("name")) {
            setTopTitle(intent.getStringExtra("name"))
        }

        setup()
    }

    private fun setup() {
        var f = supportFragmentManager.findFragmentById(R.id.frameBrowseVisits)
        if (f == null) {
            f = History()
            val b = Bundle()
            b.putString("rid", rid)
            f.arguments = b
            supportFragmentManager.beginTransaction().add(R.id.frameBrowseVisits, f).commit()
        }
    }

    override fun setTopTitle(s: String) {
        title = s
    }
}
