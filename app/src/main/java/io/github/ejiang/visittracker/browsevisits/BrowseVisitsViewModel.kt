package io.github.ejiang.visittracker.browsevisits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.ejiang.visittracker.db.RestTrackDB
import io.github.ejiang.visittracker.db.VisitDB
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class BrowseVisitsViewModel : ViewModel() {
    private val myList: MutableLiveData<List<VisitDB>> = MutableLiveData()
    private lateinit var vdb : RestTrackDB
    private lateinit var dispose : Disposable

    private var rid = ""

    fun start(vdb: RestTrackDB, rid: String) {
        this.vdb = vdb
        this.rid = rid
        loadList(rid)
    }

    fun start(vdb: RestTrackDB) {
        this.vdb = vdb
        loadList()
    }

    fun getList() : LiveData<List<VisitDB>> {
        return this.myList
    }

    private fun loadList(rid: String) {
        dispose = this.vdb.rtDao().readVisits(rid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    s -> myList.postValue(s)
                }
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
            // then delete restaurant afterwards; cascade
            vdb.rtDao().deleteRestaurant(rid)
        } else { // more
            vdb.rtDao().deleteVisit(vid)
        }
        Thread.sleep(200)

        /*
        if (this.rid.isNotBlank()) {
            loadList(this.rid)
        } else {
            loadList()
        }*/
    }
}
