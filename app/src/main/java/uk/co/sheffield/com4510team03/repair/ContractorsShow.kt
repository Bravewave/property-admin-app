package uk.co.sheffield.com4510team03.repair

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.co.sheffield.com4510team03.ui.sendEmail

@Composable
fun ContractorsShow(repairViewModel: RepairViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val repairers = repairViewModel.contractorEntityList
        Text(
            text = "Contractors",
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(15.dp, 0.dp),
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(1f)
                .padding(0.dp, 0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(repairers) { repairer ->
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .then(Modifier.padding(15.dp, 20.dp))
                ) {
                    Text(
                        text = "${repairer.firstName} ${repairer.surname}",
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(0.dp, 10.dp),
                        textDecoration = TextDecoration.Underline,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = repairer.email,
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .clickable {
                                sendEmail(context, repairer.email, "", "")
                            },
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(15.dp)
                    ) {
                        Text(
                            text = "Services: ",
                            modifier = Modifier.fillMaxWidth(0.9f),
                            fontSize = 18.sp,
                        )
                        val services = repairer.services
                        for (service in services) {
                            Text(
                                text = "• $service",
                                modifier = Modifier.fillMaxWidth(0.9f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}