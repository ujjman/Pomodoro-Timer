package com.ujjman.timer.pomodorotimer

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ujjman.timer.pomodorotimer.navigation.SetupNavGraph
import com.ujjman.timer.pomodorotimer.ui.theme.PomodoroTimerTheme
import com.ujjman.timer.pomodorotimer.viewmodelfactories.*
import com.ujjman.timer.pomodorotimer.viewmodels.*


class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var mAuth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val loginViewModel: LoginViewModel by viewModels {
            LoginViewModelFactory(db, mAuth)
        }
        val profileViewModel: ProfileViewModel by viewModels {
            ProfileViewModelFactory(db, mAuth)
        }
        val mainViewModel: MainViewModel by viewModels {
            MainViewModelFactory()
        }
        val statisticsViewModel: StatisticsViewModel by viewModels {
            StatisticsViewModelFactory(db, mAuth)
        }
        val allSessionsViewModel: AllSessionsViewModel by viewModels {
            AllSessionsViewModelFactory(db, mAuth)
        }
        val sharedPrefs: SharedPreferences =
            getSharedPreferences("POMODORO_PREFERENCES", MODE_PRIVATE)
        val dark = sharedPrefs.getBoolean("darkTheme", false)

        setContent {
            val darkTheme: Boolean by profileViewModel.isDarkTheme.observeAsState(dark)
            PomodoroTimerTheme(darkTheme = darkTheme) {

                navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    mainViewModel = mainViewModel,
                    profileViewModel = profileViewModel,
                    statisticsViewModel = statisticsViewModel,
                    allSessionsViewModel = allSessionsViewModel
                )
                if (mAuth.currentUser == null) {
                    navController.navigate(route = Screen.Login.route) {
                        popUpTo(Screen.MainScreen.route) {
                            inclusive = true
                        }
                    }
                }

            }
        }

    }
}

