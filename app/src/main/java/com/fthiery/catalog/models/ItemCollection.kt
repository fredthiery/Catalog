package com.fthiery.catalog.models

import android.net.Uri
import androidx.annotation.ColorInt
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class ItemCollection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String = "",
    var photo: Uri? = null,
    var dateAdded: Calendar = Calendar.getInstance(),
    var lastUpdated: Calendar? = null,
    @ColorInt color: Int? = null
) : Colored(color)