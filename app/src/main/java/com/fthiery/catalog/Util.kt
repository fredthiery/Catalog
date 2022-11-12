package com.fthiery.catalog

import android.content.Context
import android.graphics.Color.rgb
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.annotation.ColorInt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.graphics.ColorUtils.HSLToColor
import androidx.core.graphics.ColorUtils.colorToHSL
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.lang.Float.min
import java.util.*
import kotlin.coroutines.resume
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

fun copyToInternalStorage(uri: Uri?, context: Context): Uri? {
    if (uri == null) return null
    // Copy the selected file to internal storage
    val input = context.contentResolver.openInputStream(uri) ?: return null
    val outputFile = context.filesDir.resolve("${UUID.randomUUID()}.jpg")
    input.copyTo(outputFile.outputStream())
    return outputFile.toUri()
}

fun deleteFromInternalStorage(uri: Uri?, context: Context) {
    uri?.let {
        var file = File(it.path ?: "")
        if (file.exists()) {
            Log.i("debug", "$uri exists")
            file.delete()
        }
        file = File(it.path ?: "")
        if (!file.exists()) {
            Log.i("debug", "$uri deleted")
        }
    }
}

/**
 * Static field, contains all scroll values
 */
private val SaveMap = mutableMapOf<String, KeyParams>()

private data class KeyParams(
    val params: String = "",
    val index: Int,
    val scrollOffset: Int
)

/**
 * Save scroll state on all time.
 * @param key value for comparing screen
 * @param params arguments for find different between equals screen
 * @param initialFirstVisibleItemIndex see [LazyListState.firstVisibleItemIndex]
 * @param initialFirstVisibleItemScrollOffset see [LazyListState.firstVisibleItemScrollOffset]
 */
@Composable
fun rememberForeverLazyListState(
    key: String?,
    params: String = "",
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
        var savedValue = SaveMap[key]
        if (savedValue?.params != params) savedValue = null
        LazyListState(
            savedValue?.index ?: initialFirstVisibleItemIndex,
            savedValue?.scrollOffset ?: initialFirstVisibleItemScrollOffset
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            key?.let {
                val lastIndex = scrollState.firstVisibleItemIndex
                val lastOffset = scrollState.firstVisibleItemScrollOffset
                SaveMap[key] = KeyParams(params, lastIndex, lastOffset)
            }
        }
    }
    return scrollState
}

@Composable
fun rememberForeverLazyGridState(
    key: String?,
    params: String = "",
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyGridState {
    val scrollState = rememberSaveable(saver = LazyGridState.Saver) {
        var savedValue = SaveMap[key]
        if (savedValue?.params != params) savedValue = null
        LazyGridState(
            savedValue?.index ?: initialFirstVisibleItemIndex,
            savedValue?.scrollOffset ?: initialFirstVisibleItemScrollOffset
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            key?.let {
                val lastIndex = scrollState.firstVisibleItemIndex
                val lastOffset = scrollState.firstVisibleItemScrollOffset
                SaveMap[key] = KeyParams(params, lastIndex, lastOffset)
            }
        }
    }
    return scrollState
}

fun Float.toRad(): Float = (this * PI / 180).toFloat()
fun Float.degreesOffset(): Float = sin(this.toRad())
fun Float.absDegOffset(): Float = sin(abs(this.toRad()))

suspend fun getBitmapColor(path: String, context: Context): Int {
    val drawable = context.imageLoader.execute(
        ImageRequest
            .Builder(context)
            .allowHardware(false)
            .data(path)
            .build()
    ).drawable
    return getColor(drawable)
}

private suspend fun getColor(drawable: Drawable?): Int {
    drawable?.let {
        return suspendCancellableCoroutine { continuation ->
            Palette.from(drawable.toBitmap())
                .resizeBitmapArea(400)
                .generate {
                    it?.let {
                        val swatch = it.vibrantSwatch
                            ?: it.lightVibrantSwatch
                            ?: it.darkVibrantSwatch
                            ?: Palette.Swatch(rgb(255, 0, 255), 0)
                        continuation.resume(swatch.rgb)
                    }
                }
        }
    }
    return rgb(255, 255, 255)
}

@ColorInt
fun Int.setSL(saturation: Float? = null, luminance: Float? = null): Int {
    val hsv = FloatArray(3)
    colorToHSL(this, hsv)
    return HSLToColor(floatArrayOf(hsv[0], min(hsv[1], saturation ?: hsv[1]), luminance ?: hsv[2]))
}

fun Color.contentColor(): Color {
    return if (this.luminance() > 0.4) Color.Black
    else Color.White
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}