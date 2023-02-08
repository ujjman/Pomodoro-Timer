package com.ujjman.timer.pomodorotimer

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ujjman.timer.pomodorotimer.OverlayStateHolder.countdownHr
import com.ujjman.timer.pomodorotimer.OverlayStateHolder.countdownMin
import com.ujjman.timer.pomodorotimer.OverlayStateHolder.countdownSeconds
import com.ujjman.timer.pomodorotimer.OverlayStateHolder.durationSeconds

class ForegroundService : Service() {
    private lateinit var overlayComponent: OverlayComponent

    private val binder = LocalBinder()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        overlayComponent = OverlayComponent(this) {
            stopSelf()
        }
    }


    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val mChannel = NotificationChannel(
            "mishra_channel", "mishra channel", NotificationManager.IMPORTANCE_DEFAULT
        )
        mChannel.description = "default channel description"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intentOrNull: Intent?, flags: Int, startId: Int): Int {

        intentOrNull?.let { intent ->
            val command = intent.getStringExtra("timer")
            when (command) {

                "create" -> {
                    val sec = intent.getIntExtra("sec", 10)
                    val min = intent.getIntExtra("min", 10)
                    val hr = intent.getIntExtra("hr", 10)
                    countdownSeconds = sec
                    countdownMin = min
                    countdownHr = hr
                    durationSeconds = (hr * 3600) + (min * 60) + sec
                    overlayComponent.showOverlay()
                }
            }
        }
        createNotificationChannel()


        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }


        val notification: Notification =
            NotificationCompat.Builder(this, "mishra_channel").setContentTitle("Pomodoro Timer")
                .setSmallIcon(R.drawable.ic_alarm).setContentIntent(pendingIntent).build()
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}