package io.github.ejiang.visittracker.addvisit

import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import io.github.ejiang.visittracker.R
import kotlinx.android.synthetic.main.activity_add_visit.*


class AddVisitActivity : AppCompatActivity() {
    private lateinit var mWriterThread: WriterThread
    private var addEdit : AddEdit = AddEdit.ADD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visit)

        toolbarAddVisit.setNavigationIcon(R.drawable.ic_close_white_24dp)
        setSupportActionBar(toolbarAddVisit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mWriterThread = WriterThread("writer")
        mWriterThread.start()

        // pull stuff
        val actionType = intent.extras?.getString("actionType")
        var note : String? = ""
        var spending : Int? = 0
        var timestamp : String? = ""
        var vid: String? = ""
        if (actionType == "edit") {
            addEdit = AddEdit.EDIT
            note = intent.extras?.getString("note")
            spending = intent.extras?.getInt("spending")
            timestamp = intent.extras?.getString("timestamp")
            vid = intent.extras?.getString("vid")
        }

        val name = intent.extras?.getString("name")
        val rid = intent.extras?.getString("rid")
        val address = intent.extras?.getString("address")
        val category = intent.extras?.getString("category")
        val lat = intent.extras?.getDouble("lat")
        val lng = intent.extras?.getDouble("lng")

        title = name

        var f = supportFragmentManager.findFragmentById(R.id.addVisitContainer)
        if (f == null) {
            f = AddVisitFragment()
            val b = Bundle()
            b.apply {
                putString("actionType", actionType)
                putString("name", name)
                putString("rid", rid)
                putString("address", address)
                putString("category", category)
                putDouble("lat", lat!!)
                putDouble("lng", lng!!)
            }

            if (addEdit == AddEdit.EDIT) {
                b.apply {
                    putString("note", note)
                    putInt("spending", spending!!)
                    putString("timestamp", timestamp)
                    putString("vid", vid)
                }
            }

            f.arguments = b
            supportFragmentManager.beginTransaction().add(R.id.addVisitContainer, f).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_menu_done -> {
                mWriterThread.postTask(Runnable {
                    obtainViewModel(this@AddVisitActivity).saveNote(addEdit)
                    Thread.sleep(500)
                    val t = if(addEdit == AddEdit.ADD) "Visit added" else "Visit edited"
                    Toast.makeText(applicationContext, t, Toast.LENGTH_SHORT).show()
                    finish()
                })
            }
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return true
    }

    companion object {
        fun obtainViewModel(act: AddVisitActivity) : AddViewModel {
            return ViewModelProviders.of(act).get(AddViewModel::class.java)
        }
    }

    override fun onDestroy() {
        mWriterThread.quitSafely()
        super.onDestroy()
    }
}
