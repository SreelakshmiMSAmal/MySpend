package com.sreelakshmims.myspend.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sreelakshmims.myspend.domain.model.Expense
import com.sreelakshmims.myspend.util.getCategoryStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Transactions") }, windowInsets = WindowInsets(0, 0, 0, 0)) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChipItem(
                        selected = uiState.selectedCategory == null,
                        onClick = { viewModel.onCategorySelect(null) },
                        text = "All",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        icon = Icons.Default.Check
                    )
                }
                items(uiState.categories) { category ->
                    FilterChipItem(
                        text = category.name,
                        selected = uiState.selectedCategory == category,
                        onClick = {
                            viewModel.onCategorySelect(category)
                        },
                        color = getCategoryStyle(category.name).color,
                        icon = getCategoryStyle(category.name).icon
                    )
                }
            }

            if (uiState.groupedExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.groupedExpenses.forEach { (date, expenses) ->
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        item {
                            TransactionGroupCard(
                                transactions = expenses,
                                onTransactionClick = {}
                            )
                        }

                    }
                }
            }
        }
    }
}


@Composable
private fun FilterChipItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color,
    icon: ImageVector
) {

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary
            )
        } else {
            null
        }
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = text,
                color = color,
                fontSize = 13.sp,
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }
            )
        }
    }
}

@Composable
private fun TransactionGroupCard(
    transactions: List<Expense>,
    onTransactionClick: (Expense) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column {

            transactions.forEachIndexed { index, transaction ->

                TransactionItem(
                    transaction = transaction,
                    onClick = {
                        onTransactionClick(transaction)
                    }
                )

                if (index != transactions.lastIndex) {

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Expense,
    onClick: () -> Unit
) {

    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Category icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        getCategoryStyle(transaction.category.name).color.copy(
                            alpha = 0.12f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = getCategoryStyle(transaction.category.name).icon,
                    contentDescription = transaction.category.name,
                    tint = getCategoryStyle(transaction.category.name).color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = transaction.category.name,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = transaction.category.name,
                        color = getCategoryStyle(transaction.category.name).color,
                        fontSize = 11.sp
                    )

                    Text(
                        text = " • ",
                        //color = SecondaryText,
                        fontSize = 11.sp
                    )

                    Text(
                        text = transaction.paymentMethod.name,
                       // color = SecondaryText,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "-${formatAmount(transaction.amountPaise)}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatAmount(
    amountInPaise: Long
): String {

    val amount = amountInPaise / 100

    return "₹%,d".format(amount)
}
