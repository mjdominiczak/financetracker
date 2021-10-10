package com.mancode.financetracker.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mancode.financetracker.R
import com.mancode.financetracker.database.pojos.Report
import com.mancode.financetracker.ui.formatAsMoney

@Composable
fun TextReport(report: Report) {
    Column(modifier = Modifier.padding(16.dp)) {
        ReportRow(
            label = stringResource(id = R.string.registered_income),
            value = if (report.dataPresent()) report.income else null
        )
        Spacer(modifier = Modifier.height(16.dp))
        ReportRow(
            label = stringResource(id = R.string.total_outcome),
            value = if (report.dataPresent()) report.calculatedOutcome else null,
            false
        )
        Spacer(modifier = Modifier.height(16.dp))
        ReportRow(
            label = stringResource(id = R.string.registered_outcome),
            value = if (report.dataPresent()) report.registeredOutcome else null,
            false
        )
        Spacer(modifier = Modifier.height(16.dp))
        ReportRow(
            label = stringResource(id = R.string.unregistered_outcome),
            value = if (report.dataPresent()) report.unregisteredOutcome else null,
            false
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            Text(
                text = stringResource(id = R.string.monthly_balance),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.6f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = report.balance.formatAsMoney(),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.weight(0.4f)
            )
        }
    }
}

@Composable
fun ReportRow(label: String, value: Double?, isPositive: Boolean = true) {
    val textStyle = MaterialTheme.typography.body2
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = textStyle, modifier = Modifier.weight(0.6f))
        Text(
            text = value?.formatAsMoney() ?: "n/a",
            style = textStyle,
            color = colorResource(id = if (isPositive) R.color.colorPositiveValue else R.color.colorNegativeValue),
            modifier = Modifier.weight(0.4f)
        )
    }
}
