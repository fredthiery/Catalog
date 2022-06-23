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
    fun fromUri(uri: Uri?): String = uri.toString()

    @TypeConverter
    fun toUri(string: String?): Uri = Uri.parse(string)
}
