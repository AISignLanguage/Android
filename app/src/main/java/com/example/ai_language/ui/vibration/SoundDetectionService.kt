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

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        startForegroundService()
        handler.post(detectSoundRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording() // 서비스 종료 시 녹음 중지
        handler.removeCallbacksAndMessages(null) // 모든 예약된 작업 취소
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

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

    private fun stopRecording() {
        audioRecorder?.apply {
            stop()
            release()
        }
        audioRecorder = null
        isRecording = false
    }

    private fun vibrateIfLoud() {
        Log.d("진동","bell")
        if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            val strongerVibrationAmplitude = 255 // 최대 진동 강도 값 (1 ~ 255)
            vibrator.vibrate(VibrationEffect.createOneShot(1000, strongerVibrationAmplitude)) // 500ms 동안 더 강한 진동 발생
        }
    }

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
