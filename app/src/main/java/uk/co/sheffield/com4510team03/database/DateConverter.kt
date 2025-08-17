package uk.co.sheffield.com4510team03.database


import androidx.room.TypeConverter
import java.util.Date

class DateConverter {

    @TypeConverter
    fun fromDateToLong(date: Date?): Long? {
        return date?.time // Convert Date to timestamp (milliseconds)
    }

    @TypeConverter
    fun fromLongToDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) } // Convert timestamp back to Date
    }
}
