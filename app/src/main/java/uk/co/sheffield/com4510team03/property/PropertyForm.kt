package uk.co.sheffield.com4510team03.property

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import uk.co.sheffield.com4510team03.geolocation.GeoLocation
import uk.co.sheffield.com4510team03.maps.ShowMap
import uk.co.sheffield.com4510team03.ui.PaddedColumnText
import uk.co.sheffield.com4510team03.ui.getBitmapFromURI
import uk.co.sheffield.com4510team03.ui.to3DP
import java.io.File

// https://developer.android.com/develop/ui/views/components/dialogs
// Code from https://developer.android.com/develop/ui/views/components/dialogs#PassingEvents docs


@Composable
fun PropertyForm(
    propName: String,
    setPropName: (String) -> Unit,
    propAddress: String,
    setPropAddress: (String) -> Unit,
    propTelephone: String,
    setPropTelephone: (String) -> Unit,
    propNotes: String,
    setPropNotes: (String) -> Unit,
    propImage: Uri,
    setPropImage: (Uri) -> Unit,
    propGeolocationLat: String,
    setPropGeolocationLat: (String) -> Unit,
    propGeolocationLong: String,
    setPropGeolocationLong: (String) -> Unit,
    setGeolocation: () -> GeoLocation,
    propArchived: Boolean,
    setPropArchived: (Boolean) -> Unit,
    submitError: Boolean,
    reset: () -> Unit,
    submit: () -> Unit,
    submitEnabled: () -> Boolean = { true }
) {
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
                propGeolocationLat.toDoubleOrNull() ?: 0.0,
                propGeolocationLong.toDoubleOrNull() ?: 0.0
            )
        )
    }
    val context = LocalContext.current
    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
    var defaultMapLocation by remember { mutableStateOf(true) } // This is to decide whether it is aligned to the new location or to the property/current location
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.i("PhotoPicker", "Selected URI: $uri")
            context.contentResolver.takePersistableUriPermission(uri, flag)
            setPropImage(uri)
        } else {
            setPropImage(Uri.EMPTY)
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
                    location = setGeolocation() // Handle positive action
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

    PaddedColumnText {
        TextField(
            value = propName, onValueChange = { setPropName(it) },
            label = { Text("Property Name*") }, isError = propName.isEmpty()
        )
    }

    PaddedColumnText {
        TextField(
            value = propAddress, onValueChange = { setPropAddress(it) },
            label = { Text("Address*") }, isError = propAddress.isEmpty()
        )
    }

    PaddedColumnText {
        TextField(
            value = propTelephone, onValueChange = { setPropTelephone(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            label = { Text("Telephone*") }, isError = propTelephone.isEmpty()
        )
    }
    PaddedColumnText {
        TextField(value = propNotes, onValueChange = { setPropNotes(it) },
            label = { Text("Notes") })
    }
    PaddedColumnText {
        TextButton(onClick = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) }) {
            if (Uri.EMPTY == propImage) {
                Text("Choose Image")
            } else {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("Image Chosen: ${propImage.path?.let { File(it).name } ?: { "FileName NA" }}")
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        getBitmapFromURI(context.contentResolver, propImage)?.asImageBitmap()?.let {
                            Image(
                                bitmap = it,
                                contentDescription = "Property Image",
                                modifier = Modifier.size(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

            }
        }
    }
    val geolocationError = propGeolocationLong.isEmpty() || propGeolocationLat.isEmpty()
    val propertyName by remember { mutableStateOf(propName) }
    val propertyAddress by remember { mutableStateOf(propAddress) }
    Row(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(200.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShowMap(
            location,
            propertyName,
            propertyAddress,
            false,
            defaultMapLocation,
            { lat, long, callback ->
                markerCallback = {
                    setPropGeolocationLat(lat.toString())
                    setPropGeolocationLong(long.toString())
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
            else "(${to3DP(propGeolocationLat.toFloat())}, ${to3DP(propGeolocationLong.toFloat())})"

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
                location = setGeolocation()
                defaultMapLocation = true // We force the map to align to the new location
            }
        }) {
            if (geolocationError) {
                Text("Set Coordinates to Current Location")
            } else {
                Text("Reset Coordinates to Current Location")
            }
        }
    }

    Row(
        Modifier.fillMaxWidth(0.68f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Archive Status:")
        Spacer(modifier = Modifier.size(8.dp))
        Switch(checked = propArchived, onCheckedChange = { setPropArchived(it) })
    }

    Row(Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.SpaceBetween) {
        OutlinedButton(onClick = {
            reset()
        }) {
            Text("Reset")
        }
        ElevatedButton(onClick = {
            submit()
        }, enabled = submitEnabled()) {
            Text("Submit")
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (submitError) {
            Text(
                "Error: Ensure Title, Address, Telephone, and Geolocation are filled",
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}