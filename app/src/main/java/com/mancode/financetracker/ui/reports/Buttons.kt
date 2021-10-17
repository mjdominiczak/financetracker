package com.mancode.financetracker.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalDate

@Composable
fun ReportButtons(
    date1: LocalDate,
    date2: LocalDate,
    leftEnabled: Boolean = true,
    rightEnabled: Boolean = true,
    onLeftClick: () -> Unit,
    onMiddleClick: () -> Unit,
    onRightClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        IconButton(onClick = { onLeftClick() }, enabled = leftEnabled) {
            Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = "")
        }
        OutlinedButton(
            onClick = { onMiddleClick() },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        ) {
            Text(text = "$date1 - $date2")
        }
        IconButton(onClick = { onRightClick() }, enabled = rightEnabled) {
            Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "")
        }
    }
}