package uk.co.sheffield.com4510team03.property

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import uk.co.sheffield.com4510team03.ui.to3DP

@Entity(PropertyEntity.TABLE_NAME)
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val address: String,
    val telephone: String,
    val notes: String,
    val image: Uri,
    val geolocationLat: String,
    val geolocationLong: String,
    val archived: Boolean
) {
    fun getCoordinates(): String {
        return "(${to3DP(geolocationLat.toFloat())}, ${to3DP(geolocationLong.toFloat())})"
    }

    companion object {
        const val TABLE_NAME = "properties"
    }
}