package com.example.ai_language.ui.vibration
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi

class SoundDetectionService : Service() {
    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator
    private var isVibrationEnabled = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // 여기에 소리 감지 및 진동 제어 로직 추가
        return START_STICKY
    }

    // 소리 감지 메서드
    private fun detectSound() {
        // 마이크를 통해 소리를 감지하는 코드 작성
        // 소리가 감지되면 vibrateIfLoud() 메서드 호출
    }

    // 큰 소리가 발생하면 진동하는 메서드
    private fun vibrateIfLoud() {
        if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)
            if (currentVolume > audioManager.getStreamMaxVolume(AudioManager.STREAM_RING) * 0.75) {
                if (isVibrationEnabled) {
                    // 진동 패턴 설정
                    val pattern = longArrayOf(0, 100, 1000)
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
