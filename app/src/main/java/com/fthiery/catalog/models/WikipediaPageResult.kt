package com.fthiery.catalog.models

import com.google.gson.annotations.SerializedName

data class WikipediaPageResult(
    @SerializedName("parse") var wikiResult: WikiResult? = WikiResult()
)

data class Wikitext(
    @SerializedName("*") var text: String? = null
)

data class Properties(
    @SerializedName("name") var key: String? = null,
    @SerializedName("*") var value: String? = null
)

data class WikiResult(
    @SerializedName("title") var title: String? = null,
    @SerializedName("pageid") var pageid: Int? = null,
    @SerializedName("wikitext") var wikitext: Wikitext? = Wikitext(),
    @SerializedName("properties") var properties: ArrayList<Properties> = arrayListOf()
)