package uk.co.sheffield.com4510team03.property

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import uk.co.sheffield.com4510team03.geolocation.GeoLocationViewModel
import uk.co.sheffield.com4510team03.ui.modalClose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyContent(
    screen: PropertyNavEnum,
    propertyViewModel: PropertyViewModel,
    geolocationViewModel: GeoLocationViewModel
) {
    val propertyScope = rememberCoroutineScope()
    val (selectedProperty, setSelectedProperty) = rememberSaveable { mutableStateOf<Int?>(null) }
    val (showModal, setShowModal) = rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        // Floating button for adding a new property
        floatingActionButton = {
            if (screen == PropertyNavEnum.HOMES) {
                FloatingActionButton(onClick = {
                    setSelectedProperty(null)
                    setShowModal(true)
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Property")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            val activeProperties by propertyViewModel.getActiveProperties()
                .collectAsState(initial = emptyList())
            val archivedProperties by propertyViewModel.getArchivedProperties()
                .collectAsState(initial = emptyList())
            when (screen) {
                PropertyNavEnum.HOMES -> PropertyShow(
                    activeProperties, "Active", onEdit = { property ->
                        setSelectedProperty(property.id)
                        setShowModal(true)
                    }
                )

                PropertyNavEnum.ARCHIVED -> PropertyShow(
                    archivedProperties, "Archived", onEdit = { property ->
                        setSelectedProperty(property.id)
                        setShowModal(true)
                    }
                )
            }
        }
    }

    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = {
                setShowModal(false)
                setSelectedProperty(null)
            },
            sheetState = sheetState
        ) {
            if (selectedProperty == null) {
                PropertyAdd(
                    propertyViewModel = propertyViewModel,
                    geolocationViewModel = geolocationViewModel,
                    onClose = {
                        modalClose(
                            propertyScope,
                            sheetState,
                            setShowModal
                        ) { setSelectedProperty(null) }
                    }
                )
            } else {
                PropertyEdit(
                    propertyViewModel = propertyViewModel,
                    geolocationViewModel = geolocationViewModel,
                    selectedPropertyID = selectedProperty,
                    onClose = {
                        modalClose(
                            propertyScope,
                            sheetState,
                            setShowModal
                        ) { setSelectedProperty(null) }
                    }
                )
            }
        }
    }
}

