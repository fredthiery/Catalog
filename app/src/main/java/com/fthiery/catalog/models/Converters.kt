package com.fthiery.catalog.models

import android.net.Uri
import androidx.room.TypeConverter
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
        uri?.let {return uri.toString()}
        return null
    }

    @TypeConverter
    fun toUri(string: String?): Uri? {
        return if (string == null || string == "null") null
        else Uri.parse(string)
    }
}
