package uk.co.sheffield.com4510team03.repair

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import uk.co.sheffield.com4510team03.geolocation.GeoLocation
import uk.co.sheffield.com4510team03.geolocation.GeoLocationViewModel
import uk.co.sheffield.com4510team03.maps.ShowMap
import uk.co.sheffield.com4510team03.property.PropertyViewModel
import uk.co.sheffield.com4510team03.ui.PaddedColumnText
import uk.co.sheffield.com4510team03.ui.getBitmapFromURI
import uk.co.sheffield.com4510team03.ui.to3DP
import java.io.File

@Composable
fun RepairForm(
    repairViewModel: RepairViewModel,
    propertyViewModel: PropertyViewModel,
    geolocationViewModel: GeoLocationViewModel,
    propertyId: Int,
    setPropertyId: (Int) -> Unit,
    propertyExpanded: Boolean,
    setPropertyExpanded: (Boolean) -> Unit,
    serviceExpanded: Boolean,
    setServiceExpanded: (Boolean) -> Unit,
    contractorExpanded: Boolean,
    setContractorExpanded: (Boolean) -> Unit,
    contractorId: Int,
    setContractorId: (Int) -> Unit,
    service: String,
    setService: (String) -> Unit,
    notes: String,
    setNotes: (String) -> Unit,
    image: Uri,
    setImage: (Uri) -> Unit,
    latitude: Float,
    setLatitude: (Float) -> Unit,
    longitude: Float,
    setLongitude: (Float) -> Unit,
    submitError: Boolean,
    reset: () -> Unit,
    submit: () -> Unit,
    submitEnabled: () -> Boolean = { true }
) {
    val context = LocalContext.current
    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
    var showDialog by remember { mutableStateOf(false) }
    var showMarkerDialog by remember { mutableStateOf(false) }
    var markerCallback by remember {
        mutableStateOf({
            Log.i(
                "PropertyForm",
                "Marker callback not set"
            ); showMarkerDialog = false
        })
    }
    var location by remember {
        mutableStateOf(
            GeoLocation(
                longitude.toDouble(),
                longitude.toDouble()
            )
        )
    }
    var defaultMapLocation by remember { mutableStateOf(true) }
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.i("PhotoPicker", "Selected URI: $uri")
                context.contentResolver.takePersistableUriPermission(uri, flag)
                setImage(uri)
            } else {
                setImage(Uri.EMPTY)
                Log.i("PhotoPicker", "No media selected")
            }
        }

    // Show AlertDialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Set Location") },
            text = { Text("Do you want to update the location for this property?") },
            confirmButton = {
                Button(onClick = {
                    // Handle positive action
                    setLongitude(location.longitude.toFloat())
                    setLatitude(location.latitude.toFloat())// Handle positive action
                    defaultMapLocation = true // We force the map to align to the new location
                    showDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false // Handle negative action
                }) {
                    Text("Cancel")
                }
            }
        )
    } else if (showMarkerDialog) {
        AlertDialog(
            onDismissRequest = { showMarkerDialog = false; },
            title = { Text("Tapped new map location") },
            text = { Text("Do you want to update the location for this property?") },
            confirmButton = {
                Button(onClick = {
                    markerCallback() // Handle positive action
                    showMarkerDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showMarkerDialog = false // Handle negative action
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // conditional for the text in the choose property button
    val properties = propertyViewModel.propertyEntityList
    val choosePropertyText =
        if (properties.isEmpty()) "No properties to choose"
        else properties.find { it.id == propertyId }?.name ?: "Choose Property"

    if (propertyId != -1) {
        Text(
            style = TextStyle(fontWeight = FontWeight.Bold),
            text = "Chosen Property:"
        )
    }
    // Dropdown to select property
    PaddedColumnText {
        Button(onClick = { setPropertyExpanded(true) }) { Text(choosePropertyText) }
        DropdownMenu(propertyExpanded, onDismissRequest = { setPropertyExpanded(false) }) {
            propertyViewModel.propertyEntityList.forEachIndexed { index, property ->
                DropdownMenuItem(
                    text = { Text("P${index + 1}: ${property.name}") },
                    onClick = {
                        setPropertyId(property.id)
                        setPropertyExpanded(false)
                    }
                )
            }
        }
    }

    if (propertyId == -1) {
        Text("Choose a property to fill in details")
    } else { // once a property is selected, show the rest of the form
        val properties = propertyViewModel.propertyEntityList
        val propLat = properties.find { it.id == propertyId }?.geolocationLat
        val propLong = properties.find { it.id == propertyId }?.geolocationLong
        if (propLat != null && propLong != null) {
            location = GeoLocation(propLat.toDouble(), propLong.toDouble())
        }

        if (service != "") {
            Text(
                style = TextStyle(fontWeight = FontWeight.Bold),
                text = "Selected Service:"
            )
        }

        val chooseServiceText = if (service == "") "Select Service" else service

        PaddedColumnText {
            Button(onClick = { setServiceExpanded(true) }) { Text(chooseServiceText) }
            DropdownMenu(serviceExpanded, onDismissRequest = { setServiceExpanded(false) }) {
                repairViewModel.getServices().forEach { serviceOption ->
                    DropdownMenuItem(
                        text = { Text(serviceOption) },
                        onClick = {
                            // reset contractorId so you cant go back and choose an incompatible one
                            setContractorId(-1)
                            setService(serviceOption)
                            setServiceExpanded(false)
                        }
                    )
                }
            }
        }

        if (service != "") {

            if (contractorId != -1) {
                Text(
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    text = "Selected Contractor:"
                )
            }

            val filteredContractors = repairViewModel.getContractorsByService(service)
            val chooseContractorText =
                filteredContractors.find { it.id == contractorId }?.getFullName()
                    ?: "Choose Contractor"

            PaddedColumnText {
                Button(onClick = { setContractorExpanded(true) }) { Text(chooseContractorText) }
                DropdownMenu(
                    contractorExpanded,
                    onDismissRequest = { setContractorExpanded(false) }) {
                    filteredContractors.forEach { contractor ->
                        DropdownMenuItem(
                            text = { Text(contractor.getFullName()) },
                            onClick = {
                                setContractorId(contractor.id)
                                setContractorExpanded(false)
                            }
                        )
                    }
                }
            }

            if (contractorId != -1) {

                // Notes
                PaddedColumnText {
                    TextField(
                        value = notes,
                        onValueChange = { setNotes(it) },
                        label = { Text("Notes") }
                    )
                }

                // Image Picker
                PaddedColumnText {
                    TextButton(onClick = {
                        pickMedia.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }) {
                        if (Uri.EMPTY == image) {
                            Text("Choose Image")
                        } else {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("Image Chosen: ${image.path?.let { File(it).name } ?: { "FileName NA" }}")
                                }
                                Spacer(modifier = Modifier.size(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    getBitmapFromURI(
                                        context.contentResolver,
                                        image
                                    )?.asImageBitmap()?.let {
                                        Image(
                                            bitmap = it,
                                            contentDescription = "Issue Image",
                                            modifier = Modifier.size(200.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                fun setGeolocation() {
                    Log.i("geolocationDebug", geolocationViewModel.location.toString())
                    geolocationViewModel.geolocation?.latitude?.let { setLatitude(it.toFloat()) }
                    geolocationViewModel.geolocation?.longitude?.let { setLongitude(it.toFloat()) }
                }

                val geolocationError =
                    latitude == 0f || longitude == 0f // sucks to be from null island :P
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(200.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShowMap(
                        location,
                        "",
                        "propertyAddress",
                        false,
                        defaultMapLocation,
                        { lat, long, callback ->
                            markerCallback = {
                                setLatitude(lat)
                                setLongitude(long)
                                defaultMapLocation = false
                                callback()
                            }
                            showMarkerDialog = true
                        },
                        switchCallback = {
                            defaultMapLocation = false
                        }
                    )
                }

                // Geolocation input
                PaddedColumnText {
                    val geolocationText =
                        if (geolocationError) "Coordinates not set"
                        else "(${to3DP(latitude)}, ${to3DP(longitude)})"

                    TextField(
                        modifier = Modifier.padding(5.dp),
                        value = geolocationText, onValueChange = { setGeolocation() },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Coordinates*") }, isError = geolocationError,
                        readOnly = true
                    )
                    TextButton(modifier = Modifier.padding(5.dp), onClick = {
                        if (!geolocationError) {
                            showDialog = true
                        } else {
                            setGeolocation()
                            geolocationViewModel.geolocation?.latitude?.toFloat()
                                ?.let { setLatitude(it) }
                            geolocationViewModel.geolocation?.longitude?.toFloat()
                                ?.let { setLongitude(it) }
                            defaultMapLocation =
                                true // We force the map to align to the new location
                        }
                    }) {
                        if (geolocationError) {
                            Text("Set Coordinates to Current Location")
                        } else {
                            Text("Reset Coordinates to Current Location")
                        }
                    }
                }

                // Submit and Reset buttons
                Row(Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = { reset() }) {
                        Text("Reset")
                    }
                    ElevatedButton(onClick = { submit() }, enabled = submitEnabled()) {
                        Text("Submit")
                    }
                }

                // Error message display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (submitError) {
                        Text(
                            "Error: Ensure all required fields (Property ID, Service, Contractor and Geolocation) are filled",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}