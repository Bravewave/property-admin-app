package uk.co.sheffield.com4510team03.property

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uk.co.sheffield.com4510team03.geolocation.GeoLocation
import uk.co.sheffield.com4510team03.geolocation.GeoLocationViewModel

@Composable
fun PropertyAdd(
    propertyViewModel: PropertyViewModel,
    geolocationViewModel: GeoLocationViewModel,
    onClose: () -> Unit
) {
    val (propName, setPropName) = rememberSaveable { mutableStateOf("") }
    val (propAddress, setPropAddress) = rememberSaveable { mutableStateOf("") }
    val (propTelephone, setPropTelephone) = rememberSaveable { mutableStateOf("") }
    val (propNotes, setPropNotes) = rememberSaveable { mutableStateOf("") }
    val (propImage, setPropImage) = rememberSaveable { mutableStateOf<Uri>(Uri.EMPTY) }
    val (propGeolocationLat, setPropGeolocationLat) = rememberSaveable { mutableStateOf("") }
    val (propGeolocationLong, setPropGeolocationLong) = rememberSaveable { mutableStateOf("") }
    val (propArchived, setPropArchived) = rememberSaveable { mutableStateOf(false) }

    val (submitError, setSubmitError) = rememberSaveable { mutableStateOf(false) }
    val (geolocationError, setGeolocationError) = rememberSaveable { mutableStateOf(false) }

    val propertyEntityScope = rememberCoroutineScope()

    fun reset() {
        setPropName("")
        setPropAddress("")
        setPropTelephone("")
        setPropNotes("")
        setPropImage(Uri.EMPTY)
        setPropGeolocationLat("")
        setPropGeolocationLong("")
        setPropArchived(false)
        setSubmitError(false)
    }

    fun submit(propertyEntityScope: CoroutineScope) {
        if (propName.isEmpty() || propAddress.isEmpty() || propTelephone.isEmpty() || propGeolocationLat.isEmpty() || propGeolocationLong.isEmpty()) {
            setSubmitError(true)
        } else {
            setSubmitError(false)
            val newPropertyEntity = PropertyEntity(
                0,
                propName,
                propAddress,
                propTelephone,
                propNotes,
                propImage,
                propGeolocationLat,
                propGeolocationLong,
                propArchived
            )
            propertyEntityScope.launch {
                propertyViewModel.updatePropertyEntity(newPropertyEntity)
            }
            reset()
            onClose()
        }
    }

    fun setPropGeolocation(): GeoLocation {
        Log.i("AriDebug", geolocationViewModel.location.toString())

        if (geolocationViewModel.geolocation == null) {
            setGeolocationError(true)
            return GeoLocation(0.0, 0.0)
        } else {
            setPropGeolocationLat(geolocationViewModel.geolocation?.latitude.toString())
            setPropGeolocationLong(geolocationViewModel.geolocation?.longitude.toString())
            return GeoLocation(
                geolocationViewModel.geolocation?.latitude!!,
                geolocationViewModel.geolocation?.longitude!!
            )
        }
    }

    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
        Text("Please fill in details")
        PropertyForm(
            propName,
            setPropName,
            propAddress,
            setPropAddress,
            propTelephone,
            setPropTelephone,
            propNotes,
            setPropNotes,
            propImage,
            setPropImage,
            propGeolocationLat,
            setPropGeolocationLat,
            propGeolocationLong,
            setPropGeolocationLong,
            setGeolocation = { setPropGeolocation() },
            propArchived,
            setPropArchived,
            submitError,
            reset = { reset() },
            submit = { submit(propertyEntityScope) }
        )
    }

    if (geolocationError) {
        AlertDialog(
            onDismissRequest = { setGeolocationError(false) },
            confirmButton = {
                TextButton(onClick = { setGeolocationError(false) }) { Text("OK") }
            },
            title = { Text("Geolocation Error") },
            text = { Text("Unable to retrieve your location. Please try again.") }
        )
    }
}