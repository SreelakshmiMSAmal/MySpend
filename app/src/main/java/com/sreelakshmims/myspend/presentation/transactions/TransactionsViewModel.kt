package com.sreelakshmims.myspend.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreelakshmims.myspend.domain.model.Category
import com.sreelakshmims.myspend.domain.repository.CategoryRepository
import com.sreelakshmims.myspend.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        combine(
            expenseRepository.getAllExpenses(),
            categoryRepository.getAllCategories(),
            _selectedCategory
        ) { expenses, categories, selectedCategory ->
            val filteredExpenses = if (selectedCategory != null) {
                expenses.filter { it.category.id == selectedCategory.id }
            } else {
                expenses
            }
            val grouped = filteredExpenses.groupBy { 
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it.expenseDate))
            }
            TransactionsUiState(
                groupedExpenses = grouped,
                categories = categories,
                selectedCategory = selectedCategory,
                isLoading = false
            )
        }.onEach { newState ->
            _uiState.update { newState }
        }.launchIn(viewModelScope)
    }

    fun onCategorySelect(category: Category?) {
        _selectedCategory.value = category
    }
}
