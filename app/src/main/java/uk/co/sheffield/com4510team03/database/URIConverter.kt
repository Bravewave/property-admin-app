package uk.co.sheffield.com4510team03.database

import android.net.Uri
import androidx.room.TypeConverter


object URIConverter {
    @TypeConverter
    fun toImage(uriString: String?): Uri? {
        return if (uriString == null) {
            null
        } else {
            Uri.parse(uriString)
        }
    }

    @TypeConverter
    fun toImageString(uri: Uri?): String? {
        return uri?.toString()
    }
}