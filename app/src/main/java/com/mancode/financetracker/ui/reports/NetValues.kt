package com.mancode.financetracker.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.ui.formatAsMoney

@Composable
fun NetValueWithDate(netValue: NetValue) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = netValue.date.toString(), style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = netValue.value.formatAsMoney(), style = MaterialTheme.typography.h6)
    }
}

@Composable
fun NetValuesForReport(netValue1: NetValue, netValue2: NetValue) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            NetValueWithDate(netValue = netValue1)
            NetValueWithDate(netValue = netValue2)
        }
        val delta = netValue2.value - netValue1.value
        val percentage = delta / netValue1.value * 100
        NetValuesDelta(delta = delta, percentage = percentage)
    }
}

@Composable
fun NetValuesDelta(delta: Double, percentage: Double) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Δ = ${delta.formatAsMoney()}")
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = String.format("%.2f", percentage) + "%",
            color = when {
                delta > 0 -> colorResource(R.color.colorPositiveValue)
                delta < 0 -> colorResource(R.color.colorNegativeValue)
                else -> Color.Unspecified
            }
        )
    }
}