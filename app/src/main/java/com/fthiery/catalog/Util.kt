package com.fthiery.catalog

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

fun copyToInternalStorage(uri: Uri?, context: Context): Uri? {
    if (uri == null) return null
    // Copy the selected file to internal storage
    val input = context.contentResolver.openInputStream(uri) ?: return null
    val outputFile = context.filesDir.resolve("${UUID.randomUUID()}.jpg")
    input.copyTo(outputFile.outputStream())
    return outputFile.toUri()
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

suspend fun getBitmapColors(path: String, context: Context): Pair<Int, Int> {
    val drawable = context.imageLoader.execute(
        ImageRequest
            .Builder(context)
            .allowHardware(false)
            .data(path)
            .build()
    ).drawable
    return getColors(drawable)
}

private suspend fun getColors(drawable: Drawable?): Pair<Int, Int> {
    drawable?.let {
        return suspendCancellableCoroutine { continuation ->
            Palette.from(drawable.toBitmap())
                .resizeBitmapArea(400)
                .generate {
                    it?.let {
                        val vibrantColor = it.getVibrantColor(0xFFFFFF)
                        val darkVibrantColor = it.getDarkVibrantColor(0x000000)
                        continuation.resume(vibrantColor to darkVibrantColor)
                    }
                }
        }
    }
    return 0xFFFFFF to 0x000000
}

@Composable
fun ItemCollection.backgroundColor(): Color {
    return if (isSystemInDarkTheme()) Color(darkColor)
    else Color(lightColor)
}

@Composable
fun Item.backgroundColor(): Color {
    return if (isSystemInDarkTheme()) Color(darkColor)
    else Color(lightColor)
}