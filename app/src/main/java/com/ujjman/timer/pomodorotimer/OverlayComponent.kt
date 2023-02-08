package com.ujjman.timer.pomodorotimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.input.InputManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ujjman.timer.pomodorotimer.OverlayStateHolder.countdownHr
import com.ujjman.timer.pomodorotimer.OverlayStateHolder.countdownMin
import com.ujjman.timer.pomodorotimer.OverlayStateHolder.countdownSeconds
import com.ujjman.timer.pomodorotimer.OverlayStateHolder.durationSeconds
import com.ujjman.timer.pomodorotimer.data.SessionDetails
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class OverlayComponent(
    private val context: Context, private val stopService: () -> Unit
) {

    private val player: MediaPlayer
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var startTime: String = ""
    private var endTime: String = ""
    private var lengthOfSession: Int = 0
    private var formatter: DateTimeFormatter? = null
    private var date: String = ""
    private var completeOrIncomplete: String = ""
    var isTimerOverlayShowing = false

    private val clickTargetOverlay = OverlayViewHolder(
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            0, // todo place default position
            0,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.OPAQUE
        ), context
    )

    private val fullscreenOverlay = OverlayViewHolder(
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.OPAQUE
        ), context
    )

    init {
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        player = MediaPlayer.create(context, alarmSound)
        player.isLooping = true
        fullscreenOverlay.params.alpha = 1f
        val inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            fullscreenOverlay.params.alpha = inputManager.maximumObscuringOpacityForTouch
        }
        setContentClickTargetOverlay()
        setContentFullscreenOverlay()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showOverlay() {
        if (isTimerOverlayShowing) {
            return
        }
        formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        startTime = LocalDateTime.now().format(formatter)
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        date = LocalDateTime.now().format(formatter)
        windowManager.addView(clickTargetOverlay.view, clickTargetOverlay.params)
        windowManager.addView(fullscreenOverlay.view, fullscreenOverlay.params)
        isTimerOverlayShowing = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun endService() {

        player.pause()
        createSession()
        windowManager.removeView(clickTargetOverlay.view)
        windowManager.removeView(fullscreenOverlay.view)


        fullscreenOverlay.view.disposeComposition()
        clickTargetOverlay.view.disposeComposition()
        isTimerOverlayShowing = false
        stopService()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setContentClickTargetOverlay() {
        clickTargetOverlay.view.setContent {
            var showDialogToCancel by remember {
                mutableStateOf(false)
            }
            var showDialogForDone by remember {
                mutableStateOf(false)
            }
            var totsec: Int = durationSeconds
            var second by remember {
                mutableStateOf(countdownSeconds)
            }
            var minute by remember {
                mutableStateOf(countdownMin)
            }
            var hour by remember {
                mutableStateOf(countdownHr)
            }
            LaunchedEffect(Unit) {
                val timer = object : CountDownTimer(totsec * 1000L, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        totsec--
                        hour = totsec / 3600
                        minute = (totsec / 60) - (hour * 60)
                        if (second - 1 == -1) second = 59
                        else second--
                        lengthOfSession++

                    }

                    override fun onFinish() {
                        player.start()
                        showDialogForDone = true
                        formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                        endTime = LocalDateTime.now().format(formatter)
                    }
                }.start()
            }


            Box(
                modifier = Modifier.background(Color.Yellow)
            ) {
                Column(modifier = Modifier.align(Alignment.Center)) {

                    Text(
                        text = "$hour : $minute : $second",
                        fontSize = 40.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.padding(top = 20.dp))


                    when (showDialogForDone) {

                        true -> {
                            createNotificationChannel()

                            Button(
                                onClick = {
                                    cancelNotification(context, 1234)
                                    completeOrIncomplete = "complete"
                                    endService()
                                },
                                modifier = Modifier
                                    .align(CenterHorizontally)
                                    .padding(10.dp)
                                    .width(150.dp)
                                    .height(60.dp)

                            ) {
                                Text(text = "Done")
                            }
                            Button(
                                onClick = {
                                    cancelNotification(context, 1234)
                                    completeOrIncomplete = "complete"
                                    endService()
                                    share()
                                },
                                modifier = Modifier
                                    .align(CenterHorizontally)
                                    .padding(10.dp)
                                    .width(150.dp)
                                    .height(60.dp)

                            ) {
                                Text(text = "Share")
                            }
                        }

                        false -> {
                            when (showDialogToCancel) {
                                false -> Button(
                                    onClick = {
                                        showDialogToCancel = true
                                    },
                                    modifier = Modifier
                                        .align(CenterHorizontally)
                                        .width(150.dp)
                                        .height(60.dp)

                                ) {
                                    Text(text = "Cancel")
                                }
                            }
                            when (showDialogToCancel) {
                                true -> {
                                    Text(
                                        text = "Are you sure to cancel?",
                                        fontSize = 18.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                                                endTime = LocalDateTime.now().format(formatter)
                                                completeOrIncomplete = "incomplete"
                                                endService()
                                            }, modifier = Modifier.padding(20.dp)
                                        ) {
                                            Text(text = "Yes")
                                        }
                                        Button(
                                            onClick = { showDialogToCancel = false },
                                            modifier = Modifier.padding(20.dp)
                                        ) {
                                            Text(text = "No")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setContentFullscreenOverlay() {
        fullscreenOverlay.view.setContent {
            square()
        }
    }

    private fun share() {

        val dataToShare =
            "Pomodoro Timer \nStarting Time : $startTime \nEnd Time : $endTime \nLength of session : $lengthOfSession \nDate : $date"

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, dataToShare)
        sharingIntent.type = "text/plain"

        val chooserIntent = Intent.createChooser(sharingIntent, "Share To:")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }

    private fun cancelNotification(ctx: Context, notifyId: Int) {
        val ns = NOTIFICATION_SERVICE
        val nMgr = ctx.getSystemService(ns) as NotificationManager
        nMgr.cancel(notifyId)
    }

    @Composable
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pomodoro Timer"
            val descriptionText = "Timer ended"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("mishra", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder =
            NotificationCompat.Builder(context, "mishra").setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Timer ended")
                .setContentText("Click on Done to remove the timer screen")
                .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(1234, builder.build())
        }
    }

    private fun createSession() {
        val session: SessionDetails =
            SessionDetails(startTime, endTime, lengthOfSession, date, completeOrIncomplete)
        addSession(session)

    }

    @Composable
    fun square() {
        Surface(
            modifier = Modifier.fillMaxSize(), color = Color.Transparent
        ) {}
    }
}

