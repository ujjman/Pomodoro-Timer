package com.ujjman.timer.pomodorotimer.data

data class UserData(
    var username: String? = "",
    var photoUrl: String? = "",
    var sessions: Sessions? = null,
)