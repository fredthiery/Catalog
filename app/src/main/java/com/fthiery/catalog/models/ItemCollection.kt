package com.fthiery.catalog.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ItemCollection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String = "",
    var photo: Uri? = null,
    var dateAdded: Calendar = Calendar.getInstance(),
    var lastUpdated: Calendar? = null
) : Colored {
    override var color: Int? = null
}