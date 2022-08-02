package com.fthiery.catalog.models

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class ItemCollection(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String = "",
    var photo: Uri? = null
)