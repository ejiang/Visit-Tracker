package io.github.ejiang.visittracker.browsevisits

import io.github.ejiang.visittracker.db.RestaurantDB

interface ReadRestaurantListener {
    fun readRestaurant(rid: String) : RestaurantDB
}
