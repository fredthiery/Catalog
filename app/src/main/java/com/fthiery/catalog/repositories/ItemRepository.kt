package com.fthiery.catalog.repositories

import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import com.fthiery.catalog.models.Search
import com.fthiery.catalog.models.WikiResult
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    val collections: Flow<List<ItemCollection>>

    suspend fun firstCollection(): ItemCollection?

    fun collectionSize(id: Long): Flow<Long>

    fun getCollection(id: Long?): Flow<ItemCollection?>

    fun getItems(collectionId: Long?, searchPattern: String = ""): Flow<List<Item>>

    fun getItem(id: Long?): Flow<Item>

    suspend fun insert(item: Item): Long

    suspend fun insert(collection: ItemCollection): Long

    suspend fun delete(item: Item)

    suspend fun delete(collection: ItemCollection)

    suspend fun getPhoto(query: String): String?

    suspend fun getSuggestions(query: String): List<Search>

    suspend fun getWikiPage(query: String): WikiResult?
}