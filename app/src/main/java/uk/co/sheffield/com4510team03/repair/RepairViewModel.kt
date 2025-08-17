package uk.co.sheffield.com4510team03.repair

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uk.co.sheffield.com4510team03.database.AppDatabase

class RepairViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getDB(app.applicationContext).repairDAO()

    var contractorEntityList by mutableStateOf(listOf<ContractorInfo>())
        private set

    fun updateContractorEntityList() {
        val contractors = mutableListOf<ContractorInfo>()
        contractors.add(
            ContractorInfo(
                1,
                "John",
                "Doe",
                "jdoe@contractor.com",
                listOf(
                    "Lock Replacement",
                    "Door Replacement",
                    "Window Replacement",
                    "Glass Replacement",
                    "Key Cutting"
                )
            )
        )
        contractors.add(
            ContractorInfo(
                2,
                "Jane",
                "Doe",
                "jane@doe.co.uk",
                listOf("Lock Replacement", "Door Replacement", "Window Replacement")
            )
        )
        contractors.add(
            ContractorInfo(
                3,
                "John",
                "Smith",
                "john@smi.th",
                listOf("Lock Replacement", "Door Replacement")
            )
        )
        contractorEntityList = contractors
    }

    // the list of services that appear in a dropdown
    fun getServices(): List<String> {
        return listOf(
            "Lock Replacement",
            "Door Replacement",
            "Window Replacement",
            "Glass Replacement",
            "Key Cutting"
        )
    }

    fun getContractorsByService(service: String): List<ContractorInfo> {
        return contractorEntityList.filter { it.services.contains(service) }
    }

    private var repairEntityList by mutableStateOf(listOf<RepairEntity>())

    suspend fun updateRepairEntity(repairEntity: RepairEntity) {
        dao.updateOrInsert(repairEntity)
    }

    // TODO: Change List<RepairEntity> to Flow<List<RepairEntity>>
    fun updateAllEntities(repairEntities: List<RepairEntity>) {
        repairEntityList = repairEntities
    }

//    fun getAllRepairs(): Flow<List<RepairEntity>> {
//        return dao.getAll()
//    }

    fun getActiveRepairs(): Flow<List<RepairEntity>> {
        // TODO: Move logic to SQL request in RepairDAO interface
        return dao.getAll().map { repairs ->
            repairs.filter { !it.repaired }
        }
    }

    fun getArchivedRepairs(): Flow<List<RepairEntity>> {
        return dao.getArchivedRepairs()
    }

    suspend fun markAsComplete(repair: RepairEntity) {
        val updatedRepair = repair.copy(repaired = true)
        updateRepairEntity(updatedRepair)
    }
}