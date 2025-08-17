package uk.co.sheffield.com4510team03.property

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import uk.co.sheffield.com4510team03.database.AppDatabase

class PropertyViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getDB(app.applicationContext).propertyDAO()

    var propertyEntityList by mutableStateOf(listOf<PropertyEntity>())
        private set

    suspend fun updatePropertyEntity(propertyEntity: PropertyEntity) {
        dao.updateOrInsert(propertyEntity)
    }

    // TODO: Change List<PropertyEntity> to Flow<List<PropertyEntity>>
    fun updateAllEntities(propertyEntities: List<PropertyEntity>) {
        propertyEntityList = propertyEntities
    }

    fun getActiveProperties(): Flow<List<PropertyEntity>> {
        return dao.getActiveProperties()
    }

    fun getArchivedProperties(): Flow<List<PropertyEntity>> {
        return dao.getArchivedProperties()
    }

    fun getProperty(propID: Int): Flow<PropertyEntity> {
        return dao.getProperty(propID)
    }
}