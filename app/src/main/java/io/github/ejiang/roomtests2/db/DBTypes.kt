package io.github.ejiang.roomtests2.db

import android.util.Log
import androidx.room.*
import androidx.annotation.NonNull
import java.text.SimpleDateFormat
import java.util.*

object TypeConv {
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    @TypeConverter
    @JvmStatic
    fun toDate(v: String?): Date? {
        return formatter.parse(v)
    }

    @TypeConverter
    @JvmStatic
    fun fromDate(v: Date?): String? {
        return formatter.format(v)
    }
}

@Entity(tableName = "visits",
        foreignKeys = [ForeignKey(entity = RestaurantDB::class,
                parentColumns = ["rid"],
                childColumns =  ["rid"],
                onDelete = ForeignKey.CASCADE)])
data class VisitDB(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "visit_id")
        var vid: Long = 0,
        @NonNull
        var note: String,
        @NonNull
        var timestamp: Date,
        @NonNull
        var rid: String, // foreign key
        @NonNull
        var spending: Int // cents
)

@Entity(tableName = "restaurants")
data class RestaurantDB(
        @PrimaryKey
        @NonNull
        var rid: String,
        @NonNull
        var name: String,
        @NonNull
        var address: String,
        @NonNull
        var lat: Double,
        @NonNull
        var lng: Double,
        @NonNull
        var category: String,
        @NonNull
        var img_url: String
)

// "arbitrary POJO"
data class VisitWithTS(
        @Embedded
        val visit: VisitDB,
        @ColumnInfo(name="ts")
        val ts: Date
)

data class VisitWithCount(
        @Embedded
        val visit: VisitDB,
        @ColumnInfo(name="counted")
        val counted: Int
)

data class VisitWithSpending(
        @Embedded
        val visit: VisitDB,
        @ColumnInfo(name="totalspending")
        val totalspending: Int
)

data class RestaurantCardDB(
        var rid: String,
        var ts: Date?,
        var counted: Int?,
        var totalspending: Int?
)

data class Everything(
        @Embedded
        val visit: VisitDB,
        @ColumnInfo(name="counted")
        val counted: Int,
        @ColumnInfo(name="totalspending")
        val totalspending: Int,
        @ColumnInfo(name="ts")
        val ts: Date
)

@Entity(tableName = "lists")
data class ListDB(
        @PrimaryKey(autoGenerate = true)
        var lid: Long,
        @NonNull
        var name: String,
        @NonNull
        var restaurants: String
)
