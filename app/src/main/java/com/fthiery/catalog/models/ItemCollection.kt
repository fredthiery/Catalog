package com.fthiery.catalog.models

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ItemCollection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String = "",
    var photo: Uri? = null,
    var dateAdded: Calendar = Calendar.getInstance(),
    var lastUpdated: Calendar? = null,
    var lightColor: Int = 0xFFFFFF,
    var darkColor: Int = 0x000000
)