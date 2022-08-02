package com.fthiery.catalog.repositories

import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    val collections: Flow<List<ItemCollection>>

    fun collectionSize(collectionId: Int): Flow<Int>

    fun getItems(collectionId: Int): Flow<List<Item>>

    fun getItem(id: Int): Flow<Item>

    suspend fun insert(item: Item)

    suspend fun insert(collection: ItemCollection)

    suspend fun delete(item: Item)

    suspend fun delete(collection: ItemCollection)
}