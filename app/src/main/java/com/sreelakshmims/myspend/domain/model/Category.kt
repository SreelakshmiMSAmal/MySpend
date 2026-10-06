package com.sreelakshmims.myspend.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String? = null,
    val color: String? = null,
    val isDefault: Boolean = false,
    val necessity: NecessityLevel = NecessityLevel.NECESSITY
)
