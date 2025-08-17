package uk.co.sheffield.com4510team03.maps

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import uk.co.sheffield.com4510team03.geolocation.GeoLocation

@Composable
fun ShowMap(
    location: GeoLocation,
    title: String,
    snippet: String,
    staticMap: Boolean,
    default: Boolean,
    updateCallback: (Float, Float, () -> Boolean) -> Unit,
    switchCallback: () -> Unit
): Unit {
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.SATELLITE))
    }
    var isDragging by remember { mutableStateOf(false) }
    var locationLng = LatLng(location.latitude, location.longitude)
    val locationMarkerState = rememberMarkerState(position = locationLng)
    var cameraPosition = LatLng(location.latitude, location.longitude)
    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            if (default) LatLng(
                location.latitude,
                location.longitude
            ) else cameraPosition, 15f
        )
    }

    fun changeMarker(lat: Float, lng: Float) {
        locationLng = LatLng(lat.toDouble(), lng.toDouble())
        locationMarkerState.position = locationLng
    }

    Box(
        modifier = Modifier
            .fillMaxSize(1f)
            .zIndex(1f)
    ) {
        GoogleMap(
            // Had to override the pointerInput to allow for dragging the map and to prevent it from dragging the hamburger menu and the ModalBottomSheet
            // https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures
            modifier = Modifier
                .fillMaxSize(1f)
                .zIndex(2f)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        var newPosition = cameraPositionState.position.target
                        var tilt = cameraPositionState.position.tilt
                        var zoom = cameraPositionState.position.zoom
                        var bearing = cameraPositionState.position.bearing
                        var zoomMultiplier = (Math.pow(zoom.toDouble(), 4.2) / 10)
                        newPosition = LatLng(
                            newPosition.latitude + (dragAmount.y / zoomMultiplier),
                            newPosition.longitude - (dragAmount.x / zoomMultiplier)
                        )
                        cameraPosition = newPosition
                        cameraPositionState.position =
                            CameraPosition(newPosition, zoom, tilt, bearing)
                        switchCallback()
                        change.consume()
                    }
                },
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng: LatLng ->
                Log.d("Map", "Map clicked at ${latLng.latitude}, ${latLng.longitude}")
                if (!staticMap) {
                    // This asks the user if they wish to update the location, which will change the marker
                    updateCallback(
                        latLng.latitude.toFloat(),
                        latLng.longitude.toFloat()
                    ) {
                        changeMarker(latLng.latitude.toFloat(), latLng.longitude.toFloat())
                        true
                    }

                }
            }
        )
        {
            Marker(
                state = locationMarkerState,
                title = title,
                snippet = snippet
            )
        }
    }



    fun updateLocation() {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            if (default) LatLng(
                location.latitude,
                location.longitude
            ) else cameraPosition, 15f
        )
        if (default) {
            locationLng = LatLng(location.latitude, location.longitude)
            locationMarkerState.position = locationLng
        }
    }

    // Found LaunchedEffect from Compose docs
    // https://developer.android.com/develop/ui/compose/side-effects

    LaunchedEffect(default) {
        updateLocation()
    }

    LaunchedEffect(cameraPosition) {
        updateLocation()
    }

    LaunchedEffect(location) {
        updateLocation()
    }
}

