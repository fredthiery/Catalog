package com.fthiery.catalog.datasources

import androidx.room.*
import com.fthiery.catalog.models.Converters
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection

@Database(
    entities = [Item::class, ItemCollection::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ItemDB : RoomDatabase() {
    abstract fun ItemDAO(): ItemDAO
}
