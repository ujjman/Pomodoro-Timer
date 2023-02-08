package com.ujjman.timer.pomodorotimer.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ujjman.timer.pomodorotimer.Screen
import com.ujjman.timer.pomodorotimer.screens.*
import com.ujjman.timer.pomodorotimer.viewmodels.*

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    mainViewModel: MainViewModel,
    profileViewModel: ProfileViewModel,
    statisticsViewModel: StatisticsViewModel,
    allSessionsViewModel: AllSessionsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController, profileViewModel = profileViewModel)
        }
        composable(route = Screen.Statistics.route) {
            StatisticsScreen(
                navController = navController,
                statisticsViewModel = statisticsViewModel
            )
        }
        composable(route = Screen.AllSessions.route) {
            AllSessionsScreen(
                navController = navController,
                allSessionsViewModel = allSessionsViewModel
            )
        }
    }
}