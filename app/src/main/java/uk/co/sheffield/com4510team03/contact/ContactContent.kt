package uk.co.sheffield.com4510team03.contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ContactContent(screen: ContactNavEnum, contactViewModel: ContactViewModel) {
    when (screen) {
        ContactNavEnum.SHOW -> ShowContactInfos(contactViewModel)
    }
}

@Composable
fun ShowContactInfos(contactViewModel: ContactViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(contactViewModel.contactInfoList) { itemContactInfo ->
                ShowContactInfo(itemContactInfo)
            }
        }
    }
}

@Composable
fun ShowContactInfo(contactInfo: ContactInfo) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(0.9f)
    ) {
        Column(Modifier.padding(4.dp)) {
            Text(contactInfo.type)
            Text(contactInfo.info)
        }
    }
}