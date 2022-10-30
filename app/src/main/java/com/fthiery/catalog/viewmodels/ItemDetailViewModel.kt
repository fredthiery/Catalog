package com.fthiery.catalog.viewmodels

import android.content.Context
import android.net.Uri
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
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    var item by mutableStateOf(Item())
    var displayPhoto by mutableStateOf<Uri?>(null)
    var suggestions by mutableStateOf<List<Search>>(listOf())
    var previousQuery = ""

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

    fun getSuggestions(query: String, onComplete: (List<Search>) -> Unit = {}) = viewModelScope.launch {
        if (query.length > 5) {
            if (query != previousQuery) {
                suggestions = repository.getSuggestions(query)
                previousQuery = query
            }
            onComplete(suggestions)
        } else suggestions = listOf()
    }

    fun getDetails(query: String, onComplete: () -> Unit = {}) = viewModelScope.launch {
        repository.getWikiPage(query)?.let { result ->
            // Parses the result and returns it as a string
            val wikiParser = WikiParser().parse(result.wikitext?.text ?: "") as RootNode
            // Removes white space and keeps only the first paragraph
            item.description = wikiParser.toString().trim().split("\n").first()

            item.properties.clear()
            item.properties.putAll(wikiParser.getProperties())
        }

//        val lines = result.split("|-", "\n")
//        val map = mutableMapOf<String, String>()
//        lines.forEach {
//            val line = it.filter { char -> char != '\'' }
//            println(line)
//            if (line.startsWith("|", true)) {
//                val row = line.drop(2).split(" = ")
//                val key = row[0].trim()
//                val value = row[1]
//                    .stripLinks()
//                    .stripComments()
//                    .stripHTMLCode()
//                    .stripNotes()
//                    .parseReleaseDates()
//                    .parseList()
//                    .trim()
//                println("$key: $value")
//                map[key] = value
//            }
//        }
//        item.properties = map
    }

    private suspend fun Item.setColors(context: Context) {
        if (photos.isNotEmpty()) {
            photos[0].path?.let {
                color = getBitmapColor(it, context)
            }
        }
    }
}