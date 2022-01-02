package com.mancode.financetracker.ui.reports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.ui.formatAsMoney

@Composable
fun CategoriesList(data: List<Pair<CategoryEntity, Double>>?, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        data?.forEachIndexed { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(text = row.first.category, modifier = Modifier.weight(0.5f))
                Text(text = row.second.formatAsMoney(), modifier = Modifier.weight(0.5f))
            }
            if (index != data.indexOfLast { true }) {
                Divider()
            }
        }
    }
}

@Composable
fun CategoriesTabs(state: Int, onClick: (Int) -> Unit) {
    val titles = listOf(
        "Podsumowanie",
        stringResource(id = R.string.income),
        stringResource(id = R.string.outcome)
    )
    ScrollableTabRow(
        selectedTabIndex = state,
        backgroundColor = Color.Transparent,
        contentColor = MaterialTheme.colors.primaryVariant
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                text = { Text(text = title.uppercase()) },
                selected = state == index,
                onClick = { onClick(index) })
        }
    }
}