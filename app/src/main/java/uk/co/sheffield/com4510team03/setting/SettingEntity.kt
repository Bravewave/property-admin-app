package uk.co.sheffield.com4510team03.setting

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(SettingEntity.TABLE_NAME)
data class SettingEntity(
    @PrimaryKey
    val key: String,
    val stringVal: String,
) {
    companion object {
        const val TABLE_NAME = "settings"
    }
}