package com.sreelakshmims.myspend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sreelakshmims.myspend.domain.model.NecessityLevel

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String? = null,
    val color: String? = null,
    val isDefault: Boolean = false,
    val necessity: NecessityLevel = NecessityLevel.NECESSITY
)
