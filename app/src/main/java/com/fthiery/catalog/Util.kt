package com.fthiery.catalog

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.net.toUri
import java.util.*

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