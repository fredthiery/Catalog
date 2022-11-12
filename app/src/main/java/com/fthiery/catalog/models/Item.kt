package com.fthiery.catalog.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val collectionId: Long = 0,
    val description: String = "",
    val dateAdded: Calendar = Calendar.getInstance(),
    val lastUpdated: Calendar? = null,
    val properties: Map<String, String> = mapOf(),
    val photos: List<Uri> = listOf()
) : Colored {
    override var color: Int? = null
}