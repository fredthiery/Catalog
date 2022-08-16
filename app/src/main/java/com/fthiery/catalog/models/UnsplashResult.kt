package com.fthiery.catalog.models

import com.google.gson.annotations.SerializedName

data class UnsplashResult(
    @SerializedName("total") var total: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("results") var results: ArrayList<Results> = arrayListOf()
)

data class Results(
    @SerializedName("id") var id: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("width") var width: Int? = null,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("color") var color: String? = null,
    @SerializedName("blur_hash") var blurHash: String? = null,
    @SerializedName("likes") var likes: Int? = null,
    @SerializedName("liked_by_user") var likedByUser: Boolean? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("user") var user: User? = User(),
    @SerializedName("current_user_collections") var currentUserCollections: ArrayList<String> = arrayListOf(),
    @SerializedName("urls") var urls: Urls? = Urls(),
    @SerializedName("links") var links: Links? = Links()
)

data class Urls(
    @SerializedName("raw") var raw: String? = null,
    @SerializedName("full") var full: String? = null,
    @SerializedName("regular") var regular: String? = null,
    @SerializedName("small") var small: String? = null,
    @SerializedName("thumb") var thumb: String? = null
)

data class Links(
    @SerializedName("self") var self: String? = null,
    @SerializedName("html") var html: String? = null,
    @SerializedName("download") var download: String? = null
)

data class User(
    @SerializedName("id") var id: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("instagram_username") var instagramUsername: String? = null,
    @SerializedName("twitter_username") var twitterUsername: String? = null,
    @SerializedName("portfolio_url") var portfolioUrl: String? = null,
    @SerializedName("profile_image") var profileImage: ProfileImage? = ProfileImage(),
    @SerializedName("links") var links: Links? = Links()
)

data class ProfileImage(
    @SerializedName("small") var small: String? = null,
    @SerializedName("medium") var medium: String? = null,
    @SerializedName("large") var large: String? = null
)