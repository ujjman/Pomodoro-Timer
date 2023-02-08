package com.ujjman.timer.pomodorotimer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object OverlayStateHolder {
    var durationSeconds by mutableStateOf<Int>(0)
    var countdownSeconds by mutableStateOf<Int>(0)
    var countdownMin by mutableStateOf<Int>(0)
    var countdownHr by mutableStateOf<Int>(0)

}