package io.github.ejiang.roomtests2.addvisit

import androidx.lifecycle.*
import androidx.databinding.ObservableField
import io.github.ejiang.roomtests2.db.*
import io.github.ejiang.roomtests2.db.RestTrackDB
import io.github.ejiang.roomtests2.util.Utils.Companion.calcTimeFrom
import io.github.ejiang.roomtests2.util.Utils.Companion.currencyFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// this connects to the db

class AddViewModel: ViewModel() {
    private lateinit var vdb: RestTrackDB

    var note: ObservableField<String> = ObservableField()
    var address: ObservableField<String> = ObservableField()
    var category: ObservableField<String> = ObservableField()
    var name: ObservableField<String> = ObservableField()
    var rid: ObservableField<String> = ObservableField()
    var amount: ObservableField<String> = ObservableField()
    var lat: Double = 0f.toDouble()
    var lng: Double = 0f.toDouble()
    val totalSpending: ObservableField<String> = ObservableField()
    var visitCount: ObservableField<String> = ObservableField()
    var lastVisit: ObservableField<String> = ObservableField()

    var imgUrl: String = ""

    var timestamp: MutableLiveData<Calendar> = MutableLiveData()
    var dateField: MediatorLiveData<String> = MediatorLiveData()
    var timeField: MediatorLiveData<String> = MediatorLiveData()

    private val dateFormat : DateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM)
    private val timeFormat : DateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

    fun start(vdb: RestTrackDB) {
        this.vdb = vdb

        timestamp.value = Calendar.getInstance()

        loadStats()

        // kotlin specific syntax
        dateField.addSource(timestamp) {
            val d = timestamp.value?.time
            dateField.value = dateFormat.format(d)
        }

        timeField.addSource(timestamp) {
            val d = timestamp.value?.time
            timeField.value = timeFormat.format(d)
        }
    }

    // these are functionalities
    fun saveNote(addEdit: AddEdit) {
        when (addEdit) {
            AddEdit.ADD -> {
                val note = this.note.get() ?: ""
                val address = this.address.get()!!
                val category = this.category.get()!!
                val name = this.name.get()!!
                val rid = this.rid.get()!!
                var spending = this.amount.get() ?: ""
                spending = spending.replace(".","")
                val spending2 = if (spending == "") 0 else spending.toInt()
                val lat = this.lat
                val lng = this.lng
                val date = timestamp.value?.time!!

                val v = VisitDB(note=note, timestamp=date, rid=rid, spending=spending2)
                val r = RestaurantDB(rid, name, address, lat, lng, category, imgUrl)
                saveVisit(v, r)
            }
            AddEdit.EDIT -> {
                val note = this.note.get() ?: ""
                val date = timestamp.value?.time!!
                val rid = this.rid.get()!!
                var spending = this.amount.get() ?: ""
                spending = spending.replace(".","")
                val spending2 = if (spending == "") 0 else spending.toInt()

                val v = VisitDB(note=note, timestamp=date, rid=rid, spending=spending2)
                vdb.rtDao().insertVisit(v)
            }
        }
    }

    private fun saveVisit(v: VisitDB, r: RestaurantDB) {
        if (vdb.rtDao().countRestaurant(r.rid) == 0) {
            vdb.rtDao().insertRestaurant(r)
        }
        vdb.rtDao().insertVisit(v)
    }

    fun changeTime(hourOfDay: Int, minute: Int) {
        val d = timestamp.value
        d?.set(Calendar.HOUR_OF_DAY, hourOfDay)
        d?.set(Calendar.MINUTE, minute)
        timestamp.value = d
    }

    fun changeDate(year: Int, month: Int, dayOfMonth: Int) {
        val d = timestamp.value
        d?.set(Calendar.YEAR, year)
        d?.set(Calendar.MONTH, month)
        d?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        timestamp.value = d
    }

    private fun loadStats() {
        // load count visits
        val r = rid.get()!!
        val count = vdb.rtDao().countVisits(r)
        val d1 = count.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe({ it ->
            visitCount.set(if (it > 0) it.toString() else "Never")
        }, Throwable::printStackTrace)

        // load last visited
        val lv = vdb.rtDao().lastVisit(r)
        val d2 = lv.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe( { it ->
                    // how many days was it from today
                    val days = calcTimeFrom(it.timestamp)
                    lastVisit.set("$days days ago")
                }, Throwable::printStackTrace, {
                    lastVisit.set("Never")
                })

        // load spending
        val spending = vdb.rtDao().countSpending(r)
        val d3 = spending.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe( { it ->
                    // it has to be converted
                    totalSpending.set(currencyFormat(it))
                }, Throwable::printStackTrace, {
                    totalSpending.set("$0")
                })
    }
}
