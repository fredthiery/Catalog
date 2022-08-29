package com.fthiery.catalog.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fthiery.catalog.getBitmapColor
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    var item by mutableStateOf(Item())
    var displayPhoto by mutableStateOf<Uri?>(null)

    fun selectItem(itemId: Long? = null, collectionId: Long? = null) =
        viewModelScope.launch {
            if (itemId != item.id) repository.getItem(itemId).collect {
                item = it
                collectionId?.let { item.collectionId = collectionId }
            }
        }

    fun save(itemToSave: Item, context: Context, onComplete: (Long) -> Unit = {}) =
        viewModelScope.launch {
            itemToSave.lastUpdated = Calendar.getInstance()
            itemToSave.setColors(context)
            val itemId = repository.insert(itemToSave)
            onComplete(itemId)
            selectItem(itemId)
        }

    fun delete(item: Item, onComplete: () -> Unit = {}) =
        viewModelScope.launch {
            repository.delete(item)
            onComplete()
        }

    private suspend fun Item.setColors(context: Context) {
        if (photos.isNotEmpty()) {
            photos[0].path?.let {
                color = getBitmapColor(it, context)
            }
        }
    }
}