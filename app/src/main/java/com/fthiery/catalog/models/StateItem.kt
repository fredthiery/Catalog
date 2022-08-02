package com.fthiery.catalog.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.reflect.KProperty

class StateItem(item: Item): Item() {
    override var id by mutableStateOf(item.id)
    override var name by mutableStateOf(item.name)
    override var collectionId by mutableStateOf(item.collectionId)
    override var description by mutableStateOf(item.description)
    override var photo by mutableStateOf(item.photo)
    override var dateAdded by mutableStateOf(item.dateAdded)
    override var lastUpdated by mutableStateOf(item.lastUpdated)
}