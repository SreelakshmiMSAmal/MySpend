package com.sreelakshmims.myspend.presentation.add_expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreelakshmims.myspend.domain.model.Expense
import com.sreelakshmims.myspend.domain.repository.CategoryRepository
import com.sreelakshmims.myspend.domain.repository.PaymentMethodRepository
import com.sreelakshmims.myspend.domain.usecase.AddExpenseUseCase
import com.sreelakshmims.myspend.util.CurrencyUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        categoryRepository.getAllCategories().onEach { categories ->
            _uiState.update { it.copy(
                categories = categories,
                selectedCategory = it.selectedCategory ?: categories.firstOrNull { c -> c.isDefault } ?: categories.firstOrNull()
            ) }
        }.launchIn(viewModelScope)

        paymentMethodRepository.getAllPaymentMethods().onEach { methods ->
            _uiState.update { it.copy(
                paymentMethods = methods,
                selectedPaymentMethod = it.selectedPaymentMethod ?: methods.firstOrNull { m -> m.isDefault } ?: methods.firstOrNull()
            ) }
        }.launchIn(viewModelScope)
    }

    fun onAmountChange(amount: String) {
        if (amount.isEmpty() || amount.toDoubleOrNull() != null) {
            _uiState.update { it.copy(amount = amount) }
        }
    }

    fun onCategorySelect(category: com.sreelakshmims.myspend.domain.model.Category) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onPaymentMethodSelect(method: com.sreelakshmims.myspend.domain.model.PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = method) }
    }

    fun onMerchantChange(merchant: String) {
        _uiState.update { it.copy(merchant = merchant) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onDateChange(date: Long) {
        _uiState.update { it.copy(date = date) }
    }

    fun saveExpense() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull() ?: 0.0
        val category = state.selectedCategory
        val paymentMethod = state.selectedPaymentMethod

        if (amount <= 0 || category == null || paymentMethod == null) {
            _uiState.update { it.copy(error = "Please fill all required fields") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val expense = Expense(
                    amountPaise = CurrencyUtils.rupeesToPaise(amount),
                    category = category,
                    paymentMethod = paymentMethod,
                    expenseDate = state.date,
                    merchant = state.merchant.ifBlank { null },
                    note = state.note.ifBlank { null }
                )
                addExpenseUseCase(expense)
                _uiState.update { it.copy(isSaved = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
