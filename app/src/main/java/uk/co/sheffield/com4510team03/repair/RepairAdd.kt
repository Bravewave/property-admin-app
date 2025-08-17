package uk.co.sheffield.com4510team03.repair

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uk.co.sheffield.com4510team03.geolocation.GeoLocationViewModel
import uk.co.sheffield.com4510team03.property.PropertyViewModel
import uk.co.sheffield.com4510team03.setting.SettingViewModel
import uk.co.sheffield.com4510team03.ui.createEmailBody
import uk.co.sheffield.com4510team03.ui.sendEmail
import java.util.Date

@Composable
fun RepairAdd(
    repairViewModel: RepairViewModel,
    propertyViewModel: PropertyViewModel,
    geolocationViewModel: GeoLocationViewModel,
    settingViewModel: SettingViewModel,
    onClose: () -> Unit
) {
    // expanded is whether the dropdown options have expanded out yet
    val (propertyExpanded, setPropertyExpanded) = rememberSaveable { mutableStateOf(false) }
    val (serviceExpanded, setServiceExpanded) = rememberSaveable { mutableStateOf(false) }
    val (contractorExpanded, setContractorExpanded) = rememberSaveable { mutableStateOf(false) }

    val (propertyId, setPropertyId) = rememberSaveable { mutableIntStateOf(-1) }
    val (contractorId, setContractorId) = rememberSaveable { mutableIntStateOf(-1) }
    val (service, setService) = rememberSaveable { mutableStateOf("") }
    val (notes, setNotes) = rememberSaveable { mutableStateOf("") }
    val (image, setImage) = rememberSaveable { mutableStateOf<Uri>(Uri.EMPTY) }
    val (latitude, setLatitude) = rememberSaveable { mutableFloatStateOf(0f) }
    val (longitude, setLongitude) = rememberSaveable { mutableFloatStateOf(0f) }

    val (submitError, setSubmitError) = rememberSaveable { mutableStateOf(false) }

    val repairEntityScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Reset all fields
    fun reset() {
        setPropertyId(-1)
        setContractorId(-1)
        setService("")
        setNotes("")
        setImage(Uri.EMPTY)
        setLatitude(0f)
        setLongitude(0f)
        setSubmitError(false)
    }

    // Submit repair data
    fun submit(repairEntityScope: CoroutineScope) {
        if (propertyId == -1 || service == "" || contractorId == -1 || latitude == 0f || longitude == 0f) {
            setSubmitError(true)
        } else {
            setSubmitError(false)

            val newRepairEntity = RepairEntity(
                0, // id will be auto-generated, so we set to zero
                propertyId, // foreign key to property table
                contractorId, // references contractors defined in repairViewModel
                Date(), // Request date is the current date
                service,
                notes,
                image,
                latitude,
                longitude,
                false, // By default, repair is not complete
            )
            repairEntityScope.launch {
                repairViewModel.updateRepairEntity(newRepairEntity)
            }

            // create the email content and send it using the mailer util
            val properties = propertyViewModel.propertyEntityList
            // propertyName, contractor and contractorEmail have null checks. They will never be null
            // Bc of how the form works but it stops Android Studio complaining :P
            val propertyName = properties.find { it.id == propertyId }?.name ?: "Unknown Property"
            val contractor =
                repairViewModel.contractorEntityList.find { it.id == contractorId }?.getFullName()
                    ?: "Unknown Contractor"
            val contractorEmail =
                repairViewModel.contractorEntityList.find { it.id == contractorId }?.email
                    ?: "error@err.or"
            val emailSubject = "$service request at $propertyName"
            val emailBody = createEmailBody(
                contractor,
                service,
                propertyName,
                settingViewModel.name,
                settingViewModel.contactNumber,
                settingViewModel.signature,
                notes,
                Date(),
                latitude,
                longitude
            )
            sendEmail(context, contractorEmail, emailSubject, emailBody, image)

            reset()
            onClose()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Please fill in repair details")

        RepairForm(
            repairViewModel,
            propertyViewModel,
            geolocationViewModel,
            propertyId,
            setPropertyId,
            propertyExpanded,
            setPropertyExpanded,
            serviceExpanded,
            setServiceExpanded,
            contractorExpanded,
            setContractorExpanded,
            contractorId,
            setContractorId,
            service,
            setService,
            notes,
            setNotes,
            image,
            setImage,
            latitude,
            setLatitude,
            longitude,
            setLongitude,
            submitError,
            reset = { reset() },
            submit = { submit(repairEntityScope) }
        )
    }
}

