package com.fthiery.catalog.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var name: String,
    var collectionId: Int,
    var description: String = "",
    var photo: Uri?,
    var dateAdded: Calendar = Calendar.getInstance(),
    var lastUpdated: Calendar?
)