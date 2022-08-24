package com.fthiery.catalog.viewmodels

import androidx.lifecycle.ViewModel
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import com.fthiery.catalog.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    val collections = repository.collections
        .map { collections -> collections.associateBy { it.id } }

    fun collectionSize(collectionId: Long): Flow<Long> =
        repository.collectionSize(collectionId)

    fun getCollection(collectionId: Long?): Flow<ItemCollection?> =
        repository.getCollection(collectionId)

    fun getItems(collectionId: Long?, searchPattern: String = ""): Flow<List<Item>> =
        repository.getItems(collectionId, searchPattern)

    suspend fun firstCollection() = repository.firstCollection()
}