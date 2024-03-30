package com.example.ai_language.ui.camera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.Manifest
import android.content.ContentValues
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
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.PermissionChecker
import com.example.ai_language.ui.home.Home
import com.example.ai_language.R
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale


data class DetectionResult(val boundingBox: RectF, val text: String)


class CameraPage : AppCompatActivity() {

    companion object {
        private const val MAX_FONT_SIZE = 96F
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
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

    // 카메라 작업을 수행하기 위한 스레드 풀을 관리 (메인 스레드와 별도로 동작)
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_page)

        val homeBtn = findViewById<ImageButton>(R.id.homeButton)
        homeBtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        // Request camera permissions (카메라 권한 요청)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        //val videoBtn = findViewById<ImageButton>(R.id.CameraBtn)
        //val btn = findViewById<Button>(R.id.button2)
        //btn.setOnClickListener{ takePhoto() }
        //videoBtn.setOnClickListener{ captureVideo(videoBtn) } 실시간 번역만 사용, 녹화 X

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    // 버튼 클릭해서 카메라 전면 후면 전환하는 함수
    private fun toggleCamera() {
        val changeBtn = findViewById<ImageButton>(R.id.changeBtn)
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
    private fun captureVideo(VideoBtn: ImageButton) {
        val videoCapture = this.videoCapture ?: return
        val recImg = findViewById<ImageView>(R.id.recImg)

        //중복 녹화 방지
        VideoBtn.isEnabled = false
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
                        VideoBtn.apply {
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
                        VideoBtn.apply {
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
            val viewFinder = findViewById<PreviewView>(R.id.viewFinder)

            // 카메라 설정 초기화 (Preview 사용 사례 초기화)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider) // 미리보기 제공
            }

            // ImageAnalysis 사용 사례 초기화 -> 각 프레임마다 이미지 분석, 결과를 화면에 표시
            // 이미지 분석 작업 수행할 ImageAnalysis.Analyzer 설정
            val imageAnalysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    val bitmap = imageProxy.toBitmap()?.rotate(rotationDegrees.toFloat()) // 이미지 회전 처리
                    if (bitmap != null) {
                        runOnUiThread {
                            val detectionResults = runObjectDetection(bitmap) // 객체 감지 함수 실행
                            val imgWithResult = drawDetectionResult(bitmap, detectionResults)
                            val overlayView: ImageView = findViewById(R.id.overlayView)
                            overlayView.setImageBitmap(imgWithResult)
                        }
                    }
                    imageProxy.close() // 처리가 끝나면 ImageProxy를 닫아야 합니다.
                })
            }

            // 버튼 클릭 시 카메라 전면 후면 전환
            toggleCamera()

            try {
                // 기존에 바인딩된 사용 사례를 해제
                cameraProvider.unbindAll()

                // 카메라에 사용 사례 바인딩 (카메라를 사용하여 미리보기를 표시하고, 이미지 분석을 수행)
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)

            } catch (exc: Exception) {
                Log.e(TAG, "카메라 시작 실패", exc)
            }
        }, ContextCompat.getMainExecutor(this))
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
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // 데이터 전처리
    // CameraX에서 제공되는 이미지를 일반적인 비트맵 형식으로 변환하여
    // 이미지 처리나 모델 추론 등의 작업을 수행하는 함수
    fun ImageProxy.toBitmap(): Bitmap? {
        // 플레인 및 버퍼 가져오기
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        // 버퍼 크기 계산
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        // U and V are swapped (NV21 형식으로 이미지 데이터 병합)
        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        // YUVImage 생성 및 JPEG 압축
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    // 비트맵 이미지를 주어진 각도만큼 회전시키는 함수
    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    // 비트맵 이미지를 입력으로 받아 해당 이미지를 모델로 감지하여 위치와 확률 포함하는 목록 반환하는 함수
    private fun runObjectDetection(bitmap: Bitmap): List<DetectionResult> {

        // Step 1: Create TFLite's TensorImage object (TensorImage 객체 생성)
        val image = TensorImage.fromBitmap(bitmap)

        // Step 2: Initialize the detector object (감지기 객체 초기화)
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)           // 최대 결과수
            .setScoreThreshold(0.3f)    // 점수 임계값
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "model.tflite",   // 모델 파일 이용해서 객체 감지기 초기화(활성화)
            options
        )

        // Step 3: Feed given image to the detector
        val results = detector.detect(image)  // 주어진 이미지 객체 감지 -> 모델로 추론 -> 결과반환

        // 결과 처리
        return results.map { detection ->
            val category = detection.categories.firstOrNull() ?: return@map null
            val text = "${category.label}, ${category.score.times(100).toInt()}%"
            DetectionResult(detection.boundingBox, text)
        }.filterNotNull()
    }

    // 인자로 주어진 비트맵 이미지와 객체 감지 결과를 사용하여 감지된 객체에 대한
    // 바운딩 박스와 텍스트를 그리고 결과를 포함하는 새로운 비트맵을 생성하는 함수
    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: List<DetectionResult>
    ): Bitmap {
        // 새로운 비트맵, 캔버스 생성, 펜 설정
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        // 객체 감지 결과 목록 반복하면서 각 객체에 대해 바운딩 박스와 텍스트 그림
        detectionResults.forEach {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = it.boundingBox
            canvas.drawRect(box, pen)   // 바운딩 박스 그리기

            val tagSize = Rect(0, 0, 0, 0)

            // calculate the right font size
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.YELLOW
            pen.strokeWidth = 2F

            pen.textSize = MAX_FONT_SIZE
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F

            // 텍스트 그리기
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F), pen
            )
        }
        return outputBitmap
    }

}
