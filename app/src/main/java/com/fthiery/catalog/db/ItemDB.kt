package com.fthiery.catalog.db

import androidx.room.*
import com.fthiery.catalog.models.Converters
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [Item::class, ItemCollection::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ItemDB : RoomDatabase() {
    abstract fun ItemDAO(): ItemDAO
}
