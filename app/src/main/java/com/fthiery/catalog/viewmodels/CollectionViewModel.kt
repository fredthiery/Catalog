package com.fthiery.catalog.viewmodels

import android.content.Context
import android.graphics.Color.HSVToColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.ColorUtils.HSLToColor
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fthiery.catalog.getBitmapColor
import com.fthiery.catalog.models.ItemCollection
import com.fthiery.catalog.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {
    private var _editCollection by mutableStateOf(ItemCollection())
    val editCollection: ItemCollection
        get() = _editCollection

    fun selectCollection(id: Long? = null) = viewModelScope.launch {
        if (id == null) _editCollection = ItemCollection()
        else repository.getCollection(id).collect {
            _editCollection = it ?: ItemCollection()
        }
    }

    fun saveCollection(
        collection: ItemCollection? = null,
        context: Context,
        onComplete: (Long) -> Unit = {}
    ) = viewModelScope.launch {
        val collectionToSave = collection ?: _editCollection
        val photo = repository.getPhoto(collectionToSave.name)
        if (photo != null) {
            collectionToSave.color = getBitmapColor(photo, context)
            collectionToSave.photo = photo.toUri()
        } else {
            val hue = Random.nextFloat() * 360
            collectionToSave.color = HSVToColor(floatArrayOf(hue,0.6f,0.9f))
        }
        collectionToSave.lastUpdated = Calendar.getInstance()
        onComplete(repository.insert(collectionToSave))
    }

    fun delete(collection: ItemCollection?, onComplete: () -> Unit = {}) = viewModelScope.launch {
        collection?.let {
            repository.delete(collection)
            onComplete()
        }
    }
}