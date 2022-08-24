package com.fthiery.catalog.repositories

import com.fthiery.catalog.datasources.ItemDAO
import com.fthiery.catalog.datasources.UnsplashApiService
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemDAO: ItemDAO,
    private val unsplash: UnsplashApiService = UnsplashApiService.create()
) : ItemRepository {
    override val collections: Flow<List<ItemCollection>> = itemDAO.getCollections()

    override suspend fun firstCollection(): ItemCollection? = itemDAO.firstCollection()

    override fun collectionSize(id: Long): Flow<Long> {
        return itemDAO.collectionSize(id)
    }

    override fun getCollection(id: Long?): Flow<ItemCollection?> {
        id?.let { return itemDAO.getCollection(id) }
        return flow {}
    }

    override fun getItems(collectionId: Long?, searchPattern: String): Flow<List<Item>> {
        return if (searchPattern == "") itemDAO.getItems(collectionId)
        else itemDAO.searchItems(collectionId, "%$searchPattern%")
    }

    override fun getItem(id: Long?): Flow<Item> {
        return itemDAO.getItem(id ?: 0L).map { item ->
            item ?: Item(id = id ?: 0L)
        }
    }

    override suspend fun insert(item: Item): Long {
        return itemDAO.insert(item)
    }

    override suspend fun insert(collection: ItemCollection): Long {
        return itemDAO.insert(collection)
    }

    override suspend fun delete(item: Item) {
        /* TODO: devrait effacer les photos */
        itemDAO.delete(listOf(item))
    }

    override suspend fun delete(collection: ItemCollection) {
        itemDAO.delete(collection)
        itemDAO.delete(getItems(collection.id).first())
    }

    override suspend fun getPhoto(query: String): String? {
        val results = unsplash.searchPhotos(query).results
        val index = if (results.isNotEmpty()) Random.nextInt(results.size) else 0
        return results.getOrNull(index)?.urls?.regular
    }
}