package uk.co.sheffield.com4510team03.repair

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import uk.co.sheffield.com4510team03.property.PropertyEntity
import java.util.Date


@Entity(
    tableName = RepairEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"], // Referencing the id field from PropertyEntity
            childColumns = ["propertyId"],
            onDelete = ForeignKey.CASCADE // When a PropertyEntity is deleted, all related RepairEntity rows will be deleted
        )
    ]
)
data class RepairEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val propertyId: Int, // ForeignKey for parent property
    val contractorId: Int,
    val requestDate: Date, // Set by default to be the time when the request was made
    val service: String,
    val notes: String,
    val image: Uri, // Links to the image resource on the users device
    val latitude: Float,
    val longitude: Float,
    val repaired: Boolean, // False by default, can be flagged to true by landlord
) {
    fun getCoordinates(): String {
        return "($latitude, $longitude)"
    }

    companion object {
        const val TABLE_NAME = "repairs"
    }
}