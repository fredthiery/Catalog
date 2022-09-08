package com.fthiery.catalog.models

import android.net.Uri
import androidx.annotation.ColorInt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fthiery.catalog.setSL
import java.util.*

@Entity
class Item(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "",
    var collectionId: Long = 0,
    var description: String = "",
    var dateAdded: Calendar = Calendar.getInstance(),
    var lastUpdated: Calendar? = null,
    var properties: MutableMap<String, String> = mutableMapOf(),
    var photos: List<Uri> = listOf(),
    @ColorInt color: Int? = null
) : Colored(color)