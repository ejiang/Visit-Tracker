package io.github.ejiang.roomtests2.browsevisits

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.ejiang.roomtests2.db.RestTrackDB
import io.github.ejiang.roomtests2.db.VisitDB
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class BrowseVisitsViewModel : ViewModel() {
    private val myList: MutableLiveData<List<VisitDB>> = MutableLiveData()
    private lateinit var vdb : RestTrackDB
    private lateinit var dispose : Disposable

    fun start(vdb: RestTrackDB) {
        this.vdb = vdb
        loadList()
    }

    fun getList() : MutableLiveData<List<VisitDB>> {
        return this.myList
    }

    private fun loadList() {
        dispose = this.vdb.rtDao().readAllVisitsReg()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    s -> myList.postValue(s)
                }
    }

    override fun onCleared() {
        dispose.dispose()
    }

    fun deleteVid(vid: String, rid: String) {
        val count = vdb.rtDao().countVisitsReg(rid)
        if (count == 1) {
            // then also delete restaurant afterwards
            vdb.rtDao().deleteVisits(rid)
            vdb.rtDao().deleteRestaurant(rid)
        } else { // more
            vdb.rtDao().deleteVisit(vid)
        }
        Thread.sleep(400)
        loadList()
    }
}
