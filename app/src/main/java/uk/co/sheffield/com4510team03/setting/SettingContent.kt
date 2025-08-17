package uk.co.sheffield.com4510team03.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SettingContent(settingNavEnum: SettingNavEnum, settingViewModel: SettingViewModel) {
    when (settingNavEnum) {
        SettingNavEnum.SHOW -> ShowSettings(settingViewModel)
    }
}

@Composable
fun ShowSettings(settingViewModel: SettingViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.9f)
        ) {

            // Name setting

            TextField(
                value = settingViewModel.name,
                onValueChange = { settingViewModel.updateNameNonSuspended(it) },
                label = { Text("Name") }
            )
        }
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.9f)
        ) {

            // PhoneNumber setting

            TextField(
                value = settingViewModel.contactNumber,
                onValueChange = { settingViewModel.updatePhoneNumberNonSuspended(it) },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.9f)
        ) {

            // Signature setting

            TextField(
                value = settingViewModel.signature,
                onValueChange = { settingViewModel.updateSignatureNonSuspended(it) },
                label = { Text("Signature") },
            )
        }
    }
}