package com.fthiery.catalog.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import com.fthiery.catalog.models.StateItem
import com.fthiery.catalog.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    val collections = repository.collections.map { collections ->
        collections.associateBy({ it.id }, { it })
    }

    fun collectionSize(collectionId: Int): Flow<Int> = repository.collectionSize(collectionId)

    fun newCollection(name: String) = viewModelScope.launch {
        repository.insert(ItemCollection(name = name))
    }

    fun getItems(collectionId: Int): Flow<List<Item>> = repository.getItems(collectionId)

    fun getItem(itemId: Int?): Flow<Item> {
        itemId?.let { return repository.getItem(itemId) }
        return flow { emit(Item()) }
    }

    fun getStateItem(itemId: Int?, collectionId: Int? = null): Flow<StateItem> {
        itemId?.let {
            return repository.getItem(itemId).map {
                StateItem(it)
            }
        }
        return flow {
            emit(
                StateItem(Item(collectionId = collectionId))
            )
        }
    }

    fun saveItem(item: Item) = viewModelScope.launch { repository.insert(item) }
}