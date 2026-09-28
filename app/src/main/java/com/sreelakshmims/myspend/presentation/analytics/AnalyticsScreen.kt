package com.sreelakshmims.myspend.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.sreelakshmims.myspend.domain.model.CategorySummary
import com.sreelakshmims.myspend.util.CurrencyUtils
import com.sreelakshmims.myspend.util.getCategoryStyle
import java.text.DateFormatSymbols
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePickerDialog by remember { mutableStateOf(false) }

    if (showDatePickerDialog) {
        MonthYearPickerDialog(
            currentMonth = uiState.selectedMonth,
            currentYear = uiState.selectedYear,
            onDismiss = { showDatePickerDialog = false },
            onConfirm = { month, year ->
                viewModel.onMonthYearSelected(month, year)
                showDatePickerDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics", fontWeight = FontWeight.Bold) },
                windowInsets = WindowInsets(0, 0, 0, 0),
                actions = {
                    IconButton(onClick = { showDatePickerDialog = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Select Month")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                PeriodSelector(
                    selectedType = uiState.selectedPeriodType,
                    onTypeChange = viewModel::onPeriodTypeChange,
                    month = uiState.selectedMonth,
                    year = uiState.selectedYear,
                    onOpenPicker = { showDatePickerDialog = true }
                )
            }

            item {
                MonthlySummaryHeader(uiState, onOpenPicker = { showDatePickerDialog = true })
            }

            item {
                SpendingTrendChart(uiState)
            }

            item {
                CategoryBreakdownCard(uiState)
            }

            item {
                PaymentMethodBreakdownCard(uiState)
            }

            item {
                SmartSpendingInsights(uiState)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun PeriodSelector(
    selectedType: PeriodType,
    onTypeChange: (PeriodType) -> Unit,
    month: Int,
    year: Int,
    onOpenPicker: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PeriodTab("Month", selectedType == PeriodType.MONTH, Modifier.weight(1f)) {
            onTypeChange(PeriodType.MONTH)
        }
        PeriodTab("Year", selectedType == PeriodType.YEAR, Modifier.weight(1f)) {
            onTypeChange(PeriodType.YEAR)
        }

        Spacer(modifier = Modifier.width(4.dp))

        Surface(
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onOpenPicker() },
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            shape = CircleShape
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedType == PeriodType.MONTH)
                        "${DateFormatSymbols().shortMonths[month]} $year"
                    else
                        "$year",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Change Date",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun PeriodTab(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MonthlySummaryHeader(state: AnalyticsUiState, onOpenPicker: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onOpenPicker() }
            ) {
                Text(
                    text = "${DateFormatSymbols().months[state.selectedMonth].uppercase()} ${state.selectedYear}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "Selected",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = CurrencyUtils.formatPaiseToRupees(state.totalSpent),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                if (state.previousMonthTotal > 0) {
                    val diff = state.totalSpent - state.previousMonthTotal
                    val percentage = (diff.toFloat() / state.previousMonthTotal * 100).toInt()
                    Text(
                        text = "  ${if (diff >= 0) "+" else ""}$percentage% vs Last Period",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (diff >= 0) Color(0xFFE57373) else Color(0xFF81C784),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            val remaining = state.monthlyLimit - state.totalSpent
            Text(
                text = "${CurrencyUtils.formatPaiseToRupees(remaining)} left from monthly limit of ${CurrencyUtils.formatPaiseToRupees(state.monthlyLimit)}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )

            LinearProgressIndicator(
                progress = { (state.totalSpent.toFloat() / state.monthlyLimit).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Daily Avg", CurrencyUtils.formatPaiseToRupees(if (state.dailyTrend.isNotEmpty()) state.totalSpent / state.dailyTrend.size else 0))
                StatItem("Transactions", state.transactionCount.toString())
                StatItem("Highest Day", CurrencyUtils.formatPaiseToRupees(state.highestDayAmount))
            }
        }
    }
}

@Composable
fun MonthYearPickerDialog(
    currentMonth: Int,
    currentYear: Int,
    onDismiss: () -> Unit,
    onConfirm: (month: Int, year: Int) -> Unit
) {
    var selectedMonth by remember { mutableIntStateOf(currentMonth) }
    var selectedYear by remember { mutableIntStateOf(currentYear) }
    val months = DateFormatSymbols().shortMonths

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Month & Year", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Year Header Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Year")
                    }
                    Text(
                        text = "$selectedYear",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { selectedYear++ }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Year")
                    }
                }

                // Month Selector Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(months.take(12)) { index, monthName ->
                        val isSelected = index == selectedMonth
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedMonth = index },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = monthName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedMonth, selectedYear) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StatItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SpendingTrendChart(state: AnalyticsUiState) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(state.dailyTrend) {
        if (state.dailyTrend.isNotEmpty()) {
            modelProducer.runTransaction {
                lineModel { series(state.dailyTrend.map { it.totalAmount.toFloat() / 100 }) }
            }
        }
    }

    val xAxisFormatter = remember(state.dailyTrend) {
        val zone = ZoneId.systemDefault()
        CartesianValueFormatter { _, x, _ ->
            val index = x.roundToInt()
            state.dailyTrend
                .getOrNull(index)
                ?.date
                ?.let { millis ->
                    Instant.ofEpochMilli(millis)
                        .atZone(zone)
                        .toLocalDate()
                        .dayOfMonth
                        .toString()
                }
                ?: index.toString()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Daily Spending Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Surface(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        "${state.dailyTrend.size} Days",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Text("Click peak points for details", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))
            val labelSpacing = (state.dailyTrend.size / 6).coerceAtLeast(1)

            if (state.dailyTrend.isNotEmpty()) {
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = xAxisFormatter,
                            itemPlacer = remember(labelSpacing) {
                                HorizontalAxis.ItemPlacer.aligned(spacing = { labelSpacing })
                            }
                        )
                    ),
                    modelProducer = modelProducer,
                    modifier = Modifier.height(200.dp)
                )
            } else {
                Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No data available")
                }
            }
        }
    }
}

