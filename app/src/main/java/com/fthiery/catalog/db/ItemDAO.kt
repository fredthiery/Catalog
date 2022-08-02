package com.fthiery.catalog.db

import androidx.room.*
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO {
    @Query("SELECT * FROM itemcollection")
    fun getCollections(): Flow<List<ItemCollection>>

    @Query("SELECT COUNT(*) FROM item WHERE collectionId=:collectionId")
    fun collectionSize(collectionId: Int): Flow<Int>

    @Query("SELECT * FROM item WHERE collectionId=:collectionId")
    fun getItems(collectionId: Int): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE id=:id")
    fun getItem(id: Int): Flow<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: ItemCollection)

    @Delete
    suspend fun delete(items: List<Item>)

    @Delete
    suspend fun delete(collection: ItemCollection)
}