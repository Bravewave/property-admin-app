package uk.co.sheffield.com4510team03.repair

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class ContractorInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo
    val firstName: String,
    @ColumnInfo
    val surname: String,
    @ColumnInfo
    val email: String,
    @ColumnInfo
    val services: List<String>

) {
    fun getFullName(): String {
        return "$firstName $surname"
    }
}