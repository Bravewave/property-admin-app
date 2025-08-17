package uk.co.sheffield.com4510team03.geolocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat

class GeoLocationService(context: Context, viewModel: GeoLocationViewModel) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var listener: GeoLocationListener? = null
    val locationViewModel = viewModel

    init {
        this.start()
    }

    fun start() {
        try {
            listener = GeoLocationListener()
            listener?.locationViewModel = locationViewModel
            locationManager.requestLocationUpdates(
                LocationManager.FUSED_PROVIDER,
                1000,
                0.0f,
                listener!!
            )
        } catch (ex: SecurityException) {
            listener =
                null // We set listener to null as there is no point use it without permission
            locationViewModel.setPermissionUnavailable()
        }
    }

    fun stop() {
        // We stop the updates before declaring the listener null or it may crash, or run in the background
        listener?.let { locationManager.removeUpdates(it) }
        listener = null
    }

    fun forceLastKnownLocation(): Location? {
        return try {
            val location = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
            if (location != null) {
                locationViewModel.setGeolocation(location)
            }
            location
        } catch (ex: SecurityException) {
            null
        }
    }

    fun extractLocation(): GeoLocation? {
        if (locationViewModel.fine_permission) {
            return locationViewModel.geolocation
        }
        return null
    }

    fun setModelLocation(location: Location) {
        locationViewModel.setGeolocation(location)
    }

    fun Context.hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

}