package com.fthiery.catalog.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fthiery.catalog.datasources.wikiparser.RootNode
import com.fthiery.catalog.datasources.wikiparser.WikiParser
import com.fthiery.catalog.getBitmapColor
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.Search
import com.fthiery.catalog.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    private var suggestions by mutableStateOf<List<Search>>(listOf())
    private var previousQuery = ""

    fun getItem(itemId: Long? = null, collectionId: Long? = null): Flow<Item> {
        Log.i("debug", "GetItem: $itemId")
        return if (itemId != null) repository.getItem(itemId)
        else flow {
            emit(Item(collectionId = collectionId ?: 0))
        }
    }

    fun save(itemToSave: Item, context: Context, onComplete: (Long) -> Unit = {}) =
        viewModelScope.launch {
            itemToSave.setColors(context)
            val itemId = repository.insert(itemToSave)
            onComplete(itemId)
        }

    fun delete(item: Item, onComplete: () -> Unit = {}) =
        viewModelScope.launch {
            repository.delete(item)
            onComplete()
        }

    fun getSuggestions(query: String, onComplete: (List<Search>) -> Unit = {}) =
        viewModelScope.launch {
            if (query.length > 5) {
                if (query != previousQuery) {
                    suggestions = repository.getSuggestions(query)
                    previousQuery = query
                }
                onComplete(suggestions)
            } else suggestions = listOf()
        }

    fun fetchDetailsFromWikipedia(
        query: String,
        onComplete: (String, Map<String, String>) -> Unit
    ) = viewModelScope.launch {
        val ignoredProperties = listOf("image", "alt", "caption", "image_size", "color")
        repository.getWikiPage(query)?.let { result ->
            // Parses the result and returns it as a string
            val wikiParser = WikiParser().parse(result.wikitext?.text ?: "") as RootNode
            // Removes white space and keeps only the first paragraph
            val description = wikiParser.toString().trim().split("\n").first()
            // Retrieves properties into a map
            val properties = wikiParser.getProperties() as MutableMap<String, String>
            ignoredProperties.forEach { properties.remove(it) }

            onComplete(description, properties)
        }
    }

    private suspend fun Item.setColors(context: Context) {
        if (photos.isNotEmpty()) {
            photos[0].path?.let {
                color = getBitmapColor(it, context)
            }
        }
    }
}