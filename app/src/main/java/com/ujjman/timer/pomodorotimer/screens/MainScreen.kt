package com.ujjman.timer.pomodorotimer.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.ujjman.timer.pomodorotimer.ForegroundService
import com.ujjman.timer.pomodorotimer.Screen
import com.ujjman.timer.pomodorotimer.viewmodels.MainViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun MainScreen(
    navController: NavHostController, mainViewModel: MainViewModel
) {

    val title = mainViewModel.screenTitle
    var hr by remember { mutableStateOf(0) }
    var min by remember { mutableStateOf(0) }
    var sec by remember { mutableStateOf(0) }
    when (mainViewModel.showGrantOverlayDialog) {
        true -> {
            startTimer(context = navController.context, mainViewModel)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
    ) {
        Scaffold(topBar = {
            TopAppBar(title = { Text(title) })
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(modifier = Modifier
                        .padding(end = 5.dp)
                        .height(60.dp)
                        .width(60.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Cyan),
                        onClick = {
                            navController.navigate(Screen.Profile.route)
                        }) {
                        Image(
                            painter = rememberAsyncImagePainter(FirebaseAuth.getInstance().currentUser?.photoUrl),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
                Text(
                    "Set Timer",
                    textAlign = TextAlign.Center,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(top = 40.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            hr = mainViewModel.checkHr(hr)
                            if (hr == 0) {
                                Toast.makeText(
                                    navController.context,
                                    "Max of 2 hrs is only allowed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Text(
                                text = "+", style = TextStyle(fontSize = 20.sp)
                            )
                        }
                        Text(
                            text = hr.toString(), style = TextStyle(fontSize = 50.sp)
                        )
                        Button(onClick = {
                            if (hr != 0) hr -= 1
                        }) {
                            Text(
                                text = "-", style = TextStyle(fontSize = 20.sp)
                            )
                        }
                        Text(
                            text = "Hr.", style = TextStyle(fontSize = 20.sp)
                        )

                    }
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            min = mainViewModel.checkMin(min)
                        }) {
                            Text(
                                text = "+", style = TextStyle(fontSize = 20.sp)
                            )
                        }
                        Text(
                            text = min.toString(), style = TextStyle(fontSize = 50.sp)
                        )
                        Button(onClick = {
                            if (min != 0) min -= 1
                        }) {
                            Text(
                                text = "-", style = TextStyle(fontSize = 20.sp)
                            )
                        }
                        Text(
                            text = "Min.", style = TextStyle(fontSize = 20.sp)
                        )

                    }
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            sec = mainViewModel.checkSec(sec)
                        }) {
                            Text(
                                text = "+", style = TextStyle(fontSize = 20.sp)
                            )
                        }
                        Text(
                            text = sec.toString(), style = TextStyle(fontSize = 50.sp)
                        )
                        Button(onClick = {
                            if (sec != 0) sec -= 1
                        }) {
                            Text(
                                text = "-", style = TextStyle(fontSize = 20.sp)
                            )
                        }
                        Text(
                            text = "Sec.", style = TextStyle(fontSize = 20.sp)
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(top = 50.dp))
                Button(onClick = {
                    if (mainViewModel.checkStart(hr, min, sec) == 2) {

                        if (!Settings.canDrawOverlays(navController.context)) {
                            mainViewModel.showGrantOverlayDialog = true
                            return@Button
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            createTimer(navController.context, hr, min, sec)
                        }

                    } else if (mainViewModel.checkStart(hr, min, sec) == 1) {
                        Toast.makeText(
                            navController.context,
                            "Time cannot be less than 10 min",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            navController.context, "Time exceeds 2 hr limit", Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text(
                        text = "Start", style = TextStyle(fontSize = 30.sp)
                    )
                }

                Spacer(modifier = Modifier.padding(top = 40.dp))
                Button(onClick = {

                    navController.navigate(Screen.Statistics.route)
                }) {
                    Text(
                        text = "Statistics", style = TextStyle(fontSize = 30.sp)
                    )
                }

            }

        }
    }
}

@Composable
fun startTimer(context: Context, mainViewModel: MainViewModel) {
    AlertDialog(onDismissRequest = {
        mainViewModel.showGrantOverlayDialog = false
    }, confirmButton = {
        Button(onClick = {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.packageName)
            )

            ActivityCompat.startActivityForResult(
                context as Activity, intent, 1, null
            )
            mainViewModel.showGrantOverlayDialog = false

        }) {
            Text("Go To Settings")
        }
    }, title = {
        Text("Enable Overlay Permission")
    }, text = {
        Text(buildAnnotatedString {
            append("Please enable \"Allow display over other apps\" permission for application ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Pomodoro Timer")
            }
        })
    })
}

@RequiresApi(Build.VERSION_CODES.O)
fun createTimer(context: Context, hr: Int, min: Int, sec: Int) {

    val intent = Intent(context.applicationContext, ForegroundService::class.java)
    intent.putExtra("timer", "create")
    intent.putExtra("sec", sec)
    intent.putExtra("min", min)
    intent.putExtra("hr", hr)
    context.startForegroundService(intent)
}
