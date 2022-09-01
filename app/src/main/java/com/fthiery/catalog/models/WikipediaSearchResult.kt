package com.fthiery.catalog.models

import com.google.gson.annotations.SerializedName

data class WikipediaSearchResult(
    @SerializedName("batchcomplete") var batchcomplete: String? = null,
    @SerializedName("continue") var cont: Continue? = Continue(),
    @SerializedName("query") var query: Query? = Query()
)

data class Continue(
    @SerializedName("sroffset") var sroffset: Int? = null,
    @SerializedName("continue") var cont: String? = null
)

data class SearchInfo(
    @SerializedName("totalhits") var totalhits: Int? = null
)

data class Search(
    @SerializedName("ns") var ns: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("pageid") var pageid: Int? = null,
    @SerializedName("size") var size: Int? = null,
    @SerializedName("wordcount") var wordcount: Int? = null,
    @SerializedName("snippet") var snippet: String? = null,
    @SerializedName("timestamp") var timestamp: String? = null
)

data class Query(
    @SerializedName("searchinfo") var searchinfo: SearchInfo? = SearchInfo(),
    @SerializedName("search") var search: List<Search> = listOf()
)