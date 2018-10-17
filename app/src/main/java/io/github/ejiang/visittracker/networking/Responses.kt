package io.github.ejiang.visittracker.networking

import com.google.gson.GsonBuilder

/* begin search results JSON */

data class RestIcon(val prefix: String, val suffix: String)

data class RestCategory(val icon: RestIcon, val name: String)

data class RestaurantLocation(val address: String?, val city: String?, val state: String?, val lat: Double, val lng: Double)

data class Restaurant(val name: String, val id: String, val categories: List<RestCategory>, val location: RestaurantLocation)

data class Venues(val venues: List<Restaurant>)

data class Resp(val response: Venues)

/* end search results JSON, begin restaurant images JSON */

data class PhotoItem(val prefix: String, val suffix: String)

data class Photos(val items: List<PhotoItem>)

data class ResponsePhotos(val photos: Photos)

data class RespPhotos(val response: ResponsePhotos)

/* end restaurant images JSON */

fun Resp.parseJSON(s: String): Resp {
    val gson = GsonBuilder().create()
    val r = gson.fromJson(s, Resp::class.java)
    return r
}

fun RestIcon.getURL() : String {
    return prefix + "bg_64" + suffix
}

fun RestaurantLocation.getAddress() : String {
    return "${address ?: ""} ${city ?: ""} ${state ?: ""}"
}

fun Restaurant.getURL() : String {
    return when (categories.isEmpty()) {
        // the default, in case it's empty
        true -> "https://ss3.4sqi.net/img/categories_v2/food/default_bg_32.png"
        false -> categories[0].icon.getURL()
    }
}

fun RespPhotos.parseJSON(s: String) : RespPhotos {
    val gson = GsonBuilder().create()
    val r = gson.fromJson(s, RespPhotos::class.java)
    return r
}

fun PhotoItem.getURL() : String {
    return prefix + "100x100" + suffix
}
