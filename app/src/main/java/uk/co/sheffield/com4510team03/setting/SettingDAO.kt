package uk.co.sheffield.com4510team03.setting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDAO {
    @Insert
    suspend fun insert(repairEntity: SettingEntity)

    @Update
    fun update(repairEntity: SettingEntity): Int

    @Transaction
    suspend fun updateOrInsert(repairEntity: SettingEntity) {
        val rowUpdates = update(repairEntity)
        if (rowUpdates == 0) {
            insert(repairEntity)
        }

    }

    @Query("SELECT * from " + SettingEntity.TABLE_NAME)
    fun getAll(): Flow<List<SettingEntity>>

    @Query("SELECT COUNT(*) FROM ${SettingEntity.TABLE_NAME}")
    fun getSettingCount(): Int

}