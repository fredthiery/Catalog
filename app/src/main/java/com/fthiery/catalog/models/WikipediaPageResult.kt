package com.fthiery.catalog.models

import com.google.gson.annotations.SerializedName

data class WikipediaPageResult (
    @SerializedName("parse" ) var parse : Parse? = Parse()
)

data class Wikitext (
    @SerializedName("*" ) var text : String? = null
)

data class Parse (
    @SerializedName("title"    ) var title    : String?   = null,
    @SerializedName("pageid"   ) var pageid   : Int?      = null,
    @SerializedName("wikitext" ) var wikitext : Wikitext? = Wikitext()
)