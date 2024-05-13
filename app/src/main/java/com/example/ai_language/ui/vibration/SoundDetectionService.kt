package com.example.ai_language.ui.vibration

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.ai_language.R
import com.example.ai_language.ui.camera.Classifier
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.io.File

class SoundDetectionService : Service() {
    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator
    private var audioRecorder: MediaRecorder? = null
    private var isRecording = false
    private val handler = Handler(Looper.getMainLooper())
    private val detectionInterval: Long = 1000 // 주변 소음을 체크하는 간격 (1초)

    private val detectSoundRunnable: Runnable = object : Runnable {
        override fun run() {
            Log.d("시작","서비스")
            // 녹음 상태를 체크하여 필요하면 녹음을 시작하거나 중지합니다.
            if (!isRecording) {
                startRecording()
            }

            // 최대 진폭을 체크하여 일정 수준 이상이면 진동을 발생시킵니다.
            audioRecorder?.maxAmplitude?.let { maxAmplitude ->
                Log.d(TAG, "현재 최대 진폭: $maxAmplitude") // 현재 최대 진폭값을 로그로 출
                if (maxAmplitude > AMPLITUDE_THRESHOLD) {
                    vibrateIfLoud()
                }
            }

            // 다음 소음 체크를 위해 자신을 다시 예약합니다.
            handler.postDelayed(this, detectionInterval)
        }
    }

    // AudioManager 및 Vibrator 객체를 초기화하고, 포그라운드 서비스를 시작하고,
    // 소음 감지를 위한 Runnable을 핸들러에 예약
    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        startForegroundService()
        handler.post(detectSoundRunnable)
    }

    // 서비스 종료될 때 호출되는 함수
    override fun onDestroy() {
        super.onDestroy()
        stopRecording() // 서비스 종료 시 녹음 중지
        handler.removeCallbacksAndMessages(null) // 모든 예약된 작업 취소
    }

    // 서비스가 시작될 때 호출되는 메서드
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // 소리를 녹음하기 위해 MediaRecorder를 사용하여 오디오 레코딩을 시작하는 함수
    private fun startRecording() {
        audioRecorder = MediaRecorder().apply {
            try {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                val audioFile = File.createTempFile("temp_audio", ".3gp", applicationContext.cacheDir)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
                isRecording = true
            } catch (e: Exception) {
                Log.e(TAG, "Error starting recording: ${e.message}")
            }
        }
    }

    // 녹음을 중지하고 MediaRecorder 해제하는 함수
    private fun stopRecording() {
        audioRecorder?.apply {
            stop()
            release()
        }
        audioRecorder = null
        isRecording = false
    }

    // 주변 소리가 특정 임계값을 초과하면 진동을 발생시키는 함수
    // AudioManager를 사용하여 기기의 현재 소리 모드를 확인하고, Vibrator를 사용하여 진동을 발생시킴
    private fun vibrateIfLoud() {
        Log.d("진동","bell")
        if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            val strongerVibrationAmplitude = 255 // 최대 진동 강도 값 (1 ~ 255)
            vibrator.vibrate(VibrationEffect.createOneShot(1000, strongerVibrationAmplitude)) // 500ms 동안 더 강한 진동 발생
        }
    }

    // 포그라운드 서비스를 시작하는 함수 -> 알림 표시, 앱 백그라운드 실행
    private fun startForegroundService() {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        } else {
            // NotificationChannel이 필요하지 않은 API 레벨입니다.
            ""
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Sound Detection Service")
            .setContentText("Listening for sound...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    // 포그라운드 서비스에 필요한 알림 채널 생성하는 함수 -> API 26 부터 알림 채널 필수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "sound_detection_service_channel"
        val channelName = "Sound Detection Service"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Channel for Sound Detection Service"
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    companion object {
        private const val TAG = "SoundDetectionService"
        private const val NOTIFICATION_ID = 1
        private const val AMPLITUDE_THRESHOLD = 10000 // 소음 감지를 위한 임계값, 환경에 따라 조정 필요 //10000정도가적당
    }
}
