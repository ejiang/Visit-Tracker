package io.github.ejiang.roomtests2.second

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.ejiang.roomtests2.db.RestTrackDB
import io.github.ejiang.roomtests2.db.RestaurantDB
import io.github.ejiang.roomtests2.db.VisitDB

class SecondViewModel : ViewModel() {
    private lateinit var vdb : RestTrackDB

    var visitsData : LiveData<List<VisitDB>> = MutableLiveData()
    var restaurantsData : LiveData<List<RestaurantDB>> = MutableLiveData()

    fun start(vdb: RestTrackDB) {
        this.vdb = vdb
        visitsData = vdb.rtDao().readAllVisits()
        restaurantsData = vdb.rtDao().readAllRestaurants()
    }

    fun saveVisit(v: VisitDB, r: RestaurantDB) {
        vdb.rtDao().insertRestaurant(r)
        vdb.rtDao().insertVisit(v)
    }
}