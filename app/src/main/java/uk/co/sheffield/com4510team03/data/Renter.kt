package uk.co.sheffield.com4510team03.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import uk.co.sheffield.com4510team03.property.PropertyEntity

@Entity(
    foreignKeys = [ForeignKey(
        entity = PropertyEntity::class,
        parentColumns = ["id"],
        childColumns = ["propertyId"]
    )]
)
data class Renter(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val propertyId: Int,
    val firstName: String,
    val surname: String,
    val telephone: String,
    val image: String
)