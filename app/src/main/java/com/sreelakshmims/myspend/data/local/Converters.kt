package com.sreelakshmims.myspend.data.local

import androidx.room.TypeConverter
import com.sreelakshmims.myspend.domain.model.NecessityLevel

class Converters {
    @TypeConverter
    fun fromNecessityLevel(level: NecessityLevel?): String {
        return level?.name ?: NecessityLevel.NECESSITY.name
    }

    @TypeConverter
    fun toNecessityLevel(value: String?): NecessityLevel {
        return try {
            if (value != null) NecessityLevel.valueOf(value) else NecessityLevel.NECESSITY
        } catch (e: Exception) {
            NecessityLevel.NECESSITY
        }
    }
}
