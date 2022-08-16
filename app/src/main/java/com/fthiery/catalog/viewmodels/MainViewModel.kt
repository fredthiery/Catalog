package com.fthiery.catalog.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import com.fthiery.catalog.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    val collections = repository.collections
        .onEach { list ->
            if (_collection == null) selectCollection(list.getOrElse(0) { ItemCollection() }.id)
        }
        .map { collections -> collections.associateBy { it.id } }

    private var _collection by mutableStateOf<ItemCollection?>(null)
    val collection: ItemCollection?
        get() = _collection

    private var _items by mutableStateOf<List<Item>>(listOf())
    val items: List<Item>
        get() = _items

    private var _searchPattern by mutableStateOf("")
    var searchPattern: String
        get() = _searchPattern
        set(pattern) {
            this._searchPattern = pattern
            if (pattern.isEmpty()) {
                selectCollection(_collection?.id)
            } else viewModelScope.launch {
                repository.searchItems(_collection?.id, _searchPattern).collect {
                    _items = it
                }
            }
        }

    fun collectionSize(collectionId: Long): Flow<Long> = repository.collectionSize(collectionId)

    fun selectCollection(collectionId: Long?) = viewModelScope.launch {
        val collectionFlow = repository.getCollection(collectionId)
        val itemFlow = repository.getItems(collectionId)
        launch { collectionFlow.collect { _collection = it } }
        launch { itemFlow.collect { _items = it } }
    }
}