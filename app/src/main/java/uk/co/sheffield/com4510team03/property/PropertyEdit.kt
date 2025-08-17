package uk.co.sheffield.com4510team03.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
fun PropertyEdit(
    propertyViewModel: PropertyViewModel,
    geolocationViewModel: GeoLocationViewModel,
    onClose: () -> Unit,
    selectedPropertyID: Int
) {
    val selectedProperty by propertyViewModel.getProperty(selectedPropertyID)
        .collectAsState(initial = null)

    if (selectedProperty != null) {

        val (propID, setPropID) = rememberSaveable { mutableIntStateOf(selectedProperty!!.id) }
        val (propName, setPropName) = rememberSaveable { mutableStateOf(selectedProperty!!.name) }
        val (propAddress, setPropAddress) = rememberSaveable { mutableStateOf(selectedProperty!!.address) }
        val (propTelephone, setPropTelephone) = rememberSaveable { mutableStateOf(selectedProperty!!.telephone) }
        val (propNotes, setPropNotes) = rememberSaveable { mutableStateOf(selectedProperty!!.notes) }
        val (propImage, setPropImage) = rememberSaveable { mutableStateOf(selectedProperty!!.image) }
        val (propGeolocationLat, setPropGeolocationLat) = rememberSaveable {
            mutableStateOf(
                selectedProperty!!.geolocationLat
            )
        }
        val (propGeolocationLong, setPropGeolocationLong) = rememberSaveable {
            mutableStateOf(
                selectedProperty!!.geolocationLong
            )
        }
        val (propArchived, setPropArchived) = rememberSaveable { mutableStateOf(selectedProperty!!.archived) }

        val (submitError, setSubmitError) = rememberSaveable { mutableStateOf(false) }

        val propertyEntityScope = rememberCoroutineScope()

        fun reset() {
            setPropID(selectedProperty!!.id)
            setPropName(selectedProperty!!.name)
            setPropAddress(selectedProperty!!.address)
            setPropTelephone(selectedProperty!!.telephone)
            setPropNotes(selectedProperty!!.notes)
            setPropImage(selectedProperty!!.image)
            setPropGeolocationLat(selectedProperty!!.geolocationLat)
            setPropGeolocationLong(selectedProperty!!.geolocationLong)
            setPropArchived(selectedProperty!!.archived)
            setSubmitError(false)
        }

        fun submit(propertyEntityScope: CoroutineScope) {
            if (propName.isEmpty() || propAddress.isEmpty() || propTelephone.isEmpty() || propGeolocationLat.isEmpty() || propGeolocationLong.isEmpty()) {
                setSubmitError(true)
            } else {
                setSubmitError(false)
                val newPropertyEntity = PropertyEntity(
                    propID,
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
                    onClose()
                }
            }
        }

        fun setPropGeolocation(): GeoLocation {
            setPropGeolocationLat(geolocationViewModel.geolocation?.latitude.toString())
            setPropGeolocationLong(geolocationViewModel.geolocation?.longitude.toString())
            return GeoLocation(
                geolocationViewModel.geolocation?.latitude!!,
                geolocationViewModel.geolocation?.longitude!!
            )
        }

        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Text(
                "Editing property: ${selectedProperty!!.name}",
                style = MaterialTheme.typography.titleMedium
            )

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
                submit = { submit(propertyEntityScope) },
                submitEnabled = { true } // Param can now be removed, left here until PropertyForm refactor
            )
        }
    } else {
        Text("Loading ...")
    }
}