package com.ujjman.timer.pomodorotimer

sealed class Screen(val route: String) {
    object Login : Screen(route = "loginScreen")
    object Profile : Screen(route = "profileScreen")
    object MainScreen : Screen(route = "mainScreen")
    object Statistics : Screen(route = "statisticsScreen")
    object AllSessions : Screen(route = "allSessionsScreen")
}