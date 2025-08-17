package uk.co.sheffield.com4510team03.geolocation

import android.location.Location
import android.location.LocationListener

class GeoLocationListener : LocationListener {
    var locationViewModel: GeoLocationViewModel? = null

    override fun onLocationChanged(newlocation: Location) {
        locationViewModel?.setGeolocation(newlocation)
    }
}