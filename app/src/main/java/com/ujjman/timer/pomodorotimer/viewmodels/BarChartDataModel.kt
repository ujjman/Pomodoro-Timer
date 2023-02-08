package com.ujjman.timer.pomodorotimer.viewmodels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import java.util.*
import kotlin.collections.HashMap

class BarChartDataModel {
    private var colors = mutableListOf(
        Color(0XFFF44336),
        Color(0XFFE91E63),
        Color(0XFF9C27B0),
        Color(0XFF673AB7),
        Color(0XFF3F51B5),
        Color(0XFF03A9F4),
        Color(0XFF009688),
        Color(0XFFCDDC39),
        Color(0XFFFFC107),
        Color(0XFFFF5722),
        Color(0XFF795548),
        Color(0XFF9E9E9E),
        Color(0XFF607D8B)
    )
    val yAxisDrawer =
        mutableStateOf(SimpleYAxisDrawer(labelValueFormatter = { value -> "%.0f".format(value) }))
    var labelDrawer: SimpleValueDrawer by mutableStateOf(SimpleValueDrawer(drawLocation = SimpleValueDrawer.DrawLocation.XAxis))
    var barChartData by mutableStateOf(
        BarChartData(
            bars = listOf(

            )
        )
    )

    val bars: List<BarChartData.Bar>
        get() = barChartData.bars
    var labelLocation: SimpleValueDrawer.DrawLocation = SimpleValueDrawer.DrawLocation.XAxis
        set(value) {
            val color = when (value) {
                SimpleValueDrawer.DrawLocation.Inside -> Color.White
                SimpleValueDrawer.DrawLocation.Outside, SimpleValueDrawer.DrawLocation.XAxis -> Color.Black
            }

            labelDrawer = SimpleValueDrawer(
                drawLocation = value, labelTextColor = color
            )
            field = value
        }

    fun addBar(noOfSessionsPerDayMap: HashMap<String?, Int?>) {
        val sortedMap: MutableMap<String, Int> = TreeMap(noOfSessionsPerDayMap)
        for (data in sortedMap) {
            barChartData = barChartData.copy(bars = bars.toMutableList().apply {
                add(
                    BarChartData.Bar(
                        label = data.key, value = (data.value.times(1f)), color = randomColor()
                    )
                )
            })
        }
    }

    @JvmName("addBar1")
    fun addBar(avgDurationOfSessionsPerDayMap: HashMap<String?, Float?>) {
        val sortedMap: MutableMap<String, Float> = TreeMap(avgDurationOfSessionsPerDayMap)
        for (data in sortedMap) {
            barChartData = barChartData.copy(bars = bars.toMutableList().apply {
                add(
                    BarChartData.Bar(
                        label = data.key, value = (data.value.times(1f)), color = randomColor()
                    )
                )
            })
        }
    }

    fun removeBar() {
        barChartData = barChartData.copy(bars = bars.toMutableList().apply {
            for (lastBar in bars) {
                colors.add(lastBar.color)
                remove(lastBar)
            }

        })
    }

    private fun randomColor(): Color {
        val randomIndex = (Math.random() * colors.size).toInt()
        val color = colors[randomIndex]
        colors.removeAt(randomIndex)

        return color
    }
}