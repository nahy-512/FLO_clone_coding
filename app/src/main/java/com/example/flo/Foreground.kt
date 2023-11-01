package com.example.flo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

class Foreground : Service() {

    val CHANNEL_ID = "COA125"
    val NOTI_ID = 125

    fun createNotificationChannel() {
        // 오레오 이상 버전부터 쓰기 때문에 버전 체크 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 서비스 채널을 만들어줌
            val serviceChannel = NotificationChannel(CHANNEL_ID, "FOREGROUND", NotificationManager.IMPORTANCE_LOW)
            // 서비스가 위에서 만들어둔 채널을 사용하겠다고 알려줘야 함
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // 내가 쓸 채널을 알림
        createNotificationChannel()
        // Notification 알림 객체 설정
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Playing")
            .setSmallIcon(R.drawable.ic_flo_app_logo)
            .build()

        // 클릭 시 SongActivity로 이동하게
        val intent = Intent(this, SongActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        builder.contentIntent = pendingIntent

        // 사용하는 현재 서비스가 포그라운드로 동작한다는 것을 알려줌
        startForeground(NOTI_ID, builder)

        runBackground()

        return super.onStartCommand(intent, flags, startId)
    }

    fun runBackground() {
        thread(start=true) {
            for (i in 0..1000) {
                Thread.sleep(1000)
                Log.d("Service", "COUNT===>$i")
            }
        }
    }


    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }
}