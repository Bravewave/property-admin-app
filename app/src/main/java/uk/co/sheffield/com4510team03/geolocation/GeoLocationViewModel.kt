package uk.co.sheffield.com4510team03.geolocation

import android.Manifest
import android.app.Application
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.AndroidViewModel


class GeoLocationViewModel(private val app: Application) : AndroidViewModel(app) {

    // This is the lat long data class
    var geolocation by mutableStateOf<GeoLocation?>(null)
        private set

    // This a string representation, that will change into error messages depending on the state
    var location by mutableStateOf("(checking permission)")
        private set

    var fine_permission by mutableStateOf(true)
        private set

    var coarse_permission by mutableStateOf(true)
        private set

    init {
        updateFine()
    }

    fun setGeolocation(location: Location) {
        this.geolocation = GeoLocation(location.latitude, location.longitude)
        this.location = "${location.latitude}, ${location.longitude}"
    }

    fun updateFine() {
        val allowed = ContextCompat.checkSelfPermission(
            app.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (allowed == PermissionChecker.PERMISSION_GRANTED) {
            fine_permission = true
            // This bit would be replaced after the listener receives the location
            location = "permission granted"
        } else {
            fine_permission = false
            location = "Permission denied: FINE location"
            // If we can't use fine location, we try course
        }
    }

    fun setPermissionUnavailable() {
        location = "Permission denied: FINE location"
    }


}