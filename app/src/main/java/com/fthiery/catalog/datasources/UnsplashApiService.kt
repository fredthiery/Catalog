package com.fthiery.catalog.datasources

import com.fthiery.catalog.BuildConfig
import com.fthiery.catalog.models.UnsplashResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApiService {
    @GET("search/photos?client_id=" + BuildConfig.UNSPLASH_API_KEY)
    suspend fun searchPhotos(@Query("query") query: String?): UnsplashResult

    companion object {
        fun create(): UnsplashApiService {
            return Retrofit.Builder()
                .baseUrl("https://api.unsplash.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UnsplashApiService::class.java)
        }
    }
}