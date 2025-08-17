package uk.co.sheffield.com4510team03.property

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDAO {
    @Insert
    suspend fun insert(propertyEntity: PropertyEntity)

    @Update
    fun update(propertyEntity: PropertyEntity): Int

    @Transaction
    suspend fun updateOrInsert(propertyEntity: PropertyEntity) {
        val rowUpdates = update(propertyEntity)
        if (rowUpdates == 0) {
            insert(propertyEntity)
        }

    }

    @Query("SELECT * from " + PropertyEntity.TABLE_NAME)
    fun getAll(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM ${PropertyEntity.TABLE_NAME} WHERE archived = 1")
    fun getArchivedProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM ${PropertyEntity.TABLE_NAME} WHERE archived = 0")
    fun getActiveProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM ${PropertyEntity.TABLE_NAME} WHERE id = :propID LIMIT 1")
    fun getProperty(propID: Int): Flow<PropertyEntity>
}