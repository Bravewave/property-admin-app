package uk.co.sheffield.com4510team03.property

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.co.sheffield.com4510team03.ui.ShowImage

@Composable
fun PropertyItem(currentPropertyEntity: PropertyEntity, onEdit: () -> Unit) {
    val context = LocalContext.current
    val (showMore, setShowMore) = rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var name = currentPropertyEntity.name
            if (name.length > 18) {
                name = name.take(18) + "..."
            }
            Text(
                text = name,
                modifier = Modifier.padding(bottom = 2.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Row(horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { setShowMore(!showMore) }) {
                    if (showMore) {
                        Text(text = "View Less")
                    } else {
                        Text(text = "View More")
                    }
                }

                IconButton(onClick = { onEdit() }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Property")
                }
            }
        }

        if (showMore) {
            Column(
                modifier = Modifier.padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Address:", fontWeight = FontWeight.Bold)
                Text(text = currentPropertyEntity.address)

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Telephone:", fontWeight = FontWeight.Bold)
                        Text(text = currentPropertyEntity.telephone)
                    }
                    val annotatedString = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                            pushStringAnnotation(
                                tag = "tel",
                                annotation = "tel:${currentPropertyEntity.telephone}"
                            )
                            append(currentPropertyEntity.telephone)
                            pop()
                        }
                    }
                    TextButton(
                        onClick = {
                            annotatedString.getStringAnnotations(
                                tag = "tel",
                                start = 0,
                                end = annotatedString.length
                            )
                                .firstOrNull()?.let { annotation ->
                                    val intent =
                                        Intent(Intent.ACTION_DIAL, Uri.parse(annotation.item))
                                    context.startActivity(intent)
                                }
                        }
                    ) {
                        Text("Call Property")
                    }
                }

                Text(text = "Notes:", fontWeight = FontWeight.Bold)
                Text(text = currentPropertyEntity.notes)

                Text(text = "Image:", fontWeight = FontWeight.Bold)
                ShowImage(currentPropertyEntity.image)

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val mapsLink =
                        "https://www.google.com/maps/search/?api=1&query=${currentPropertyEntity.geolocationLat},${currentPropertyEntity.geolocationLong}"
                    Column {
                        Text(text = "Coordinates:", fontWeight = FontWeight.Bold)
                        Text(text = currentPropertyEntity.getCoordinates())
                    }
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsLink))
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Show Directions")
                    }
                }
            }
        }
    }
}
