package com.fthiery.catalog.models

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class ItemCollection(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var name: String,
    var photo: Uri
)