package com.fthiery.catalog.datasources

import androidx.room.*
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO {
    @Query("SELECT * FROM itemcollection")
    fun getCollections(): Flow<List<ItemCollection>>

    @Query("SELECT * FROM itemcollection ORDER BY dateAdded LIMIT 1")
    suspend fun firstCollection(): ItemCollection?

    @Query("SELECT * FROM itemcollection WHERE id=:id")
    fun getCollection(id: Long): Flow<ItemCollection?>

    @Query("SELECT COUNT(*) FROM item WHERE collectionId=:collectionId")
    fun collectionSize(collectionId: Long): Flow<Long>

    @Query("SELECT * FROM item WHERE collectionId=:collectionId")
    fun getItems(collectionId: Long?): Flow<List<Item>>

    @Query(
        """
        SELECT * FROM item
        WHERE collectionId=:collectionId
        AND ( name LIKE :searchPattern OR
        description LIKE :searchPattern OR
        properties LIKE :searchPattern )
        """
    )
    fun searchItems(collectionId: Long?, searchPattern: String): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE id=:id")
    fun getItem(id: Long): Flow<Item?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: ItemCollection): Long

    @Delete
    suspend fun delete(items: List<Item>)

    @Delete
    suspend fun delete(collection: ItemCollection)
}