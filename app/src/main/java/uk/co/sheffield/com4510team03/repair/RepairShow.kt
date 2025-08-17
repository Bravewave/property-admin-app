package uk.co.sheffield.com4510team03.repair

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.co.sheffield.com4510team03.property.PropertyViewModel
import uk.co.sheffield.com4510team03.setting.SettingViewModel

@Composable
fun RepairShow(
    repairs: List<RepairEntity>,
    type: String,
    repairViewModel: RepairViewModel,
    propertyViewModel: PropertyViewModel,
    settingViewModel: SettingViewModel
) {
    val (expandedSort, setExpandedSort) = rememberSaveable { mutableStateOf(false) }
    val (sortBy, setSortBy) = rememberSaveable { mutableStateOf("Date (Youngest)") }

    if (repairs.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val text = when (type) {
                "Archived" -> "No repairs archived"
                "Active" -> "No repairs added"
                else -> "Invalid repair type input"
            }
            Text(text, color = Color.Gray, fontSize = 24.sp, textAlign = TextAlign.Center)
        }
    } else {
        // Added a y-offset to remove whitespace between the header and content
        Column(modifier = Modifier.offset(y = (-32).dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text("Sort by: $sortBy")
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { setExpandedSort(true) }) {
                    Icon(Icons.Filled.ArrowDropDown, "Sort By Drop Down")
                }
                Spacer(modifier = Modifier.width(8.dp))
                DropdownMenu(
                    expanded = expandedSort,
                    onDismissRequest = { setExpandedSort(false) },
                    offset = androidx.compose.ui.unit.DpOffset(
                        x = (-8).dp,
                        y = 0.dp
                    ) // Adjust X to align to the right
                ) {
                    DropdownMenuItem(
                        text = { Text("Date (Youngest)") },
                        onClick = {
                            setSortBy("Date (Youngest)")
                            setExpandedSort(false)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Date (Oldest)") },
                        onClick = {
                            setSortBy("Date (Oldest)")
                            setExpandedSort(false)
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val sortedRepairs = when (sortBy) {
                    "Date (Youngest)" -> repairs.sortedBy { it.requestDate }
                    "Date (Oldest)" -> repairs.sortedByDescending { it.requestDate }
                    else -> repairs
                }

                itemsIndexed(sortedRepairs) { _, currentRepair ->
                    RepairItem(currentRepair, propertyViewModel, repairViewModel, settingViewModel)
                }
            }
        }
    }
}