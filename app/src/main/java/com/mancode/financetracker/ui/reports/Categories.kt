package com.mancode.financetracker.ui.reports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mancode.financetracker.R
import com.mancode.financetracker.ui.formatAsMoney

@Composable
fun CategoriesList(data: List<Pair<Int, Double>>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        data.forEachIndexed { index, row ->
            Text(
                text = "${row.first}: ${row.second.formatAsMoney()}",
                modifier = Modifier.padding(4.dp)
            )
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

@Preview(showBackground = true)
@Composable
fun CategoriesPreview() {
    CategoriesList(
        data = listOf(
            Pair(1, 23.0),
            Pair(2, 23.0),
            Pair(3, 23.0),
        ),
        modifier = Modifier.padding(8.dp)
    )
}
