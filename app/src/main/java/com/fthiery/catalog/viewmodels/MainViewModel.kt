package com.fthiery.catalog.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.ItemCollection
import com.fthiery.catalog.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    var collections by mutableStateOf(listOf<ItemCollection>())
        private set
    var currentItem by mutableStateOf(Item())
    var modeEdit by mutableStateOf(false)

    init {
        viewModelScope.launch {
            repository.collections.collect() {
                collections = it
            }
        }
    }

    fun getItems(collectionId: Int): SnapshotStateList<Item> {
        val items = mutableStateListOf<Item>()
        viewModelScope.launch {
            repository.getItems(collectionId).collect() {
                items.clear()
                items.addAll(it)
            }
        }
        return items
    }

    fun getItem(itemId: Int) {
        viewModelScope.launch {
            repository.getItem(itemId).collect() {
                currentItem = it
            }
        }
    }

    fun saveItem(item: Item) {
        viewModelScope.launch { repository.insert(item) }
    }

    fun newCollection(name: String) {
        viewModelScope.launch { repository.insert(ItemCollection(name = name)) }
    }

}