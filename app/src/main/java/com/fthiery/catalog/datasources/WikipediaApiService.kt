package com.fthiery.catalog.datasources

import com.fthiery.catalog.models.WikipediaPageResult
import com.fthiery.catalog.models.WikipediaSearchResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApiService {
    @GET("api.php?action=query&list=search&utf8&format=json")
    suspend fun search(@Query("srsearch") query: String): WikipediaSearchResult

    @GET("api.php?action=parse&prop=wikitext&utf8&format=json&section=0")
    suspend fun get(@Query("page") query:String): WikipediaPageResult

    companion object {
        fun create(): WikipediaApiService {
            return Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/w/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WikipediaApiService::class.java)
        }
    }
}