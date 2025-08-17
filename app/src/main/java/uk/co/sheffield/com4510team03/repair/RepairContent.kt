package uk.co.sheffield.com4510team03.repair

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
import uk.co.sheffield.com4510team03.property.PropertyViewModel
import uk.co.sheffield.com4510team03.setting.SettingViewModel
import uk.co.sheffield.com4510team03.ui.modalClose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairContent(
    screen: RepairNavEnum,
    repairViewModel: RepairViewModel,
    propertyViewModel: PropertyViewModel,
    geolocationViewModel: GeoLocationViewModel,
    settingViewModel: SettingViewModel
) {
    val repairAddScope = rememberCoroutineScope()
    val (showRepairAdd, setShowRepairAdd) = rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        // Floating button for adding a new property
        floatingActionButton = {
            if (screen == RepairNavEnum.SHOW) {
                FloatingActionButton(onClick = {
                    setShowRepairAdd(true)
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Repair")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            val activeRepairs by repairViewModel.getActiveRepairs()
                .collectAsState(initial = emptyList())
            val archivedRepairs by repairViewModel.getArchivedRepairs()
                .collectAsState(initial = emptyList())
            when (screen) {
                RepairNavEnum.SHOW -> RepairShow(
                    activeRepairs,
                    "Active",
                    repairViewModel,
                    propertyViewModel,
                    settingViewModel
                )

                RepairNavEnum.CONTRACTORS_DETAILS -> ContractorsShow(repairViewModel)
                RepairNavEnum.REPAIRED -> RepairShow(
                    archivedRepairs,
                    "Archived",
                    repairViewModel,
                    propertyViewModel,
                    settingViewModel
                )
            }
        }
    }

    if (showRepairAdd) {
        ModalBottomSheet(
            onDismissRequest = { setShowRepairAdd(false) },
            sheetState = sheetState
        ) {
            RepairAdd(
                propertyViewModel = propertyViewModel,
                geolocationViewModel = geolocationViewModel,
                repairViewModel = repairViewModel,
                settingViewModel = settingViewModel,
                onClose = { modalClose(repairAddScope, sheetState, setShowRepairAdd) }
            )
        }
    }
}

