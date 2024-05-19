package com.example.ai_language.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.YuvImage
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.CameraSelector
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
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityCameraPageBinding
import com.example.ai_language.ui.home.Home
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


data class DetectionResult(val boundingBox: RectF, val text: String)

@androidx.camera.core.ExperimentalGetImage
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

    private lateinit var modelModule: Module
    private lateinit var imageNet_classes: List<String>

    // 카메라 작업을 수행하기 위한 스레드 풀을 관리 (메인 스레드와 별도로 동작)
    private lateinit var cameraExecutor: ExecutorService

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
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

    private fun loadTorchModel(fileName: String): String {
        val modelFile = File(cacheDir, fileName)

        if (modelFile.exists() && modelFile.length() > 0) {
            return modelFile.absolutePath
        }

        assets.open(fileName).use { inputStream ->
            FileOutputStream(modelFile).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        }

        return modelFile.absolutePath
    }

    private fun loadClasses(fileName: String): List<String> {
        val classes = mutableListOf<String>()
        try {
            val br = BufferedReader(InputStreamReader(assets.open(fileName)))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                // 파일에서 클래스 이름을 읽어와 리스트에 추가합니다.
                classes.add(line!!)
            }
            br.close() // 파일을 다 읽었으면 BufferedReader를 닫아줍니다.
        } catch (e: Exception) {
            // 파일을 읽는 도중 오류가 발생한 경우 예외를 처리할 수 있습니다.
            e.printStackTrace()
        }
        return classes
    }

    override fun setLayout() {
        val homeBtn = binding.homeButton
        homeBtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        imageNet_classes = loadClasses("classes2.txt")

        modelModule = LiteModuleLoader.load(loadTorchModel("best0519.torchscript.ptl")) // 모듈 로딩
        cameraExecutor = Executors.newSingleThreadExecutor()

        // 앱 시작 시 권한 확인 및 요청
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

//        val videoBtn = binding.cameraBtn
//        val btn = binding.button2
//        btn.setOnClickListener{ takePhoto() }
        //videoBtn.setOnClickListener{ captureVideo(videoBtn) } 실시간 번역만 사용, 녹화 X
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

    // 카메라를 사용하여 미리보기 표시하고 객체 감지 수행하는 함수 (카메라 화면 제공)
    private fun startCamera() {

        val viewFinder = binding.viewFinder

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            toggleCamera()

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 640)) // 모델 입력 크기에 맞춤
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer())
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))

        /* // 카메라 제공자 초기화, 리스너 등록
             // 이미지 분석 작업 수행할 ImageAnalysis.Analyzer 설정
             imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                 val rotationDegrees = imageProxy.imageInfo.rotationDegrees // 이미지 회전 각도
                 val bitmap = imageProxy.toBitmap()?.rotate(rotationDegrees.toFloat()) // 이미지 회전 처리
                 bitmap?.let { rotatedBitmap ->
                     runOnUiThread {
                         val detectionResults = runObjectDetection(rotatedBitmap) // 객체 감지 함수 실행
                         val resultTextView = binding.tx1

                         if (detectionResults.isNotEmpty()) {
                             // 객체 감지 결과가 있는 경우 텍스트뷰에 표시
                             val resultText = buildString {
                                 detectionResults.forEachIndexed { index, result ->
                                     append("${index + 1}. ${result.text}\n") // 예시: "1. Person, 80%\n"
                                 }
                             }
                             resultTextView.text = resultText
                             resultTextView.visibility = View.VISIBLE // 텍스트뷰 표시
                         } else {
                             // 객체 감지 결과가 없는 경우 텍스트뷰 숨김
                             resultTextView.visibility = View.GONE
                         }
                     }
                 }
                 imageProxy.close() // 처리가 끝나면 ImageProxy를 닫아야 합니다.
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
         }, ContextCompat.getMainExecutor(this))*/
    }

    @androidx.camera.core.ExperimentalGetImage
    private inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            val image = imageProxy.image ?: return
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees // 이미지 회전 각도
            //val bitmap = imageProxy.toBitmap()?.rotate(rotationDegrees.toFloat())

            // 이미지 전처리
            val bitmap = imageProxy.toBitmap()
            val resizedBitmap = bitmap?.let {
                Bitmap.createScaledBitmap(
                    it,
                    640,
                    640,
                    true
                )
            }

            val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                resizedBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
            )
            Log.d("로그", "inputTensor ${inputTensor.shape()[0]}")
            Log.d("로그", "inputTensor ${inputTensor.shape()[1]}")
            Log.d("로그", "inputTensor ${inputTensor.shape()[2]}")

            // 모델 추론 실행
            val outputTuple = modelModule.forward(IValue.from(inputTensor)).toTuple()
            val outputTensor = outputTuple[0].toTensor()
            val scores = outputTensor.dataAsFloatArray
            Log.d("로그", "outputTensor ${outputTensor}")

            // 가장 높은 점수를 가진 클래스 찾기
            val numClasses = imageNet_classes.size
            var maxScore = Float.NEGATIVE_INFINITY
            var maxClassIndex = -1

            for (i in 0 until numClasses) {
                Log.d("로그", "${scores[i]}")
                if (scores[i] > maxScore) {
                    maxScore = scores[i]
                    maxClassIndex = i
                }
            }

//            for (i in scores.indices step (5 + numClasses)) {
//                val classScores = scores.sliceArray((i + 5) until (i + 5 + numClasses))
//                for ((index, score) in classScores.withIndex()) {
//                    if (score > maxScore) {
//                        maxScore = score
//                        maxClassIndex = index
//                    }
//                }
//            }

            val valueList = mutableListOf<String>("사랑", "사랑해", "웃다", "최고", "살아", "무엇", "좋아")
            var valText: String? = ""
            // UI 업데이트
            runOnUiThread {
                val className = if (maxClassIndex != -1) imageNet_classes[maxClassIndex] else "Unknown"
//                when(className) {
//                    "heart" -> valText = valueList[0]
//                    "iloveyou" -> valText = valueList[1]
//                    "smile" -> valText = valueList[2]
//                    "best" -> valText = valueList[3]
//                    "live" -> valText = valueList[4]
//                    "what" -> valText = valueList[5]
//                    "fine" -> valText = valueList[6]
//
//                }
                binding.tx1.text = "Class: $className - 확률: $maxScore"
            }

            imageProxy.close()
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
                .Builder(
                    contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
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
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
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
                    if (PermissionChecker.checkSelfPermission(
                            this@CameraPage,
                            Manifest.permission.RECORD_AUDIO
                        ) ==
                        PermissionChecker.PERMISSION_GRANTED
                    ) {
                        withAudioEnabled()
                    }
                }
                .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                    // 새 녹음 시작, 리스너 등록
                    when (recordEvent) {
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
                                            "${recordEvent.error}"
                                )
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


        // 데이터 전처리
        // CameraX에서 제공되는 이미지를 일반적인 비트맵 형식으로 변환하여
        // 이미지 처리나 모델 추론 등의 작업을 수행하는 함수
        private fun ImageProxy.toBitmap(): Bitmap? {
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
        private fun Bitmap.rotate(degrees: Float): Bitmap {
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

}
