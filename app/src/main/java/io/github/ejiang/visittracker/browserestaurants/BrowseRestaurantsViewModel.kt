package io.github.ejiang.visittracker.browserestaurants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import io.github.ejiang.visittracker.db.*
import io.github.ejiang.visittracker.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.schedulers.Schedulers
import java.util.*

typealias RestCardList = List<RestCard>

enum class Selector {
    DAYS_AGO, SPENT, VISITS
}

class BrowseRestaurantsViewModel : ViewModel() {
    private lateinit var vdb : RestTrackDB
    private lateinit var dao : RTDao

    private var myList : MutableLiveData<RestCardList> = MutableLiveData()

    var selector : Selector = Selector.DAYS_AGO
    var regularOrder : Boolean = true

    private lateinit var dispose : Disposable

    fun start(vdb: RestTrackDB) {
        this.vdb = vdb
        this.dao = vdb.rtDao()
        loadList()
    }

    private fun daysAgoSelector(rc: RestCard) : Int = rc.daysAgo.toInt()

    private fun spentSelector(rc: RestCard) : Int = -Utils.reverseCurrency(rc.spent)

    private fun visitsSelector(rc: RestCard) : Int = -rc.visits.toInt()

    private fun loadList() {
        val allRest = this.dao.readAllRestaurantsRx()
        val alls = this.dao.readAll()
        val d = allRest.combineLatest(alls)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())


        dispose = d.subscribe {
                    val (rl, every) = it
                    if (rl.size != every.size) {
                        // then wait for the next one
                        return@subscribe
                    }

                    // turn restaurants into hashmap
                    val hs = HashMap<String, RestaurantDB>()
                    for (rest in rl) {
                        hs[rest.rid] = rest
                    }

                    val ls = LinkedList<RestCard>()
                    for (e in every) {
                        val rid = e.visit.rid
                        val rest = hs[rid]
                        val imgurl = rest?.img_url
                        val address = rest?.address
                        val name = rest?.name
                        val category = rest?.category

                        val daysAgo = Utils.calcTimeFrom(e.ts).toString()
                        val spent = Utils.currencyFormat(e.totalspending)
                        val visits = e.counted.toString()

                        val rc = RestCard(rid, imgurl!!, address!!, name!!, category!!, daysAgo, spent, visits)
                        ls.add(rc)
                    }

                    // default sort is last visited
                    val tmp : LinkedList<RestCard> = LinkedList(ls)
                    when (selector) {
                        Selector.DAYS_AGO -> tmp.sortBy(::daysAgoSelector)
                        Selector.SPENT -> tmp.sortBy(::spentSelector)
                        Selector.VISITS -> tmp.sortBy(::visitsSelector)
                    }
                    myList.postValue(if (regularOrder) tmp else tmp.asReversed())
                }
    }

    fun getList() : MutableLiveData<RestCardList> {
        return myList
    }

    fun flipOrder() {
        regularOrder = !regularOrder
        val lis = myList.value
        myList.postValue(lis?.asReversed())
    }

    override fun onCleared() {
        dispose.dispose()
    }

    fun changeSelector(selector: Selector, regularOrder: Boolean) {
        this.selector = selector
        this.regularOrder = regularOrder

        val lis = myList.value
        val tmp : LinkedList<RestCard> = LinkedList(lis)
        when (selector) {
            Selector.DAYS_AGO -> tmp.sortBy(::daysAgoSelector)
            Selector.SPENT -> tmp.sortBy(::spentSelector)
            Selector.VISITS -> tmp.sortBy(::visitsSelector)
        }
        myList.postValue(if (regularOrder) tmp else tmp.asReversed())
    }

    fun deleteRid(rid: String) {
        dao.deleteVisits(rid)
        dao.deleteRestaurant(rid)
        Thread.sleep(200)
        loadList()
    }
}