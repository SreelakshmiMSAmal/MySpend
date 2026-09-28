package com.sreelakshmims.myspend.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sreelakshmims.myspend.presentation.transactions.TransactionItem
import com.sreelakshmims.myspend.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

@Composable
fun DashboardScreen(
    onAddExpenseClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
                //.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = SimpleDateFormat("MMMM yyyy", LocalLocale.current.platformLocale).format(Date()),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                SummaryCard(uiState)
            }

            item {
                Text(
                    text = "Recent Expenses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.recentExpenses) { expense ->
                TransactionItem(
                    expense
                ) {}
            }
        }
    }
}

@Composable
fun SummaryCard(state: DashboardUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Total Spent This Month", style = MaterialTheme.typography.labelMedium)
            Text(
                text = CurrencyUtils.formatPaiseToRupees(state.totalSpentMonth),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryItem("Today", CurrencyUtils.formatPaiseToRupees(state.spentToday))
                SummaryItem("This Week", CurrencyUtils.formatPaiseToRupees(state.spentThisWeek))
                SummaryItem("Avg/Day", CurrencyUtils.formatPaiseToRupees(state.averagePerDay))
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, amount: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(amount, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