@Composable
fun CategoryBreakdownCard(state: AnalyticsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Spending by Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${state.categorySummaries.size} categories sorted by expense", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp)) {
                    Text(DateFormatSymbols().shortMonths[state.selectedMonth], modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                }
            }
            val slices = state.categorySummaries
                .filter { it.totalAmountPaise > 0 }
                .map { DonutSlice(it.totalAmountPaise, getCategoryStyle(it.categoryName).color) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DonutChart(
                    modifier = Modifier.size(140.dp),
                    slices = slices,
                    totalAmount = state.totalSpent
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.categorySummaries.forEach { summary ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(getCategoryStyle(summary.categoryName).color))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(summary.categoryName, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            state.categorySummaries.forEach { summary ->
                CategoryRowItem(summary, state.totalSpent)
            }
        }
    }
}

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    slices: List<DonutSlice>,
    totalAmount: Long
) {
    val emptyColor = MaterialTheme.colorScheme.outlineVariant

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 40f
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            val total = slices.sumOf { it.value }.toFloat()

            if (total <= 0f) {
                drawArc(
                    color = emptyColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
                return@Canvas
            }

            val gap = if (slices.size > 1) 2f else 0f
            var startAngle = -90f

            slices.forEach { slice ->
                val sweep = slice.value / total * 360f
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = (sweep - gap).coerceAtLeast(0.1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
                startAngle += sweep
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                CurrencyUtils.formatPaiseToRupees(totalAmount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Total Spent", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun CategoryRowItem(summary: CategorySummary, total: Long) {
    val percentage = if (total > 0) (summary.totalAmountPaise.toFloat() / total * 100).toInt() else 0
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = Color.Gray.copy(alpha = 0.1f)) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(6.dp), tint = getCategoryStyle(summary.categoryName).color)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(summary.categoryName, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(CurrencyUtils.formatPaiseToRupees(summary.totalAmountPaise), fontWeight = FontWeight.Bold)
                Text("$percentage%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
        LinearProgressIndicator(
            progress = { percentage.toFloat() / 100f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
            color = getCategoryStyle(summary.categoryName).color,
            trackColor = Color.LightGray.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun PaymentMethodBreakdownCard(state: AnalyticsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Payment Methods", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape)) {
                state.paymentMethodSummaries.forEach { summary ->
                    val weight = if (state.totalSpent > 0) summary.totalAmountPaise.toFloat() / state.totalSpent else 0f
                    if (weight > 0) {
                        Box(modifier = Modifier.fillMaxHeight().weight(weight).background(getPaymentMethodColor(summary.paymentMethodName)))
                    }
                }
            }

            state.paymentMethodSummaries.forEach { summary ->
                val percentage = if (state.totalSpent > 0) (summary.totalAmountPaise.toFloat() / state.totalSpent * 100).toInt() else 0
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(getPaymentMethodColor(summary.paymentMethodName)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(summary.paymentMethodName, modifier = Modifier.weight(1f))
                    Text(CurrencyUtils.formatPaiseToRupees(summary.totalAmountPaise), fontWeight = FontWeight.Bold)
                    Text(" ($percentage%)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun SmartSpendingInsights(state: AnalyticsUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Smart Spending Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        state.insights.forEach { insight ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = Color.Yellow.copy(alpha = 0.1f)) {
                        Icon(
                            imageVector = when(insight.type) {
                                InsightType.CATEGORY -> Icons.Default.Category
                                InsightType.PAYMENT_METHOD -> Icons.Default.CreditCard
                                InsightType.TREND -> Icons.AutoMirrored.Filled.TrendingUp
                                InsightType.SPIKE -> Icons.Default.Warning
                            },
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp),
                            tint = Color(0xFFFFA500)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(insight.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(insight.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

fun getCategoryColor(name: String): Color {
    return when (name) {
        "Food" -> Color(0xFF61E006)
        "Groceries" -> Color(0xFFF5CC0B)
        "Shopping" -> Color(0xFFCC0101)
        "Transport" -> Color(0xFF6E2402)
        "Bills" -> Color(0xFFE761AB)
        "Rent" -> Color(0xFFA60AC0)
        "Health" -> Color(0xFF1774E5)
        "Entertainment" -> Color(0xFF0CE7CD)
        "Subscriptions" -> Color(0xFFD96106)
        "Personal" -> Color(0xFF0A24D3)
        "Travel" -> Color(0xFF673AB7)
        else -> Color(0xFF90A4AE)
    }
}

fun getPaymentMethodColor(name: String): Color {
    return when (name) {
        "UPI" -> Color(0xFF00695C)
        "Cash" -> Color(0xFFFF8F00)
        "Credit Card" -> Color(0xFF283593)
        "Debit Card" -> Color(0xFF0277BD)
        "Bank Transfer" -> Color(0xFF4CAF50)
        "Net Banking" -> Color(0xFFE91E63)
        else -> Color(0xFF455A64)
    }
}

data class DonutSlice(val value: Long, val color: Color)