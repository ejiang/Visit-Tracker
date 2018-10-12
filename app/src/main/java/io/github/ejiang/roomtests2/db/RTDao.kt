package io.github.ejiang.roomtests2.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface RTDao {
    @Query("SELECT * from visits")
    fun readAllVisits(): LiveData<List<VisitDB>>

    @Query("select * from visits order by datetime(timestamp) desc")
    fun readAllVisitsReg(): Flowable<List<VisitDB>>

    @Query("SELECT * from visits")
    fun readAllVisitsLive(): DataSource.Factory<Int, VisitDB> // for pagedlist

    @Query("SELECT * from restaurants")
    fun readAllRestaurants(): LiveData<List<RestaurantDB>>

    @Query("select * from restaurants")
    fun readAllRestaurantsLive(): DataSource.Factory<Int, RestaurantDB>

    @Query("select * from restaurants")
    fun readAllRestaurantsRx() : Flowable<List<RestaurantDB>>

    @Query("select * from restaurants where rid = :rid limit 1")
    fun readRestaurant(rid: String) : Flowable<RestaurantDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurant(r: RestaurantDB)

    @Insert
    fun insertVisit(v: VisitDB)

    @Query("SELECT * from visits where visit_id = :vid limit 1")
    fun readVisit(vid: Long): Flowable<VisitDB>

    @Query("DELETE FROM visits")
    fun nukeTable()

    /*@Query("select count(1) from visits where rid = :rid")
    fun countVisits(rid: String)*/

    @Query("select * from visits group by rid order by count(rid) desc")
    fun mostVisited() : Flowable<VisitDB>

    // may need to format timestamp
    @Query("select * from visits group by rid order by datetime(timestamp)")
    fun recentVisited() : Flowable<VisitDB>

    /*@Query("select * from visits inner join restaurants on rid")
    fun getVisitsWithNames() : Flowable<VisitDB>*/

    @Query("select count(1) from visits where rid = :rid")
    fun countVisits(rid: String) : Single<Int>

    @Query("select count(1) from visits where rid = :rid")
    fun countVisitsReg(rid: String) : Int

    @Query("select * from visits where rid = :rid order by datetime(timestamp) desc limit 1")
    fun lastVisit(rid: String) : Maybe<VisitDB>

    @Query("select sum(spending) from visits where rid = :rid")
    fun countSpending(rid: String) : Maybe<Int>

    @Query("select count(1) from restaurants where rid = :rid")
    fun countRestaurant(rid: String): Int

    @Query("select *, max(datetime(timestamp)) as ts from visits group by rid order by rid")
    fun readMostRecentVisits() : Flowable<List<VisitWithTS>>

    @Query("select *, count(1) as counted from visits group by rid order by rid")
    fun countAllRestaurants() : Flowable<List<VisitWithCount>>

    @Query("select *, sum(spending) as totalspending from visits group by rid order by rid")
    fun readAllSpending() : Flowable<List<VisitWithSpending>>

    @Query("select *, max(datetime(timestamp)) as ts, count(1) as counted, sum(spending) as totalspending from visits group by rid")
    fun readAll() : Flowable<List<Everything>>

    @Query("delete from visits where rid = :rid")
    fun deleteVisits(rid: String)

    @Query("delete from restaurants where rid = :rid")
    fun deleteRestaurant(rid: String)

    @Query("delete from visits where visit_id = :vid")
    fun deleteVisit(vid: String)
}
