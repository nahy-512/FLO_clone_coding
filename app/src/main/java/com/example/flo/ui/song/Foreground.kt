package com.example.flo.ui.song

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.flo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Foreground : Service() {

    val CHANNEL_ID = "COA125"
    val NOTI_ID = 125

    // 카운트 최댓값 지정
    val PROGRESS_MAX = 100

    // 코루틴 스코프와 작업자 선언
    private var job: Job? = null
    val scope = CoroutineScope(Dispatchers.Default)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // 내가 쓸 채널을 알림
        createNotificationChannel()

        // 백그라운드에서 프로그래스 진행
        runBackground()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onDestroy() {
        super.onDestroy()

        job?.cancel()
    }

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

    private fun updateNotificationProgress(progress: Int) {
        // Notification 알림 객체 설정
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Playing")
            .setSmallIcon(R.drawable.ic_flo_app_logo)
            .setContentText("count : $progress")
            .setProgress(PROGRESS_MAX, progress, false)
            .build()

        /* 알림창 클릭 시 SongActivity로 이동 */
        val intent = Intent(this, SongActivity::class.java)
        // 호출된 액티비티가 현재 태스크의 top 이미 존재할 경우 재실행되지 않음 (기존 top 재사용)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        notification.contentIntent = pendingIntent

        // 알림을 업데이트
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTI_ID, notification)

        // 사용하는 현재 서비스가 포그라운드로 동작한다는 것을 알려줌
        startForeground(NOTI_ID, notification)
    }

    private fun runBackground() {
        // job이 이미 실행 중인지 확인하고, 실행 중이 아닌 경우에만 호출
        if (job == null || job?.isCancelled == true) {
            job = scope.launch {
                try {
                    repeat(PROGRESS_MAX) { i ->
                        // progress bar 업데이트
                        updateNotificationProgress(i)
                        delay(1000)
                    }
                } finally {
                    //
                }
            }
        }

//        thread(start=true) {
//            for (i in 0..PROGRESS_MAX) {
//                Thread.sleep(1000)
//                Log.d("Service", "COUNT===>$i")
//            }
//        }
    }
}