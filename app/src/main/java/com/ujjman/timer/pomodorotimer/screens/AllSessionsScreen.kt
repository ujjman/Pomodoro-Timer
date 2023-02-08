package com.ujjman.timer.pomodorotimer.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.ujjman.timer.pomodorotimer.data.SessionDetails
import com.ujjman.timer.pomodorotimer.viewmodels.AllSessionsViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AllSessionsScreen(
    navController: NavHostController, allSessionsViewModel: AllSessionsViewModel
) {

    val isDataLoaded = remember {
        mutableStateOf(false)
    }
    var listOfSessions = ArrayList<SessionDetails>()
    var whichDataToShow = 0
    LaunchedEffect(Unit) {
        allSessionsViewModel.clear()
        allSessionsViewModel.getAllSessionsData()
        allSessionsViewModel.isAllSessionsDataLoaded.observe(navController.context as LifecycleOwner,
            Observer {
                if (it) {
                    isDataLoaded.value = true
                }
            })
    }

    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = {
                    isDataLoaded.value = false
                    allSessionsViewModel.clear()
                    navController.navigateUp()
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Go back to home")
                }
            }, title = { Text(text = "All Session Details") })
        },
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text(text = "List of all Sessions", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text(text = "Sort by", fontSize = 20.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Button(onClick = {
                            if (isDataLoaded.value) {
                                whichDataToShow = 3
                                isDataLoaded.value = false
                                isDataLoaded.value = true
                            }
                        }) {
                            Text(text = "Last Month")
                        }
                        Spacer(modifier = Modifier.padding(top = 10.dp))
                        Button(onClick = {
                            if (isDataLoaded.value) {
                                whichDataToShow = 2
                                isDataLoaded.value = false
                                isDataLoaded.value = true
                            }
                        }) {
                            Text(text = "Last Week")
                        }
                    }
                    Column(modifier = Modifier.padding(10.dp)) {
                        Button(onClick = {
                            if (isDataLoaded.value) {
                                whichDataToShow = 0
                                isDataLoaded.value = false
                                isDataLoaded.value = true
                            }
                        }) {
                            Text(text = "All Sessions")
                        }
                        Spacer(modifier = Modifier.padding(top = 10.dp))
                        Button(onClick = {
                            if (isDataLoaded.value) {
                                whichDataToShow = 1
                                isDataLoaded.value = false
                                isDataLoaded.value = true
                            }
                        }) {
                            Text(text = "Today")
                        }
                    }
                }




                when (isDataLoaded.value) {
                    true -> {
                        when (whichDataToShow) {
                            0 -> {
                                listOfSessions = allSessionsViewModel.listOfAllSessions
                            }
                            1 -> {
                                listOfSessions = allSessionsViewModel.listOfTodaySessions
                            }
                            2 -> {
                                listOfSessions = allSessionsViewModel.listOfLastWeekSessions
                            }
                            3 -> {
                                listOfSessions = allSessionsViewModel.listOfLastMonthSessions
                            }
                        }
                        if (allSessionsViewModel.checkDataIsNull) {
                            showMessageForNoData(navController.context)
                        } else {
                            DisplayListOfSessions(
                                listOfSessions = listOfSessions
                            )
                        }
                    }
                    false -> {
                        Box(modifier = Modifier.padding(top = 10.dp)) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colors.onSecondary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }


            }
        }
    }
}


@Composable
fun DisplayListOfSessions(
    listOfSessions: ArrayList<SessionDetails>
) {
    Box(
        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Total sessions - ${listOfSessions.size}",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
    LazyColumn(
        modifier = Modifier.padding(10.dp), state = rememberLazyListState()
    ) {
        items(listOfSessions.size) { item ->

            val startTime = listOfSessions[item].startTime
            val endTime = listOfSessions[item].endTime
            val date = listOfSessions[item].date
            val lengthOfSession = listOfSessions[item].length
            val isCompleteOrIncomplete = listOfSessions[item].completeOrIncomplete

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = MaterialTheme.colors.onError
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Start Time")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = "End Time")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = "Length of session")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = "Date")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = "Is Completed or Not")
                    }
                    Column(
                        modifier = Modifier.padding(5.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = ":")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = ":")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = ":")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = ":")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = ":")
                    }
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "$startTime ")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = "$endTime ")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = "$lengthOfSession ")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = "$date ")
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(text = "$isCompleteOrIncomplete ")
                    }
                }

            }
        }
    }
}

private fun showMessageForNoData(context: Context) {
    Toast.makeText(context, "No data to show", Toast.LENGTH_SHORT).show()
}