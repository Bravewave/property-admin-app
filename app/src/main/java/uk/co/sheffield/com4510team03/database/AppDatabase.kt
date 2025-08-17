package uk.co.sheffield.com4510team03.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uk.co.sheffield.com4510team03.property.PropertyDAO
import uk.co.sheffield.com4510team03.property.PropertyEntity
import uk.co.sheffield.com4510team03.repair.RepairDAO
import uk.co.sheffield.com4510team03.repair.RepairEntity
import uk.co.sheffield.com4510team03.setting.SettingDAO
import uk.co.sheffield.com4510team03.setting.SettingEntity

@Database(
    entities = [PropertyEntity::class, RepairEntity::class, SettingEntity::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(URIConverter::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun propertyDAO(): PropertyDAO
    abstract fun repairDAO(): RepairDAO
    abstract fun settingDAO(): SettingDAO

    companion object {
        private const val DB_NAME = "locksmith_db"

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, DB_NAME
        )
            .fallbackToDestructiveMigration().build()

        @Volatile
        private var thisDB: AppDatabase? = null

        fun getDB(context: Context): AppDatabase =
            thisDB ?: synchronized(this) {
                thisDB ?: buildDatabase(context).also { thisDB = it }
            }
    }

}