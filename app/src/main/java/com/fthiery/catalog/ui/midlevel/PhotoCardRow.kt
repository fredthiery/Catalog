package com.fthiery.catalog.ui.midlevel

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.fthiery.catalog.BuildConfig
import com.fthiery.catalog.R
import com.fthiery.catalog.ui.baselevel.*
import com.fthiery.catalog.ui.theme.angle
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun PhotoCardRow(
    modifier: Modifier = Modifier,
    photos: List<Uri> = listOf(),
    color: Color = MaterialTheme.colors.primary,
    onNewPhoto: (Uri, Context) -> Unit,
    onClick: (Uri) -> Unit
) {
    val context = LocalContext.current

    // Provides a temp Content Uri for the TakePicture contract
    val tmpUri: Uri = FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.provider",
        context.filesDir.resolve("tmp_image.jpg")
    )
    val filePickerLauncher = filePickerLauncher {
        if (it != null) onNewPhoto(it, context)
    }
    val takePictureLauncher = takePictureLauncher {
        onNewPhoto(tmpUri, context)
    }

    val angle = MaterialTheme.shapes.angle
    val photoCardSize = 128

    fun Float.toRad(): Double = this * PI / 180
    val offset = 1 + sin(abs(angle.toRad()))
    val rowHeight = photoCardSize * offset
    val widthDp = LocalConfiguration.current.screenWidthDp
    val width = (widthDp / sin((90f - abs(angle)).toRad())) + (sin(abs(angle).toRad()) * rowHeight)

    LazyRow(
        modifier = modifier
            .requiredWidth(width.dp)
            .rotate(angle),
        contentPadding = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
            .add(WindowInsets(left = 14.dp, right = 14.dp))
            .asPaddingValues(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos) { photo ->
            Card(
                modifier = Modifier
                    .clickable { onClick(photo) }
                    .width(photoCardSize.dp)
                    .height(rowHeight.dp)
                    .rotate(-angle),
                shape = quadrilateralShape(
                    cornerSizes(4.dp),
                    angles(horizontal = angle)
                )
            ) {
                AsyncImage(
                    model = photo,
                    contentDescription = "Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.requiredHeight((rowHeight * offset).dp)
                )
            }
        }

        item {
            var dropdownExpanded by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier
                    .clickable { dropdownExpanded = true }
                    .width(photoCardSize.dp)
                    .height(rowHeight.dp)
                    .rotate(-angle),
                shape = quadrilateralShape(
                    cornerSizes(4.dp),
                    angles(horizontal = angle)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.AddAPhoto,
                    contentDescription = stringResource(R.string.add_a_photo),
                    tint = color,
                    modifier = Modifier.padding(32.dp)
                )
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }) {

                    DropdownMenuItem(
                        onClick = {
                            filePickerLauncher.launch("image/*")
                            dropdownExpanded = false
                        }) { Text("Pick image from storage") }

                    DropdownMenuItem(
                        onClick = {
                            takePictureLauncher.launch(tmpUri)
                            dropdownExpanded = false
                        }) { Text("Take a photo") }
                }
            }
        }
    }
}