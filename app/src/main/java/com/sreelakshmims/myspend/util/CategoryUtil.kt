package com.sreelakshmims.myspend.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryStyle(
    val color: Color,
    val icon: ImageVector
)

private val DefaultCategoryStyle = CategoryStyle(Color(0xFF78909C), Icons.Default.Category)

fun getCategoryStyle(name: String): CategoryStyle = when (name.trim().lowercase()) {
    "food"                 -> CategoryStyle(Color(0xFF4CAF50), Icons.Default.Restaurant)
    "groceries"            -> CategoryStyle(Color(0xFFFFC107), Icons.Default.LocalGroceryStore)
    "shopping"             -> CategoryStyle(Color(0xFFE53935), Icons.Default.ShoppingBag)
    "transport"            -> CategoryStyle(Color(0xFFFF9800), Icons.Default.DirectionsCar)
    "bills"                -> CategoryStyle(Color(0xFFEC407A), Icons.Default.Receipt)
    "rent"                 -> CategoryStyle(Color(0xFF8E24AA), Icons.Default.Home)
    "health"               -> CategoryStyle(Color(0xFF1E88E5), Icons.Default.LocalHospital)
    "entertainment"        -> CategoryStyle(Color(0xFF00ACC1), Icons.Default.Movie)
    "subscriptions"        -> CategoryStyle(Color(0xFFFF5722), Icons.Default.Subscriptions)
    "personal"             -> CategoryStyle(Color(0xFF3F51B5), Icons.Default.Person)
    "travel"               -> CategoryStyle(Color(0xFFC0CA33), Icons.Default.Flight)
    "education"            -> CategoryStyle(Color(0xFF00897B), Icons.Default.School)
    "emi"                  -> CategoryStyle(Color(0xFF5E35B1), Icons.Default.AccountBalance)
    "savings"              -> CategoryStyle(Color(0xFF1B5E20), Icons.Default.Savings)
    "insurance"            -> CategoryStyle(Color(0xFF0288D1), Icons.Default.Security)
    "parents"              -> CategoryStyle(Color(0xFFF4511E), Icons.Default.FamilyRestroom)
    "charity"              -> CategoryStyle(Color(0xFFD81B60), Icons.Default.VolunteerActivism)
    "home maintenance"     -> CategoryStyle(Color(0xFF6D4C41), Icons.Default.Handyman)
    "professional"         -> CategoryStyle(Color(0xFF455A64), Icons.Default.Work)
    "fuel & vehicle"       -> CategoryStyle(Color(0xFFB71C1C), Icons.Default.LocalGasStation)
    "gifts & celebrations" -> CategoryStyle(Color(0xFFAB47BC), Icons.Default.CardGiftcard)
    "others"               -> CategoryStyle(Color(0xFF78909C), Icons.Default.MoreHoriz)
    else                   -> DefaultCategoryStyle
}
