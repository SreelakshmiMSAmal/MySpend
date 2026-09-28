package com.sreelakshmims.myspend.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryStyle(
    val color: Color,
    val icon: ImageVector
)

private val DefaultCategoryStyle = CategoryStyle(Color(0xFF90A4AE), Icons.Default.Category)

fun getCategoryStyle(name: String): CategoryStyle = when (name.trim().lowercase()) {
    "food"          -> CategoryStyle(Color(0xFF61E006), Icons.Default.Restaurant)
    "groceries"     -> CategoryStyle(Color(0xFFF5CC0B), Icons.Default.LocalGroceryStore)
    "shopping"      -> CategoryStyle(Color(0xFFCC0101), Icons.Default.ShoppingBag)
    "transport"     -> CategoryStyle(Color(0xFFEEA108), Icons.Default.DirectionsCar)
    "bills"         -> CategoryStyle(Color(0xFFE761AB), Icons.Default.Receipt)
    "rent"          -> CategoryStyle(Color(0xFFA60AC0), Icons.Default.Home)
    "health"        -> CategoryStyle(Color(0xFF1774E5), Icons.Default.LocalHospital)
    "entertainment" -> CategoryStyle(Color(0xFF0CE7CD), Icons.Default.Movie)
    "subscriptions" -> CategoryStyle(Color(0xFFD96106), Icons.Default.Subscriptions)
    "personal"      -> CategoryStyle(Color(0xFF0A24D3), Icons.Default.Person)
    "travel"        -> CategoryStyle(Color(0xFF9FE00A), Icons.Default.Flight)
    "education" -> CategoryStyle(Color(0xFF009688), Icons.Default.School)
    else            -> DefaultCategoryStyle
}