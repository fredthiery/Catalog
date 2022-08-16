package com.fthiery.catalog.models

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity
open class ItemCollection(
    @PrimaryKey(autoGenerate = true) open val id: Long = 0,
    open var name: String = "",
    open var photo: Uri? = null,
    open var dateAdded: Calendar = Calendar.getInstance(),
    open var lastUpdated: Calendar? = null
)