package com.ujjman.timer.pomodorotimer.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import com.ujjman.timer.pomodorotimer.Screen
import com.ujjman.timer.pomodorotimer.viewmodels.BarChartDataModel
import com.ujjman.timer.pomodorotimer.viewmodels.StatisticsViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StatisticsScreen(
    navController: NavHostController, statisticsViewModel: StatisticsViewModel
) {
    val checkDataLoaded = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        statisticsViewModel.clear()
        statisticsViewModel.getLastSevenDaysData()
        statisticsViewModel.checkData.observe(navController.context as LifecycleOwner, Observer {
            if (it) {
                statisticsViewModel.getNoOfSessionsPerDay()
                statisticsViewModel.getAvgDurationOfSessionPerDay()
                if (statisticsViewModel.checkDataIsNull) {
                    showMessageForNoData(navController.context)
                }
                checkDataLoaded.value = true
            }
        })
    }
    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = {
                    checkDataLoaded.value = false
                    statisticsViewModel.clear()
                    navController.navigateUp()
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Go back to home")
                }
            }, title = { Text(text = "Statistics") })
        },
    ) {
        when (checkDataLoaded.value) {

            false -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.onSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            true -> {
                BarChartScreenContent(statisticsViewModel, navController)
            }
        }
    }
}

@Composable
private fun BarChartScreenContent(
    statisticsViewModel: StatisticsViewModel, navController: NavHostController
) {

    val barChartDataModel = BarChartDataModel()
    barChartDataModel.yAxisDrawer.value = SimpleYAxisDrawer(
        labelValueFormatter = { value -> "%.0f".format(value) },
        labelTextColor = MaterialTheme.colors.onSecondary
    )
    barChartDataModel.labelDrawer = SimpleValueDrawer(
        drawLocation = SimpleValueDrawer.DrawLocation.XAxis,
        labelTextColor = MaterialTheme.colors.onSecondary
    )
    barChartDataModel.yAxisDrawer.value = SimpleYAxisDrawer(
        labelValueFormatter = { value -> "%.0f".format(value) },
        labelTextColor = MaterialTheme.colors.onSecondary
    )
    Column(
        modifier = Modifier.padding(
                horizontal = 12.dp, vertical = 8.dp
            )
    ) {
        BarChartRow(barChartDataModel)
        ChangeBars(barChartDataModel, statisticsViewModel, navController)
    }
}


@Composable
fun ChangeBars(
    barChartDataModel: BarChartDataModel,
    statisticsViewModel: StatisticsViewModel,
    navController: NavHostController
) {

    val simpleYAxisDrawerWithNoLeadingZero = SimpleYAxisDrawer(
        labelValueFormatter = { value -> "%.0f".format(value) },
        labelTextColor = MaterialTheme.colors.onSecondary
    )
    val simpleYAxisDrawerWithOneLeadingZero = SimpleYAxisDrawer(
        labelValueFormatter = { value -> "%.1f".format(value) },
        labelTextColor = MaterialTheme.colors.onSecondary
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                if (statisticsViewModel.checkDataIsNull) {
                    showMessageForNoData(navController.context)
                    return@Button
                }
                barChartDataModel.removeBar()
                barChartDataModel.yAxisDrawer.value = simpleYAxisDrawerWithNoLeadingZero
                barChartDataModel.addBar(statisticsViewModel.noOfSessionsPerDayMap)
            }, shape = CircleShape
        ) {
            Text(text = "No of sessions per day")
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                if (statisticsViewModel.checkDataIsNull) {
                    showMessageForNoData(navController.context)
                    return@Button
                }
                barChartDataModel.removeBar()
                barChartDataModel.yAxisDrawer.value = simpleYAxisDrawerWithOneLeadingZero
                barChartDataModel.addBar(statisticsViewModel.avgDurationOfSessionsPerDayMap)
            }, shape = CircleShape
        ) {
            Text(text = "Avg duration of sessions per day")
        }
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(top = 30.dp))
            Button(onClick = {
                navController.navigate(Screen.AllSessions.route)
            }) {
                Text(text = "List of all Sessions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

}

@Composable
private fun BarChartRow(barChartDataModel: BarChartDataModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(vertical = 16.dp)
    ) {
        BarChart(
            barChartData = barChartDataModel.barChartData,
            labelDrawer = barChartDataModel.labelDrawer,
            yAxisDrawer = barChartDataModel.yAxisDrawer.value
        )
    }
}

private fun showMessageForNoData(context: Context) {
    Toast.makeText(context, "No data to show", Toast.LENGTH_SHORT).show()
}
