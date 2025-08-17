package uk.co.sheffield.com4510team03.contact

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class ContactViewModel(app: Application) : AndroidViewModel(app) {

    var contactInfoList by mutableStateOf(listOf<ContactInfo>())
        private set

    fun updateContactEntityList() {
        val contractInfo = mutableListOf<ContactInfo>()
        contractInfo.add(ContactInfo("Phone", "0114 4510 1337"))

        contractInfo.add(ContactInfo("Email", "contact@RegentCourtLockSmith.co.uk"))

        contractInfo.add(ContactInfo("Website", "RegentCourtLockSmith.co.uk"))

        contactInfoList = contractInfo
    }


}