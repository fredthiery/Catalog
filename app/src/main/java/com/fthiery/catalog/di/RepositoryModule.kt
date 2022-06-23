package com.fthiery.catalog.di

import com.fthiery.catalog.repositories.ItemRepository
import com.fthiery.catalog.repositories.ItemRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun providesItemRepository(impl: ItemRepositoryImpl): ItemRepository
}