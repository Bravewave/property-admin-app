package uk.co.sheffield.com4510team03.ui

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uk.co.sheffield.com4510team03.property.PropertyEntity
import java.io.FileNotFoundException
import java.util.Date
import java.util.Locale

@Composable
fun PaddedColumnText(content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        content()
    }
}

fun getBitmapFromURI(contentResolver: ContentResolver, uri: Uri): Bitmap? {
    val source = ImageDecoder.createSource(contentResolver, uri)
    return try {
        ImageDecoder.decodeBitmap(source)
    } catch (e: FileNotFoundException) {
        null
    }
}

@Composable
fun ShowImage(uri: Uri, height: Dp = 200.dp) {
    if (uri == Uri.EMPTY) {
        return Text("No Image Chosen")
    }
    val bitmap = getBitmapFromURI(LocalContext.current.contentResolver, uri)
        ?: return Text("Image Not Found")

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier
            .height(height) // Set the desired height
            .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat()) // Maintain aspect ratio
            .clip(RoundedCornerShape(12.dp))
    )
}

fun to3DP(n: Float): String {
    return String.format(Locale.US, "%.3f", n)
}

@Composable
fun SendEmailButton(recipient: String, subject: String, body: String, attachment: Uri? = null) {
    val context = LocalContext.current

    Button(onClick = {
        sendEmail(context, recipient, subject, body, attachment)
    }) {
        Text("Resend Email")
    }
}


fun createEmailBody(
    contractor: String,
    service: String,
    propertyName: String,
    landlordName: String,
    phoneNumber: String,
    signature: String,
    notes: String,
    date: Date,
    latitude: Float,
    longitude: Float
): String {
    val mapsLink = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
    return """
        Hello $contractor,

        There has been a $service request at $propertyName.

        The details are as follows:
        Request Date: $date
        Notes: $notes
        Google Maps Address: $mapsLink

        $signature,
        $landlordName
        
        Phone Number: $phoneNumber
    """.trimIndent()
}

fun sendEmail(
    context: Context,
    recipient: String,
    subject: String,
    body: String,
    attachment: Uri? = null
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = if (attachment != null) "application/octet-stream" else "text/plain"
        putExtra(Intent.EXTRA_EMAIL, recipient)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        attachment?.let { putExtra(Intent.EXTRA_STREAM, it) } // Add only if not null
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
        Log.i("email", "Redirected to mail app")
    } else {
        Log.i("email", "No email app found...")
    }
}


/**
 * Handles closing the [ModalBottomSheet]s for adding/editing properties and adding repairs
 * @param coroutineScope the active [CoroutineScope]
 * @param sheetState a [remember]ed [SheetState]
 * @param setShowModal a function to set the visibility of the [ModalBottomSheet]
 * @param setPropNull a function to reset the [PropertyEntity] in the case of adding/editing a property`
 * @return [Unit]
 */
@OptIn(ExperimentalMaterial3Api::class)
fun modalClose(
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    setShowModal: (Boolean) -> Unit,
    setPropNull: (() -> Unit)? = null
) {
    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
        if (!sheetState.isVisible) {
            setShowModal(false)
            setPropNull?.invoke()
        }
    }
}