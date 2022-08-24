package com.fthiery.catalog.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.ColorUtils.HSLToColor
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fthiery.catalog.getBitmapColors
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
            val (light, dark) = getBitmapColors(photo, context)
            collectionToSave.lightColor = light
            collectionToSave.darkColor = dark
            collectionToSave.photo = photo.toUri()
        } else {
            val hue = Random.nextFloat() * 360
            collectionToSave.lightColor = HSLToColor(floatArrayOf(hue,0.7f,0.7f))
            collectionToSave.darkColor = HSLToColor(floatArrayOf(hue,0.5f,0.3f))
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