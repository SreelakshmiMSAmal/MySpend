package com.sreelakshmims.myspend.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreelakshmims.myspend.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val prefs = context.getSharedPreferences("app_security_prefs", Context.MODE_PRIVATE)

    init {
        val savedPassword = prefs.getString("security_password", null)
        _uiState.update {
            it.copy(isPasswordSet = !savedPassword.isNull_or_Empty())
        }
    }

    private fun String?.isNull_or_Empty(): Boolean = this == null || this.isEmpty()

    fun onMonthYearSelected(month: Int, year: Int) {
        _uiState.update {
            it.copy(
                selectedMonth = month,
                selectedYear = year,
                showMonthYearPicker = false
            )
        }
    }

    fun onYearSelected(year: Int) {
        _uiState.update {
            it.copy(selectedYear = year)
        }
    }

    fun showMonthYearPicker(show: Boolean) {
        _uiState.update { it.copy(showMonthYearPicker = show) }
    }

    fun onDeleteClick(mode: DeleteMode) {
        viewModelScope.launch {
            val month = _uiState.value.selectedMonth
            val year = _uiState.value.selectedYear

            val (count, label) = when (mode) {
                DeleteMode.MONTHLY -> {
                    val (startTime, endTime) = getDateRangeForMonth(month, year)
                    val cnt = expenseRepository.getExpenseCountBetweenDates(startTime, endTime)
                    val monthName = DateFormatSymbols().months[month]
                    Pair(cnt, "$monthName $year")
                }
                DeleteMode.YEARLY -> {
                    val (startTime, endTime) = getDateRangeForYear(year)
                    val cnt = expenseRepository.getExpenseCountBetweenDates(startTime, endTime)
                    Pair(cnt, "Year $year")
                }
                DeleteMode.ALL -> {
                    val cnt = expenseRepository.getTotalExpenseCount()
                    Pair(cnt, "All Recorded Transactions")
                }
            }

            _uiState.update {
                it.copy(
                    selectedDeleteMode = mode,
                    targetLabel = label,
                    expenseCountToDelete = count,
                    showConfirmDialog = true
                )
            }
        }
    }

    fun dismissConfirmDialog() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun proceedToPasswordAuth() {
        _uiState.update {
            it.copy(
                showConfirmDialog = false,
                showPasswordDialog = true,
                passwordInput = "",
                passwordError = null
            )
        }
    }

    fun dismissPasswordDialog() {
        _uiState.update {
            it.copy(
                showPasswordDialog = false,
                passwordInput = "",
                passwordError = null
            )
        }
    }

    fun onPasswordInputChanged(input: String) {
        _uiState.update {
            it.copy(
                passwordInput = input,
                passwordError = null
            )
        }
    }

    fun onVerifyPasswordAndDelete() {
        val enteredPassword = _uiState.value.passwordInput
        val savedPassword = prefs.getString("security_password", "1234") ?: "1234"

        if (enteredPassword == savedPassword || (enteredPassword.isEmpty() && savedPassword == "1234")) {
            executeDeletion()
        } else {
            _uiState.update {
                it.copy(passwordError = "Incorrect password/PIN. Try default '1234' or set your custom PIN in Settings.")
            }
        }
    }

    fun onPhoneAuthSuccess() {
        executeDeletion()
    }

    private fun executeDeletion() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, showPasswordDialog = false) }

            val mode = _uiState.value.selectedDeleteMode
            val month = _uiState.value.selectedMonth
            val year = _uiState.value.selectedYear
            val label = _uiState.value.targetLabel

            val deletedCount = when (mode) {
                DeleteMode.MONTHLY -> {
                    val (startTime, endTime) = getDateRangeForMonth(month, year)
                    expenseRepository.deleteExpensesBetweenDates(startTime, endTime)
                }
                DeleteMode.YEARLY -> {
                    val (startTime, endTime) = getDateRangeForYear(year)
                    expenseRepository.deleteExpensesBetweenDates(startTime, endTime)
                }
                DeleteMode.ALL -> {
                    expenseRepository.deleteAllExpenses()
                }
            }

            _uiState.update {
                it.copy(
                    isDeleting = false,
                    userMessage = "Successfully deleted $deletedCount expense(s) for $label."
                )
            }
        }
    }

    fun showSetupPasswordDialog(show: Boolean) {
        _uiState.update {
            it.copy(
                showSetupPasswordDialog = show,
                newPasswordInput = "",
                confirmPasswordInput = "",
                setupPasswordError = null
            )
        }
    }

    fun onNewPasswordChanged(input: String) {
        _uiState.update { it.copy(newPasswordInput = input, setupPasswordError = null) }
    }

    fun onConfirmPasswordChanged(input: String) {
        _uiState.update { it.copy(confirmPasswordInput = input, setupPasswordError = null) }
    }

    fun saveNewPassword() {
        val newPass = _uiState.value.newPasswordInput.trim()
        val confirmPass = _uiState.value.confirmPasswordInput.trim()

        if (newPass.length < 4) {
            _uiState.update { it.copy(setupPasswordError = "Password/PIN must be at least 4 characters/digits.") }
            return
        }

        if (newPass != confirmPass) {
            _uiState.update { it.copy(setupPasswordError = "Passwords do not match.") }
            return
        }

        prefs.edit().putString("security_password", newPass).apply()

        _uiState.update {
            it.copy(
                isPasswordSet = true,
                showSetupPasswordDialog = false,
                userMessage = "Security Password/PIN updated successfully."
            )
        }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun getDateRangeForMonth(month: Int, year: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis

        return Pair(startTime, endTime)
    }

    private fun getDateRangeForYear(year: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis

        return Pair(startTime, endTime)
    }
}
