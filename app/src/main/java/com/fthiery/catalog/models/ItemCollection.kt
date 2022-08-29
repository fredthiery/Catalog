package com.fthiery.catalog.models

import android.graphics.Color.rgb
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ItemCollection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String = "",
    var photo: Uri? = null,
    var dateAdded: Calendar = Calendar.getInstance(),
    var lastUpdated: Calendar? = null,
    @ColorInt var color: Int? = null
)