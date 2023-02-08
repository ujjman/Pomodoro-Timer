package com.ujjman.timer.pomodorotimer.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var showGrantOverlayDialog by mutableStateOf(false)
    val screenTitle = "Pomodoro Timer"

    fun checkHr(hr: Int): Int {
        return if (hr == 2) 0
        else (hr + 1)
    }

    fun checkMin(min: Int): Int {
        return if (min == 59) 0
        else (min + 1)
    }

    fun checkSec(sec: Int): Int {
        return if (sec == 59) 0
        else (sec + 1)
    }

    fun checkStart(hr: Int, min: Int, sec: Int): Int {
        val totSec = (hr * 3600) + (min * 60) + (sec)
        return if (totSec > 7200) 0
        else if (totSec < 600) 1
        else 2
    }


}