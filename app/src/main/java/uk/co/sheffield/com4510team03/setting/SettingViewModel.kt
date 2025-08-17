package uk.co.sheffield.com4510team03.setting

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.co.sheffield.com4510team03.database.AppDatabase

class SettingViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDB(app.applicationContext).settingDAO()

    var name by mutableStateOf("John Doe")
        private set

    var contactNumber by mutableStateOf("")

    var signature by mutableStateOf("Thank you for your help")

    fun updateNameNonSuspended(nameVal: String) {
        viewModelScope.launch {
            updateName(nameVal)
        }
    }

    fun updatePhoneNumberNonSuspended(numberVal: String) {
        viewModelScope.launch {
            updateNumber(numberVal)
        }
    }

    suspend fun updateName(nameVal: String) {
        name = nameVal
        dao.updateOrInsert(SettingEntity(key = NAME, stringVal = name))
    }

    suspend fun updateNumber(numberVal: String) {
        contactNumber = numberVal
        dao.updateOrInsert(SettingEntity(key = CONTACTNUMBER, stringVal = contactNumber))
    }

    suspend fun updateSignature(signatureVal: String) {
        signature = signatureVal
        dao.updateOrInsert(SettingEntity(key = SIGNATURE, stringVal = signature))
    }

    fun updateSignatureNonSuspended(signatureVal: String) {
        viewModelScope.launch {
            updateSignature(signatureVal)
        }
    }

    //getN

    suspend fun updateSettings(settingEntity: List<SettingEntity>) {
        settingEntity.forEach { entity ->
            when (entity.key) {
                NAME -> {
                    updateName(entity.stringVal)
                }

                CONTACTNUMBER -> {
                    updateNumber(entity.stringVal)
                }

                SIGNATURE -> {
                    updateSignature(entity.stringVal)
                }

                else -> Log.w("SettingsViewModel", "Unknown setting: ${entity.key}")
            }
        }
    }

    companion object {
        const val NAME = "NAME"
        const val CONTACTNUMBER = "CONTACTNUMBER"
        const val SIGNATURE = "SIGNATURE"
    }
}