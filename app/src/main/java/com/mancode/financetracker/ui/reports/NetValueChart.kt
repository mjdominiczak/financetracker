package com.mancode.financetracker.ui.reports

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import co.csadev.kellocharts.gesture.ContainerScrollType
import co.csadev.kellocharts.model.*
import co.csadev.kellocharts.view.LineChartView
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.NetValue
import org.threeten.bp.LocalDate
import kotlin.math.max

@Composable
fun NetValueChart(
    modifier: Modifier = Modifier,
    netValues: List<NetValue>,
    reportRange: Pair<LocalDate, LocalDate>? = null,
) {
    val datesSorted = remember { netValues.map { it.date }.sorted() }
    val valuesSorted = remember { netValues.map { it.value.toFloat() }.sorted() }
    val dateMin = datesSorted.first()
    val dateMax = datesSorted.last()
    val valueMin = valuesSorted.first()
    val valueMax = valuesSorted.last()
    val deltaY = valueMax - valueMin

    AndroidView(
        factory = { context ->
            LineChartView(context).apply {
                setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL)
                isZoomEnabled = false

                if (netValues.isNotEmpty()) {
                    val newValues = netValues
                        .sortedBy { it.date }
                        .map { PointValue(it.date.toEpochDay().toFloat(), it.value.toFloat()) }
                        .toMutableList()
                    val axisXValues = datesSorted
                        .filter { it.dayOfMonth == 1 }
                        .map { date -> AxisValue(date.toEpochDay().toInt(), date.toString()) }
                        .toMutableList()
                    val color = ContextCompat.getColor(context, R.color.colorSecondaryVariant)
                    lineChartData = LineChartData(
                        mutableListOf(
                            Line(
                                newValues, color = color, isCubic = true, hasLabels = false,
                                hasLabelsOnlyForSelected = true, isFilled = true, pointRadius = 2
                            )
                        )
                    ).apply {
                        axisXBottom = getAxisXFormatted(axisXValues)
                    }
                    maximumViewport = Viewport(
                        left = dateMin.toEpochDay().toFloat(),
                        right = dateMax.toEpochDay().toFloat(),
                        top = valueMax + max(0.05f * deltaY, 0.01f),
                        bottom = valueMin - max(0.05f * deltaY, 0.01f)
                    )
                }
            }
        },
        modifier = modifier
    ) { chartView ->
        val targetViewport = chartView.maximumViewport.copy().apply {
            if (reportRange != null) {
                right = minOf(reportRange.second.plusDays(15), dateMax).toEpochDay().toFloat()
                left = maxOf(reportRange.first.minusDays(15), dateMin).toEpochDay().toFloat()
            } else {
                left = right - 60f
            }
        }
        chartView.setCurrentViewportWithAnimation(targetViewport)
    }
}

//private fun adjustChartViewport() {
//    val (from, to) = viewModel.getReportRange()
//    val mv = binding.netValueChart.maximumViewport
//    if (mv.left > mv.right) return
//    val x = ((to.toEpochDay().toFloat() + from.toEpochDay().toFloat()) / 2)
//        .coerceIn(mv.left, mv.right - 0.1f) // end of range needs to be open for scroll to work
//    val y = (mv.top + mv.bottom) / 2
//    binding.netValueChart.moveToWithAnimation(x, y)
//}

@Preview(showBackground = true)
@Composable
fun NetValueChartPreview() {
    NetValueChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        netValues = emptyList()
    )
}

private fun getAxisXFormatted(
    axisXValues: MutableList<AxisValue> = ArrayList(),
    isAutoGenerated: Boolean = false
) =
    Axis(axisXValues, hasLines = true, maxLabelChars = 10, isAutoGenerated = isAutoGenerated)
