package com.fthiery.catalog.di

import android.content.Context
import androidx.room.Room
import com.fthiery.catalog.datasources.ItemDAO
import com.fthiery.catalog.datasources.ItemDB
import com.fthiery.catalog.datasources.UnsplashApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DBModule {
    @Provides
    fun provideItemDAO(itemDB: ItemDB): ItemDAO {
        return itemDB.ItemDAO()
    }

    @Provides
    @Singleton
    fun provideItemDB(@ApplicationContext appContext: Context): ItemDB {
        return Room.databaseBuilder(
            appContext,
            ItemDB::class.java,
            "ItemDB"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUnsplashApiService(): UnsplashApiService {
        return UnsplashApiService.create()
    }
}