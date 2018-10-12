package io.github.ejiang.roomtests2.networking

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchAPI {
    @GET("search")
    fun getRestaurant(@Query("near") near: String,
                      @Query("intent") intent: String,
                      @Query("query") query: String,
                      @Query("limit") limit: String): Observable<Resp>


    @GET("{venue}/photos")
    fun getImage(@Path("venue") venue: String,
                 @Query("limit") limit: String): Observable<RespPhotos>
}
