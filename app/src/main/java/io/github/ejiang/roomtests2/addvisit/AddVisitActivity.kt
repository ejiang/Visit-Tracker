package io.github.ejiang.roomtests2.addvisit

import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import io.github.ejiang.roomtests2.R
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
        if (actionType == "edit") addEdit = AddEdit.EDIT

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
                putString("name", name)
                putString("rid", rid)
                putString("address", address)
                putString("category", category)
                putDouble("lat", lat!!)
                putDouble("lng", lng!!)
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
                    Toast.makeText(applicationContext, "Visit added", Toast.LENGTH_SHORT).show()
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
