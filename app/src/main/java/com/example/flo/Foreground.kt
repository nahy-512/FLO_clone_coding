package com.example.flo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class Foreground : Service() {

    val CHANNEL_ID = "COA125"
    val NOTI_ID = 125

    // 프로그래스
    val PROGRESS_MAX = 100
    val scope = CoroutineScope(Dispatchers.Default)
    var count = 0

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

        // 백그라운드에서 프로그래스 진행
        runBackground()

        return super.onStartCommand(intent, flags, startId)
    }

    fun updateNotificationProgress(progress: Int) {
        // Notification 알림 객체 설정
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Playing")
            .setSmallIcon(R.drawable.ic_flo_app_logo)
            .setProgress(PROGRESS_MAX, progress, false)
            .build()

        // 클릭 시 SongActivity로 이동하게
        val intent = Intent(this, SongActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        notification.contentIntent = pendingIntent

        // 알림을 업데이트
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTI_ID, notification)

        // 사용하는 현재 서비스가 포그라운드로 동작한다는 것을 알려줌
        startForeground(NOTI_ID, notification)
    }

    fun runBackground() {
        // progress bar 작동
        scope.launch {
            while (count <= PROGRESS_MAX) {
                updateNotificationProgress(count)
                count++
                delay(1000)
            }
        }

//        thread(start=true) {
//            for (i in 0..PROGRESS_MAX) {
//                Thread.sleep(1000)
//                Log.d("Service", "COUNT===>$i")
//            }
//        }
    }


    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }
}