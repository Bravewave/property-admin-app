package uk.co.sheffield.com4510team03.property

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

@Composable
fun PropertyShow(properties: List<PropertyEntity>, type: String, onEdit: (PropertyEntity) -> Unit) {
    val (expandedSort, setExpandedSort) = rememberSaveable { mutableStateOf(false) }
    val (sortBy, setSortBy) = rememberSaveable { mutableStateOf("Name (Asc)") }

    if (properties.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val text = when (type) {
                "Archived" -> "No properties archived"
                "Active" -> "No properties added"
                else -> "Invalid property type input"
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
                Spacer(modifier = Modifier.width(6.dp))
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
                        text = { Text("Name (Asc)") },
                        onClick = {
                            setSortBy("Name (Asc)")
                            setExpandedSort(false)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Name (Desc)") },
                        onClick = {
                            setSortBy("Name (Desc)")
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

                val sortedProperties = when (sortBy) {
                    "Name (Asc)" -> properties.sortedBy { it.name }
                    "Name (Desc)" -> properties.sortedByDescending { it.name }
                    else -> properties
                }

                itemsIndexed(sortedProperties) { _, currentProperty ->
                    PropertyItem(currentProperty, onEdit = { onEdit(currentProperty) })
                }
            }
        }
    }
}
