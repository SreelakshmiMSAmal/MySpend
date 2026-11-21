package com.sreelakshmims.myspend

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.ui.graphics.Color
import com.sreelakshmims.myspend.di.DatabaseModule
import com.sreelakshmims.myspend.domain.model.CategorySummary
import com.sreelakshmims.myspend.domain.model.NecessityLevel
import com.sreelakshmims.myspend.util.CurrencyUtils
import com.sreelakshmims.myspend.util.getCategoryStyle
import org.junit.Assert.*
import org.junit.Test

class CategoryUnitTest {

    @Test
    fun initialCategories_includesBooksCategory() {
        val categories = DatabaseModule.getInitialCategories()
        val booksCategory = categories.find { it.name == "Books" }

        assertNotNull("Books category should be included in initial categories", booksCategory)
        assertEquals("Books category should have necessity SEMI_NECESSITY", NecessityLevel.SEMI_NECESSITY, booksCategory?.necessity)
        assertTrue("Books category should be default", booksCategory?.isDefault == true)
    }

    @Test
    fun categoryStyle_returnsCorrectStyleForBooks() {
        val style = getCategoryStyle("Books")

        assertNotNull(style)
        assertEquals(Icons.Default.Book, style.icon)
        assertEquals(Color(0xFF8D6E63), style.color)
    }

    @Test
    fun categoryStyle_handlesCaseAndWhitespaceForBooks() {
        val styleLower = getCategoryStyle("books")
        val styleSpaced = getCategoryStyle("  Books  ")

        assertEquals(Icons.Default.Book, styleLower.icon)
        assertEquals(Icons.Default.Book, styleSpaced.icon)
    }

    @Test
    fun existingCategories_retainCorrectNecessityAndDefaults() {
        val categories = DatabaseModule.getInitialCategories()
        val food = categories.find { it.name == "Food" }
        val savings = categories.find { it.name == "Savings" }
        val shopping = categories.find { it.name == "Shopping" }

        assertNotNull(food)
        assertEquals(NecessityLevel.NECESSITY, food?.necessity)

        assertNotNull(savings)
        assertEquals(NecessityLevel.SAVINGS, savings?.necessity)

        assertNotNull(shopping)
        assertEquals(NecessityLevel.NOT_NECESSARY, shopping?.necessity)
    }

    @Test
    fun booksCategoryInsight_formatsTotalSpentCorrectly() {
        val booksSummary = CategorySummary(categoryName = "Books", totalAmountPaise = 150000)
        val totalSpent = 500000L
        val percentage = (booksSummary.totalAmountPaise.toFloat() / totalSpent * 100).toInt()
        val formattedAmount = CurrencyUtils.formatPaiseToRupees(booksSummary.totalAmountPaise)

        assertTrue(formattedAmount.contains("1,500"))
        assertEquals(30, percentage)
    }
}
