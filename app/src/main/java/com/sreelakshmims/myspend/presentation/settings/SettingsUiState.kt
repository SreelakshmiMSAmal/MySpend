package com.sreelakshmims.myspend.presentation.settings

import java.util.Calendar

enum class DeleteMode {
    MONTHLY, YEARLY, ALL
}

data class SettingsUiState(
    val selectedDeleteMode: DeleteMode = DeleteMode.MONTHLY,
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),

    // Dialog visibility
    val showMonthYearPicker: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val showSetupPasswordDialog: Boolean = false,

    // Target metadata for deletion confirmation
    val targetLabel: String = "",
    val expenseCountToDelete: Int = 0,

    // Security password / PIN state
    val isPasswordSet: Boolean = false,
    val passwordInput: String = "",
    val passwordError: String? = null,

    // Setup / Change Password fields
    val newPasswordInput: String = "",
    val confirmPasswordInput: String = "",
    val setupPasswordError: String? = null,

    // User feedback
    val userMessage: String? = null,
    val isDeleting: Boolean = false
)
