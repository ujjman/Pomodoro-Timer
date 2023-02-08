package com.ujjman.timer.pomodorotimer.data

data class SessionDetails(
    var startTime: String? = "",
    var endTime: String? = "",
    var length: Int? = 0,
    var date: String? = "",
    var completeOrIncomplete: String? = ""
)