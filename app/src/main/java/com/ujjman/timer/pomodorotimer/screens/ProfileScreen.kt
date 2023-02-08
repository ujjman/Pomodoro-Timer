package com.ujjman.timer.pomodorotimer.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.AsyncImagePainter.State.Loading
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.ujjman.timer.pomodorotimer.Screen
import com.ujjman.timer.pomodorotimer.viewmodels.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavHostController, profileViewModel: ProfileViewModel
) {
    var photoUrl by remember {
        mutableStateOf("")
    }
    var dialog by remember {
        mutableStateOf(false)
    }
    val painter = rememberAsyncImagePainter(
        photoUrl
    )
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var userName by remember { mutableStateOf(TextFieldValue("")) }
    var name by remember {
        mutableStateOf("")
    }
    val sharedPrefs =
        LocalContext.current.getSharedPreferences("POMODORO_PREFERENCES", Context.MODE_PRIVATE)
    val dark = sharedPrefs.getBoolean("darkTheme", false)
    var checked by remember { mutableStateOf(dark) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        GlobalScope.launch(Dispatchers.IO) {
            imageUri?.let {
                dialog = true
                profileViewModel.uploadPhoto(
                    it, "${FirebaseAuth.getInstance().currentUser!!.uid}.jpg", "image/jpg"
                ) {
                    GlobalScope.launch(Dispatchers.Main) {
                        profileViewModel.putPhotoUrlToFirestore(it)
                        profileViewModel.photoUrl.value = it
                        dialog = false
                        photoUrl = profileViewModel.photoUrl.value.toString()

                    }
                }
            }

        }
    }

    profileViewModel.photoUrl.observe(LocalContext.current as LifecycleOwner, Observer {
        photoUrl = it
    })

    profileViewModel.username.observe(LocalContext.current as LifecycleOwner, Observer {
        name = it
    })

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Profile") }, navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Rounded.ArrowBack, "")
                    }
                })
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(top = 50.dp))
                Button(modifier = Modifier
                    .height(50.dp)
                    .width(200.dp), onClick = {
                    FirebaseAuth.getInstance().signOut()

                    navController.navigate(route = Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
                ) {
                    Text(text = "Sign Out")
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(fraction = 0.9f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DarkMode, contentDescription = "Dark Mode"
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    Text(
                        text = "Enable Dark Mode", fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 55.dp))
                    Switch(checked = checked, onCheckedChange = {
                        sharedPrefs.edit().putBoolean("darkTheme", it).apply()
                        checked = it
                        if (it) profileViewModel.setDarkTheme() else profileViewModel.setLightTheme()
                    })
                }
                Spacer(modifier = Modifier.padding(top = 30.dp))
                profileViewModel.getPhotoUrl(LocalContext.current)
                Box {
                    Image(painter = painter,
                        contentDescription = "",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(200.dp)
                            .clickable {
                                launcher.launch("image/*")
                            })
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    when (painter.state is Loading || painter.state is AsyncImagePainter.State.Error || dialog) {
                        true -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colors.onSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Username : ",
                        modifier = Modifier.padding(top = 10.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = name, modifier = Modifier.padding(top = 10.dp))
                }
                Spacer(modifier = Modifier.padding(top = 30.dp))
                TextField(value = userName,
                    onValueChange = { newUsername ->
                        userName = newUsername
                    },
                    modifier = Modifier.height(50.dp),
                    textStyle = TextStyle(fontSize = 13.sp),
                    placeholder = { Text(text = "Enter new username") })
                Spacer(modifier = Modifier.padding(top = 40.dp))
                Button(modifier = Modifier
                    .height(50.dp)
                    .width(150.dp), onClick = {
                    profileViewModel.saveName(userName.text)
                }

                ) {
                    Text(text = "Save")
                }

            }
        }
    }
}
