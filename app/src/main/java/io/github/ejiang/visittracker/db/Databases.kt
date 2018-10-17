package io.github.ejiang.visittracker.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(entities = [VisitDB::class, RestaurantDB::class, ListDB::class], version = 3)
@TypeConverters(TypeConv::class)
abstract class RestTrackDB: RoomDatabase() {
    abstract fun rtDao(): RTDao

    companion object {
        // the singleton

        private var instance: RestTrackDB? = null

        fun getInstance(context: Context): RestTrackDB? {
            if (instance == null) {
                synchronized(RestTrackDB::class) {
                    instance = Room.databaseBuilder(context.applicationContext,
                            RestTrackDB::class.java, "resttrack.db")
                            .fallbackToDestructiveMigration().build()
                }
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }
    }
}
