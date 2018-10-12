package io.github.ejiang.roomtests2.networking

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SearchRepo(private val searchAPI: SearchAPI) {

    // the return value has to be some observable, or whatever
    fun getMovies(query: String, location: String, limit: String) : Observable<Resp> {
        val o: Observable<Resp> = searchAPI.getRestaurant(location, "browse", query, limit)
        return o
    }

    fun getPhoto(rid: String) : Observable<RespPhotos> {
        val o: Observable<RespPhotos> = searchAPI.getImage(rid, limit="1")
        return o
    }

    companion object {
        fun getRepo() : SearchRepo {
            val i : Interceptor = RespInterceptor()
            val client : OkHttpClient = OkHttpClient.Builder()
                                            .addInterceptor(i).build()
            val retrofit : Retrofit = Retrofit.Builder().client(client)
                    .baseUrl("https://api.foursquare.com/v2/venues/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            val searchAPI : SearchAPI = retrofit.create(SearchAPI::class.java)
            val repo = SearchRepo(searchAPI)
            return repo
        }
    }
}
