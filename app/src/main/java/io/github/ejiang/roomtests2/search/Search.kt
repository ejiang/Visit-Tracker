package io.github.ejiang.roomtests2.search

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.FixedPreloadSizeProvider
import io.github.ejiang.roomtests2.R
import io.github.ejiang.roomtests2.networking.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

class Search : AppCompatActivity() {

    var lisItems : ArrayList<Restaurant> = ArrayList()
    private lateinit var adapter : SearchAdapter
    private lateinit var svm : SearchViewModel
    private lateinit var req : GlideRequest<Drawable>
    private var limit = 10
    private lateinit var query : String
    private lateinit var location : String
    private val sr = SearchRepo.getRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        toolbar.title = ""

        setupRV()

        setupPullLayout()

        // bind to the VM

        // this is lifecycle observer, very different
        // a single directionality

        val sObs : Observer<ArrayList<Restaurant>> = Observer {
            adapter.setData(it!!)
        }
        svm = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        svm.getCurrent().value = lisItems
        svm.getCurrent().observe(this, sObs)
        svm.viewState.value = false
        svm.viewState.observe(this, Observer {
            if (it == true) {
                placeholder.visibility = View.GONE
                pullLayout.visibility = View.VISIBLE
            } else {
                placeholder.visibility = View.VISIBLE
                pullLayout.visibility = View.GONE
            }
        })


        // the search

        search_button.setOnClickListener {
            query = editText1.text.toString()
            location = editText2.text.toString()

            if (query == "" || location == "") {
                val toast = Toast.makeText(applicationContext, "Both fields required", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                toast.show()
                return@setOnClickListener
            }

            makeRequest()
        }
    }

    private fun setupRV() {
        rvResults.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        req = GlideApp.with(this).asDrawable()
        adapter = SearchAdapter(this, lisItems, req)
        rvResults.adapter = adapter

        val sizeProvider: ListPreloader.PreloadSizeProvider<Restaurant> = FixedPreloadSizeProvider(32,32)
        val modelProvider: ListPreloader.PreloadModelProvider<Restaurant> = MyPreloadModelProvider()
        val preloader: RecyclerViewPreloader<Restaurant> = RecyclerViewPreloader(Glide.with(this), modelProvider, sizeProvider, 10)
        rvResults.addOnScrollListener(preloader)
    }

    private fun setupPullLayout() {
        pullLayout.releaseListener = object : PullReleaseListener {
            override fun onRelease() {
                if (limit < 50) {
                    limit += 10
                    makeRequest()
                }
            }
        }
    }

    private fun makeRequest() {
        val o: Observable<Resp> = sr.getMovies(query, location, limit.toString())
        val d = o.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ s ->
                    val v: Venues = s.response
                    val l: List<Restaurant> = v.venues
                    val a: ArrayList<Restaurant> = ArrayList(l)
                    svm.change(a)
                    svm.viewState.value = true
                }, {
                    val toast = Toast.makeText(applicationContext, "No results", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER_VERTICAL,0,0)
                    toast.show()
                })
    }

    inner class MyPreloadModelProvider : ListPreloader.PreloadModelProvider<Restaurant> {
        override fun getPreloadItems(position: Int): MutableList<Restaurant> {
            return Collections.singletonList(svm.getCurrent().value!![position])
        }

        override fun getPreloadRequestBuilder(r: Restaurant): RequestBuilder<*>? {
            return req.clone()
        }
    }
}
