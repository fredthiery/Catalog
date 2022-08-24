package com.fthiery.catalog.models

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "",
    var collectionId: Long = 0,
    var description: String = "",
    var dateAdded: Calendar = Calendar.getInstance(),
    var lastUpdated: Calendar? = null,
    var properties: MutableMap<String, String> = mutableMapOf(),
    var photos: List<Uri> = listOf(),
    var lightColor: Int = 0xFFFFFF,
    var darkColor: Int = 0x000000
)