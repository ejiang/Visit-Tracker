package io.github.ejiang.visittracker.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.ejiang.visittracker.networking.Restaurant

class SearchViewModel : ViewModel() {
    private val current: MutableLiveData<ArrayList<Restaurant>> = MutableLiveData()
    var viewState : MutableLiveData<Boolean> = MutableLiveData()

    fun getCurrent() : MutableLiveData<ArrayList<Restaurant>> {
        return current
    }

    fun changeTop(s: Restaurant) {
        current.value!![0] = s
    }

    fun change(a: ArrayList<Restaurant>) {
        current.value = a
    }
}
