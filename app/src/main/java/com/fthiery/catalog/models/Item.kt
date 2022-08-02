package com.fthiery.catalog.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity
open class Item(
    @PrimaryKey(autoGenerate = true) open val id: Int = 0,
    open var name: String = "",
    open var collectionId: Int? = null,
    open var description: String = "",
    open var photo: Uri? = null,
    open var dateAdded: Calendar = Calendar.getInstance(),
    open var lastUpdated: Calendar? = null
)