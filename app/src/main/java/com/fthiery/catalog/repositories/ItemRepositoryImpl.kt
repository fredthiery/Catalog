package com.fthiery.catalog.repositories

import com.fthiery.catalog.db.ItemDAO
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemDAO: ItemDAO
) : ItemRepository {
    override val collections: Flow<List<ItemCollection>> = itemDAO.getCollections()

    override fun collectionSize(collectionId: Int): Flow<Int> {
        return itemDAO.collectionSize(collectionId)
    }

    override fun getItems(collectionId: Int): Flow<List<Item>> {
        return itemDAO.getItems(collectionId)
    }

    override fun getItem(id: Int): Flow<Item> {
        return itemDAO.getItem(id)
    }

    override suspend fun insert(item: Item) {
        itemDAO.insert(item)
    }

    override suspend fun insert(collection: ItemCollection) {
        itemDAO.insert(collection)
    }

    override suspend fun delete(item: Item) {
        itemDAO.delete(listOf(item))
    }

    override suspend fun delete(collection: ItemCollection) {
        itemDAO.delete(collection)
        itemDAO.delete(getItems(collection.id).first())
    }
}