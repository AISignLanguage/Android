package com.example.ai_language.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.YuvImage
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityCameraPageBinding
import com.example.ai_language.ui.home.Home
import org.pytorch.Module
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraPage : BaseActivity<ActivityCameraPageBinding>(R.layout.activity_camera_page) {

    companion object {
        private const val MAX_FONT_SIZE = 96F
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                /*if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }*/
            }.toTypedArray()
    }

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    // 모델 관련
    private lateinit var viewFinder: PreviewView
    private lateinit var detectionInfo: TextView
    private lateinit var model: Module
    private lateinit var classNames: List<String>

    // Speech to Text
    private lateinit var recordBtn: ImageButton
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isRecording = false

    // 카메라 작업을 수행하기 위한 스레드 풀을 관리 (메인 스레드와 별도로 동작)
    private lateinit var cameraExecutor: ExecutorService

    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        // 말하기 시작할 준비가되면 호출
        override fun onReadyForSpeech(params: Bundle) {
            //binding.tvState.text = "이제 말씀하세요!"
            Log.d("로그", "이제 말씀하세요!")
        }
        // 말하기 시작했을 때 호출
        override fun onBeginningOfSpeech() {
            //binding.tvState.text = "잘 듣고 있어요."
            Log.d("로그", "잘 듣고 있어요")
        }
        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}
        // 말을 시작하고 인식이 된 단어를 buffer에 담음
        override fun onBufferReceived(buffer: ByteArray) {}
        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {
            Log.d("로그", "끝")
        }
        // 오류 발생했을 때 호출
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            //binding.tvState.text = "에러 발생: $message"
            Log.d("로그", "에러 : $message")
        }
        // 인식 결과가 준비되면 호출
        override fun onResults(results: Bundle) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in matches!!.indices) binding.ttsTextTv.text = matches[i]
            speechRecognizer.startListening(intent)
        }
        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {}
        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    // 녹음 시작
    private fun startRecord() {
        isRecording = true

        recordBtn.setImageResource(R.drawable.mike_on_orignal)
        Toast.makeText(this, "음성 녹음 시작", Toast.LENGTH_SHORT).show()

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this@CameraPage)
        speechRecognizer.setRecognitionListener(recognitionListener)    // 리스너 설정
        speechRecognizer.startListening(intent)                         // 듣기 시작
    }

    // 녹음 중지
    private fun stopRecord() {
        isRecording = false

        recordBtn.setImageResource(R.drawable.mike_off_original)
        speechRecognizer.stopListening()
        Toast.makeText(this, "음성 녹음 중지", Toast.LENGTH_SHORT).show()
        binding.ttsTextTv.text = ""
    }

    override fun setLayout() {

        // 앱 시작 시 권한 확인 및 요청
        if (!allPermissionsGranted()) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        } else {
            startCamera()
        }

        recordBtn = binding.recordBtn

        // 녹음 버튼 눌러서 음성인식 시작
        binding.recordBtn.setOnClickListener {
            if (!isRecording) {
                startRecord()
            } else {
                stopRecord()
            }
        }

        val modelLoader = ModelLoader(this)
        model = modelLoader.loadModel()
        classNames = modelLoader.loadClassNames()
        viewFinder = binding.viewFinder
        detectionInfo = binding.detectionInfo
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    // 버튼 클릭해서 카메라 전면 후면 전환하는 함수
    private fun toggleCamera() {
        val changeBtn = binding.changeBtn
        changeBtn.setOnClickListener {
            // CameraSelector 업데이트
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                startCamera() //바뀐 카메라 화면으로 카메라 재실행
            } else {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                startCamera() //바뀐 카메라 화면으로 카메라 재실행
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        speechRecognizer.stopListening()
    }

    // 카메로부터 이미지를 촬영하고 저장하는 함수
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry. (MediaStore 콘텐츠 값 생성)
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            // Android Q 이상- > RELATIVE_PATH 사용해서 저장 경로 지정.
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            /*if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }*/
        }

        // Create output options object which contains file + metadata (이미지 저장 옵션 설정)
        // 이 객체에서 원하는 출력 방법 지정 가능
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        // takePicture() 호출, 이미지 캡처 및 저장
        // outputOptions, 실행자, 이미지 저장될 때 콜백 전달
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                // 캡쳐 성공 -> 사진을 저장
                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    Log.d(TAG, "Photo capture succeeded: ${output.savedUri}")
                }
            }
        )
    }

    // CameraX VideoCapture 기능 사용하여 비디오를 촬영하고 저장하는 함수
    private fun captureVideo(videoBtn: ImageButton) {
        val videoCapture = this.videoCapture ?: return
        val recImg = binding.recImg

        //중복 녹화 방지
        videoBtn.isEnabled = false
        val curRecording = recording

        // 진행 중인 활성 녹화 세션 중지
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        // 비디오 파일의 메타데이터 설정
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }*/
        }

        // 콘텐츠의 외부 저장 위치를 옵션으로 설정하기 위해
        // 빌더를 만들고 인스턴스를 빌드
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()


        // 비디오 캡처 출력 옵션을 설정하고 녹화 영상 출력을 위한 세션 생성
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                // 오디오 사용 설정
                if (PermissionChecker.checkSelfPermission(this@CameraPage,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                // 새 녹음 시작, 리스너 등록
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        videoBtn.apply {
                            isEnabled = true
                        }
                        // 비디오 녹화가 시작되면 recImg를 VISIBLE로 설정
                        recImg.visibility = View.VISIBLE
                    }
                    // 녹화 완료
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        }
                        // 녹화 실패
                        else {
                            recording?.close()
                            recording = null
                            Log.e(
                                TAG, "Video capture ends with error: " +
                                        "${recordEvent.error}")
                        }
                        videoBtn.apply {
                            isEnabled = true
                        }
                        // 비디오 녹화가 완료되면 recImg를 INVISIBLE로 설정
                        recImg.visibility = View.INVISIBLE
                    }
                }
            }
    }

    // 카메라를 사용하여 미리보기 표시하고 객체 감지 수행하는 함수 (카메라 화면 제공)
    private fun startCamera() {

        // 카메라 제공자 초기화, 리스너 등록
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // 카메라 설정 초기화 (Preview 사용 사례 초기화)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider) // 미리보기 제공
            }

            // ImageAnalysis -> 각 프레임마다 이미지 분석, 결과를 화면에 표시
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 640))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer(model, classNames, detectionInfo, this))
                }

            // 버튼 클릭 시 카메라 전면 후면 전환
            toggleCamera()

            try {
                // 기존에 바인딩된 사용 사례를 해제
                cameraProvider.unbindAll()

                // 카메라에 사용 사례 바인딩 (카메라를 사용하여 미리보기를 표시하고, 이미지 분석을 수행)
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
                val cameraController = camera.cameraControl

                // 터치 이벤트를 통한 초점 맞추기
                binding.viewFinder.setOnTouchListener { v : View, event : MotionEvent ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            v.performClick()
                            return@setOnTouchListener true
                        }
                        MotionEvent.ACTION_UP -> {

                            val factory = binding.viewFinder.meteringPointFactory
                            val point = factory.createPoint(event.x, event.y)
                            val action = FocusMeteringAction.Builder(point).build()
                            cameraController?.startFocusAndMetering(action)
                            v.performClick()
                            return@setOnTouchListener true
                        }
                        else -> return@setOnTouchListener false
                    }
                }
            } catch (exc: Exception) {
                Log.e(TAG, "카메라 시작 실패", exc)
            }
        }, ContextCompat.getMainExecutor(this)) // 메인 스레드에서 실행
    }

}