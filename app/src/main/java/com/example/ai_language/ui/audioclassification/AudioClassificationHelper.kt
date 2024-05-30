package com.example.ai_language.ui.audioclassification

import android.content.Context
import android.media.AudioRecord
import android.os.SystemClock
import android.util.Log
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.core.BaseOptions

class AudioClassificationHelper(
    val context: Context,
    val listener: AudioClassificationListener,  // 오디오 분류 결과 수신 리스너
    var currentModel: String = YAMNET_MODEL,    // 사용할 모델 파일
    var classificationThreshold: Float = DISPLAY_THRESHOLD, // 분류 임계값
    var overlap: Float = DEFAULT_OVERLAP_VALUE,     // 오디오 세그먼트 간 겹침 비율
    var numOfResults: Int = DEFAULT_NUM_OF_RESULTS, // 분류 결과 최대 수
    var currentDelegate: Int = 0,   // 사용할 딜리게이트 (기본 Cpu)
    var numThreads: Int = 2     // 사용할 스레드 수
) {
    private lateinit var classifier: AudioClassifier
    private lateinit var tensorAudio: TensorAudio
    private lateinit var recorder: AudioRecord
    private lateinit var executor: ScheduledThreadPoolExecutor

    private val classifyRunnable = Runnable {
        classifyAudio()
    }

    init {
        initClassifier()
    }

    // 분류 옵션 설정 및 분류기 초기화
    fun initClassifier() {

        // 기본 옵션 빌더
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(numThreads)  // 스레드 수 설정

        when (currentDelegate) {
            DELEGATE_CPU -> { } // Default
            DELEGATE_NNAPI -> { baseOptionsBuilder.useNnapi() }
        }

        // 분류 옵션 설정
        val options = AudioClassifier.AudioClassifierOptions.builder()
            .setScoreThreshold(classificationThreshold)
            .setMaxResults(numOfResults)
            .setBaseOptions(baseOptionsBuilder.build())
            .build()

        // 분류기 생성
        try {
            classifier = AudioClassifier.createFromFileAndOptions(context, currentModel, options)
            tensorAudio = classifier.createInputTensorAudio()
            recorder = classifier.createAudioRecord()
            startAudioClassification()
        } catch (e: IllegalStateException) {
            listener.onError(
                "Audio Classifier failed to initialize. See error logs for details"
            )

            Log.e("로그", "TFLite failed to load with error: " + e.message)
        }
    }

    // 오디오 녹음, 분류 작업 수행
    fun startAudioClassification() {
        if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            return
        }

        recorder.startRecording()
        executor = ScheduledThreadPoolExecutor(1)

        val lengthInMilliSeconds = ((classifier.requiredInputBufferSize * 1.0f) /
                classifier.requiredTensorAudioFormat.sampleRate) * 1000

        val interval = (lengthInMilliSeconds * (1 - overlap)).toLong()

        executor.scheduleAtFixedRate(
            classifyRunnable,
            0,
            interval,
            TimeUnit.MILLISECONDS)
    }

    // 오디오 분류 함수
    private fun classifyAudio() {
        tensorAudio.load(recorder)
        var inferenceTime = SystemClock.uptimeMillis()
        val output = classifier.classify(tensorAudio)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        listener.onResult(output[0].categories, inferenceTime)
    }

    // 오디오 분류 작업 중지 함수
    fun stopAudioClassification() {
        recorder.stop()
        executor.shutdownNow()
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_NNAPI = 1
        const val DISPLAY_THRESHOLD = 0.3f
        const val DEFAULT_NUM_OF_RESULTS = 2
        const val DEFAULT_OVERLAP_VALUE = 0.5f
        const val YAMNET_MODEL = "yamnet_audio.tflite"
        const val SPEECH_COMMAND_MODEL = "speech_commands.tflite"
    }
}