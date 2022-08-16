package com.fthiery.catalog.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fthiery.catalog.models.ItemCollection
import com.fthiery.catalog.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {
    var editCollection by mutableStateOf(ItemCollection())

    fun selectCollection(id: Long? = null) = viewModelScope.launch {
        if (id == null) editCollection = ItemCollection()
        else repository.getCollection(id).collect {
            editCollection = it
        }
    }

    fun saveCollection(
        collection: ItemCollection? = null,
        onComplete: (Long) -> Unit = {}
    ) = viewModelScope.launch {
        val collectionToSave = collection ?: editCollection
        collectionToSave.photo = repository.getPhoto(collectionToSave.name)?.toUri()
        onComplete(repository.insert(collectionToSave))
    }
}