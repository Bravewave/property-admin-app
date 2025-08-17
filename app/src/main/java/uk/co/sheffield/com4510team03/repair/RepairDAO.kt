package uk.co.sheffield.com4510team03.repair

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairDAO {
    @Insert
    suspend fun insert(repairEntity: RepairEntity)

    @Update
    fun update(repairEntity: RepairEntity): Int

    @Transaction
    suspend fun updateOrInsert(repairEntity: RepairEntity) {
        val rowUpdates = update(repairEntity)
        if (rowUpdates == 0) {
            insert(repairEntity)
        }

    }

    @Query("SELECT * from " + RepairEntity.TABLE_NAME)
    fun getAll(): Flow<List<RepairEntity>>

    @Query("SELECT * FROM ${RepairEntity.TABLE_NAME} WHERE repaired = 1")
    fun getArchivedRepairs(): Flow<List<RepairEntity>>
}