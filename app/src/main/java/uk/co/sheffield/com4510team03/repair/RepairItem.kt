package uk.co.sheffield.com4510team03.repair

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uk.co.sheffield.com4510team03.property.PropertyViewModel
import uk.co.sheffield.com4510team03.setting.SettingViewModel
import uk.co.sheffield.com4510team03.ui.SendEmailButton
import uk.co.sheffield.com4510team03.ui.ShowImage
import uk.co.sheffield.com4510team03.ui.createEmailBody
import uk.co.sheffield.com4510team03.ui.to3DP

@Composable
fun RepairItem(
    repair: RepairEntity,
    propertyViewModel: PropertyViewModel,
    repairViewModel: RepairViewModel,
    settingViewModel: SettingViewModel
) {

    val archiveScope = rememberCoroutineScope()
    val (showMore, setShowMore) = rememberSaveable { mutableStateOf(false) }

    // get the name of the property by the repair's propertyId field
    val properties = propertyViewModel.propertyEntityList
    val propertyName = properties.find { it.id == repair.propertyId }?.name ?: "Unknown Property"
    val contractor =
        repairViewModel.contractorEntityList.find { it.id == repair.contractorId }?.getFullName()
            ?: "Unknown Repairer"
    val contractorEmail =
        repairViewModel.contractorEntityList.find { it.id == repair.contractorId }?.email
    val emailSubject = "${repair.service} request at $propertyName"
    val emailBody = createEmailBody(
        contractor,
        repair.service,
        propertyName,
        settingViewModel.name,
        settingViewModel.contactNumber,
        settingViewModel.signature,
        repair.notes,
        repair.requestDate,
        repair.latitude,
        repair.longitude
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(0.9f)
    ) {
        Row(
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                var name = propertyName
                if (name.length > 18) {
                    name = name.take(18) + "... "
                }
                Text(
                    text = name,
                    modifier = Modifier.padding(bottom = 2.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = repair.notes.take(20),
                    modifier = Modifier.padding(bottom = 2.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )

            }

            TextButton(onClick = { setShowMore(!showMore) }) {
                if (showMore) {
                    Text(text = "View Less")
                } else {
                    Text(text = "View More")
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (contractorEmail != null) {
                SendEmailButton(contractorEmail, emailSubject, emailBody, repair.image)
            }

            if (!repair.repaired) {
                TextButton(onClick = {
                    archiveScope.launch {
                        repairViewModel.markAsComplete(repair)
                    }
                }) {
                    Text("Mark as Repaired")
                }
            }

        }

        if (showMore) {
            Column(
                modifier = Modifier.padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = "Property:",
                    fontWeight = FontWeight.Bold
                )
                Text(propertyName)

                Text(
                    text = "Service:",
                    fontWeight = FontWeight.Bold
                )
                Text(repair.service)

                Text(
                    text = "Contractor:",
                    fontWeight = FontWeight.Bold
                )
                Text(contractor)

                Text(
                    text = "Request Date:",
                    fontWeight = FontWeight.Bold
                )
                Text(repair.requestDate.toString())

                Text(
                    text = "Notes:",
                    fontWeight = FontWeight.Bold
                )
                Text(repair.notes)

                Text(
                    text = "Image:",
                    fontWeight = FontWeight.Bold
                )
                ShowImage(repair.image)

                Text(
                    text = "Coordinates:",
                    fontWeight = FontWeight.Bold
                )
                Text("(${to3DP(repair.latitude)}, ${to3DP(repair.longitude)})")

            }
        }
    }
}