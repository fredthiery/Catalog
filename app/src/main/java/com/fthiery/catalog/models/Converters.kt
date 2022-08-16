package com.fthiery.catalog.models

import android.net.Uri
import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        val cal = Calendar.getInstance()
        return value?.let {
            cal.timeInMillis = it
            cal
        }
    }

    @TypeConverter
    fun toTimestamp(cal: Calendar?): Long? = cal?.timeInMillis

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        uri?.let { return uri.toString() }
        return null
    }

    @TypeConverter
    fun toUri(string: String?): Uri? {
        return if (string == null || string == "null") null
        else Uri.parse(string)
    }

    @TypeConverter
    fun stringToMap(string: String): Map<String, String> {
        return Json.decodeFromString(string)
    }

    @TypeConverter
    fun mapToString(map: Map<String, String>): String {
        return Json.encodeToString(map)
    }

    @TypeConverter
    fun stringToUriList(string: String): List<Uri?> {
        val list = Json.decodeFromString<List<String>>(string)
        return list.map { toUri(it) }
    }

    @TypeConverter
    fun uriListToString(list: List<Uri>): String {
        return Json.encodeToString(list.map { fromUri(it) })
    }
}
