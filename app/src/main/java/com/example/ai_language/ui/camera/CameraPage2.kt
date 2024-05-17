//package com.example.ai_language.ui.camera
//
//import android.Manifest
//import android.content.ContentValues
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.RectF
//import android.provider.MediaStore
//import android.util.Log
//import android.view.View
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageCapture
//import androidx.camera.core.ImageCaptureException
//import androidx.camera.video.MediaStoreOutputOptions
//import androidx.camera.video.Recorder
//import androidx.camera.video.Recording
//import androidx.camera.video.VideoCapture
//import androidx.camera.video.VideoRecordEvent
//import androidx.core.content.ContextCompat
//import androidx.core.content.PermissionChecker
//import com.example.ai_language.R
//import com.example.ai_language.base.BaseActivity
//import com.example.ai_language.databinding.ActivityCameraPageBinding
//import com.example.ai_language.ui.camera.mediapipe.HandsResultGlRenderer
//import com.example.ai_language.ui.home.Home
//import com.google.mediapipe.solutioncore.CameraInput
//import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
//import com.google.mediapipe.solutions.hands.Hands
//import com.google.mediapipe.solutions.hands.HandsOptions
//import com.google.mediapipe.solutions.hands.HandsResult
//import org.tensorflow.lite.Interpreter
//import java.io.IOException
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.nio.channels.FileChannel
//import java.text.SimpleDateFormat
//import java.util.Locale
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import kotlin.math.acos
//import kotlin.math.sqrt
//
//
//data class DetectionResult(val boundingBox: RectF, val text: String)
//
//class CameraPage2 : BaseActivity<ActivityCameraPageBinding>(R.layout.activity_camera_page) {
//
//    companion object {
//        private const val MAX_FONT_SIZE = 96F
//        private const val TAG = "CameraXApp"
//        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
//        private const val REQUEST_CODE_PERMISSIONS = 10
//        private val REQUIRED_PERMISSIONS =
//            mutableListOf (
//                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO
//            ).apply {
//                /*if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                }*/
//            }.toTypedArray()
//    }
//
//    // mediapipe
//    private lateinit var hands : Hands
//    private lateinit var cameraInput: CameraInput
//    private lateinit var glSurfaceView: SolutionGlSurfaceView<HandsResult>
//
//    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//    private var imageCapture: ImageCapture? = null
//    private var videoCapture: VideoCapture<Recorder>? = null
//    private var recording: Recording? = null
//
//    // 카메라 작업을 수행하기 위한 스레드 풀을 관리 (메인 스레드와 별도로 동작)
//    private lateinit var cameraExecutor: ExecutorService
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(
//            baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>, grantResults:
//        IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//
//                //glSurfaceView.post {startCamera()}
//                //glSurfaceView.visibility = View.VISIBLE
//                //startCamera()
//            } else {
//                Toast.makeText(this,
//                    "Permissions not granted by the user.",
//                    Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//    }
//
//    private fun setupStreamingModePipeline() {
//        hands = Hands(
//            this@CameraPage,
//            HandsOptions.builder()
//                .setStaticImageMode(false)
//                .setMaxNumHands(1)
//                .setRunOnGpu(true)
//                .build()
//        )
//        hands.setErrorListener { message, e -> Log.e("TAG", "MediaPipe Hands error: $message") }
//
//        cameraInput = CameraInput(this@CameraPage)
//        cameraInput.setNewFrameListener { hands.send(it) }
//
//        glSurfaceView = SolutionGlSurfaceView(this@CameraPage, hands.glContext,
//            hands.glMajorVersion)
//        glSurfaceView.setSolutionResultRenderer(HandsResultGlRenderer())
//        glSurfaceView.setRenderInputImage(true)
//
//        hands.setResultListener {
//            translate(it, this)
//            glSurfaceView.setRenderData(it)
//            glSurfaceView.requestRender()
//        }
//
//        glSurfaceView.post(this::startCamera)
//
//        // activity_main.xml에 선언한 FrameLayout에 화면을 띄우는 코드
//        binding.previewDisplayLayout.apply {
//            removeAllViewsInLayout()
//            addView(glSurfaceView)
//            glSurfaceView.visibility = View.VISIBLE
//            requestLayout()
//        }
//    }
//
//    override fun setLayout() {
//        val homeBtn = binding.homeButton
//        homeBtn.setOnClickListener {
//            val intent = Intent(this, Home::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        // 앱 시작 시 권한 확인 및 요청
//        if (!allPermissionsGranted()) {
//            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        } else {
//            setupStreamingModePipeline()
//            //startCamera()
//        }
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
////        val videoBtn = binding.cameraBtn
////        val btn = binding.button2
////        btn.setOnClickListener{ takePhoto() }
//        //videoBtn.setOnClickListener{ captureVideo(videoBtn) } 실시간 번역만 사용, 녹화 X
//    }
//
//    // 버튼 클릭해서 카메라 전면 후면 전환하는 함수
//    private fun toggleCamera() {
//        val changeBtn = binding.changeBtn
//        changeBtn.setOnClickListener {
//            // CameraSelector 업데이트 CameraInput.CameraFacing.FRONT
//            if (cameraSelector ==  CameraSelector.DEFAULT_BACK_CAMERA) {
//                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
//                startCamera() //바뀐 카메라 화면으로 카메라 재실행
//            } else {
//                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//                startCamera() //바뀐 카메라 화면으로 카메라 재실행
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cameraExecutor.shutdown()
//    }
//
//    // 카메로부터 이미지를 촬영하고 저장하는 함수
//    private fun takePhoto() {
//        // Get a stable reference of the modifiable image capture use case
//        val imageCapture = imageCapture ?: return
//
//        // Create time stamped name and MediaStore entry. (MediaStore 콘텐츠 값 생성)
//        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//            .format(System.currentTimeMillis())
//
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            // Android Q 이상- > RELATIVE_PATH 사용해서 저장 경로 지정.
//            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            /*if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }*/
//        }
//
//        // Create output options object which contains file + metadata (이미지 저장 옵션 설정)
//        // 이 객체에서 원하는 출력 방법 지정 가능
//        val outputOptions = ImageCapture.OutputFileOptions
//            .Builder(contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues)
//            .build()
//
//        // Set up image capture listener, which is triggered after photo has been taken
//        // takePicture() 호출, 이미지 캡처 및 저장
//        // outputOptions, 실행자, 이미지 저장될 때 콜백 전달
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(this),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onError(exc: ImageCaptureException) {
//                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                }
//
//                // 캡쳐 성공 -> 사진을 저장
//                override fun onImageSaved(output: ImageCapture.OutputFileResults){
//                    Log.d(TAG, "Photo capture succeeded: ${output.savedUri}")
//                }
//            }
//        )
//    }
//
//    // CameraX VideoCapture 기능 사용하여 비디오를 촬영하고 저장하는 함수
//    private fun captureVideo(videoBtn: ImageButton) {
//        val videoCapture = this.videoCapture ?: return
//        val recImg = binding.recImg
//
//        //중복 녹화 방지
//        videoBtn.isEnabled = false
//        val curRecording = recording
//
//        // 진행 중인 활성 녹화 세션 중지
//        if (curRecording != null) {
//            // Stop the current recording session.
//            curRecording.stop()
//            recording = null
//            return
//        }
//
//        // create and start a new recording session
//        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//            .format(System.currentTimeMillis())
//
//        // 비디오 파일의 메타데이터 설정
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
//            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
//            /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
//            }*/
//        }
//
//        // 콘텐츠의 외부 저장 위치를 옵션으로 설정하기 위해
//        // 빌더를 만들고 인스턴스를 빌드
//        val mediaStoreOutputOptions = MediaStoreOutputOptions
//            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//            .setContentValues(contentValues)
//            .build()
//
//
//        // 비디오 캡처 출력 옵션을 설정하고 녹화 영상 출력을 위한 세션 생성
//        recording = videoCapture.output
//            .prepareRecording(this, mediaStoreOutputOptions)
//            .apply {
//                // 오디오 사용 설정
//                if (PermissionChecker.checkSelfPermission(this@CameraPage,
//                        Manifest.permission.RECORD_AUDIO) ==
//                    PermissionChecker.PERMISSION_GRANTED)
//                {
//                    withAudioEnabled()
//                }
//            }
//            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
//                // 새 녹음 시작, 리스너 등록
//                when(recordEvent) {
//                    is VideoRecordEvent.Start -> {
//                        videoBtn.apply {
//                            isEnabled = true
//                        }
//                        // 비디오 녹화가 시작되면 recImg를 VISIBLE로 설정
//                        recImg.visibility = View.VISIBLE
//                    }
//                    // 녹화 완료
//                    is VideoRecordEvent.Finalize -> {
//                        if (!recordEvent.hasError()) {
//                            val msg = "Video capture succeeded: " +
//                                    "${recordEvent.outputResults.outputUri}"
//                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
//                                .show()
//                            Log.d(TAG, msg)
//                        }
//                        // 녹화 실패
//                        else {
//                            recording?.close()
//                            recording = null
//                            Log.e(
//                                TAG, "Video capture ends with error: " +
//                                        "${recordEvent.error}")
//                        }
//                        videoBtn.apply {
//                            isEnabled = true
//                        }
//                        // 비디오 녹화가 완료되면 recImg를 INVISIBLE로 설정
//                        recImg.visibility = View.INVISIBLE
//                    }
//                }
//            }
//    }
//
//    // 카메라를 사용하여 미리보기 표시하고 객체 감지 수행하는 함수 (카메라 화면 제공)
//    private fun startCamera() {
//
//        // 카메라 화면 전환
//        var cameraFacing: CameraInput.CameraFacing? = null
//        if (cameraSelector ==  CameraSelector.DEFAULT_BACK_CAMERA) {
//            cameraFacing = CameraInput.CameraFacing.FRONT
//        } else {
//            cameraFacing = CameraInput.CameraFacing.BACK
//        }
//
//        cameraInput.start(
//            this@CameraPage,
//            hands.glContext,
//            cameraFacing,
//            glSurfaceView.width,
//            glSurfaceView.height
//        )
//
//        // 버튼 클릭 시 카메라 전면 후면 전환
//        toggleCamera()
//
////        // 카메라 제공자 초기화, 리스너 등록
////        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
////        cameraProviderFuture.addListener({
////            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
////            val viewFinder = binding.viewFinder
////
////            // 카메라 설정 초기화 (Preview 사용 사례 초기화)
////            val preview = Preview.Builder().build().also {
////                it.setSurfaceProvider(viewFinder.surfaceProvider) // 미리보기 제공
////            }
////
////            // ImageAnalysis 사용 사례 초기화 -> 각 프레임마다 이미지 분석, 결과를 화면에 표시
////            val imageAnalysis = ImageAnalysis.Builder().build()
//
//            // 이미지 분석 작업 수행할 ImageAnalysis.Analyzer 설정
////            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
////                val rotationDegrees = imageProxy.imageInfo.rotationDegrees // 이미지 회전 각도
////                val bitmap = imageProxy.toBitmap()?.rotate(rotationDegrees.toFloat()) // 이미지 회전 처리
////                bitmap?.let { rotatedBitmap ->
////                    runOnUiThread {
////                        val detectionResults = runObjectDetection(rotatedBitmap) // 객체 감지 함수 실행
////                        val resultTextView = binding.tx1
////
////                        if (detectionResults.isNotEmpty()) {
////                            // 객체 감지 결과가 있는 경우 텍스트뷰에 표시
////                            val resultText = buildString {
////                                detectionResults.forEachIndexed { index, result ->
////                                    append("${index + 1}. ${result.text}\n") // 예시: "1. Person, 80%\n"
////                                }
////                            }
////                            resultTextView.text = resultText
////                            resultTextView.visibility = View.VISIBLE // 텍스트뷰 표시
////                        } else {
////                            // 객체 감지 결과가 없는 경우 텍스트뷰 숨김
////                            resultTextView.visibility = View.GONE
////                        }
////                    }
////                }
////                imageProxy.close() // 처리가 끝나면 ImageProxy를 닫아야 합니다.
////            }
//
//
////            try {
////                // 기존에 바인딩된 사용 사례를 해제
////                cameraProvider.unbindAll()
////
////                // 카메라에 사용 사례 바인딩 (카메라를 사용하여 미리보기를 표시하고, 이미지 분석을 수행)
////                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
////
////            } catch (exc: Exception) {
////                Log.e(TAG, "카메라 시작 실패", exc)
////            }
////        }, ContextCompat.getMainExecutor(this))
//    }
//
//    //  path 경로에 있는 tflite 모델을 불러오는 함수
//
//    // Get the TensorFlow Lite interpreter
//    private fun getTfliteInterpreter(modelName: String, context: Context): Interpreter? {
//        return try {
//            val assetFileDescriptor = context.assets.openFd(modelName)
//            val fileInputStream = assetFileDescriptor.createInputStream()
//            val model = fileInputStream.channel.map(FileChannel.MapMode.READ_ONLY, assetFileDescriptor.startOffset, assetFileDescriptor.declaredLength)
//            Interpreter(model)
//        } catch (e: IOException) {
//            Log.e("translate", "Error loading model: ${e.message}")
//            null
//        }
//    }
//
//    // 입력 및 출력 텐서의 세부 정보를 로그로 보여주는 함수
//    private fun logTensorDetails(interpreter: Interpreter) {
//        val inputTensor = interpreter.getInputTensor(0)
//        val inputShape = inputTensor.shape()
//        val inputDataType = inputTensor.dataType()
//        Log.d("TensorDetails", "Input Tensor Shape: ${inputShape.joinToString(", ")}")
//        Log.d("TensorDetails", "Input Tensor DataType: $inputDataType")
//
//        val outputTensor = interpreter.getOutputTensor(0)
//        val outputShape = outputTensor.shape()
//        val outputDataType = outputTensor.dataType()
//        Log.d("TensorDetails", "Output Tensor Shape: ${outputShape.joinToString(", ")}")
//        Log.d("TensorDetails", "Output Tensor DataType: $outputDataType")
//    }
//
//    // 입력 및 출력 버퍼를 사용하여 TensorFlow Lite 모델을 실행 함수
//    @Synchronized
//    private fun runModel(interpreter: Interpreter, inputBuffer: ByteBuffer, outputBuffer: ByteBuffer) {
//        interpreter.run(inputBuffer, outputBuffer)
//        Log.d("translate", "model is runnig")
//    }
//
//    private fun translate(result: HandsResult, context: Context) {
//        if (result.multiHandLandmarks().isEmpty()) {
//            Log.e("translate", "No hand landmarks detected.")
//            return
//        }
//
//        // 손의 관절 데이터 가져와서  x, y, z 좌표를 추출하여 joint 배열에 저장
//        val landmarkList = result.multiHandLandmarks()[0].landmarkList
//        val joint = Array(21) { FloatArray(3) }
//        for (i in 0..20) {
//            joint[i][0] = landmarkList[i].x
//            joint[i][1] = landmarkList[i].y
//            joint[i][2] = landmarkList[i].z
//        }
//
//        // v1 -> 부모 관절에서 출발하는 벡터들
//        val v1 = joint.slice(0..19).toMutableList()
//        for (i in 4..16 step 4) {
//            v1[i] = v1[0]
//        }
//
//        // v2 -> 자식 관절로 향하는 벡터들
//        val v2 = joint.slice(1..20)
//        val v = Array(20) { FloatArray(3) }
//
//        for (i in 0..19) {
//            for (j in 0..2) {
//                v[i][j] = v2[i][j] - v1[i][j]
//            }
//        }
//
//        for (i in 0..19) {
//            val norm = sqrt(v[i][0] * v[i][0] + v[i][1] * v[i][1] + v[i][2] * v[i][2])
//            for (j in 0..2) {
//                v[i][j] /= norm
//            }
//        }
//
//        val tmpv1 = mutableListOf<FloatArray>()
//        for (i in 0..18) {
//            if (i != 3 && i != 7 && i != 11 && i != 15) {
//                tmpv1.add(v[i])
//            }
//        }
//        val tmpv2 = mutableListOf<FloatArray>()
//        for (i in 1..19) {
//            if (i != 4 && i != 8 && i != 12 && i != 16) {
//                tmpv2.add(v[i])
//            }
//        }
//
//        // 두 벡터의 내적을 계산하여 einsum 배열에 저장
//        val einsum = FloatArray(15)
//        for (i in 0..14) {
//            einsum[i] = tmpv1[i][0] * tmpv2[i][0] + tmpv1[i][1] * tmpv2[i][1] + tmpv1[i][2] * tmpv2[i][2]
//        }
//
//        // 라디안으로 계산된 값을 도 단위로 변환하여 angle 배열에 저장
//        val angle = FloatArray(15)
//        for (i in 0..14) {
//            angle[i] = Math.toDegrees(acos(einsum[i]).toDouble()).toFloat()
//        }
//
//        // Check if the model is loaded correctly
//        val interpreter = getTfliteInterpreter("mog_f_model.tflite", context)
//        if (interpreter == null) {
//            Log.e("translate", "Failed to initialize TFLite interpreter.")
//            return
//        }
//
//        // Log input and output tensor details
//        logTensorDetails(interpreter)
//
//        // Check the model input tensor shape
//        val inputShape = interpreter.getInputTensor(0).shape()
//        val batchSize = inputShape[0] // 1
//        val seqLength = inputShape[1] // 30
//        val numFeatures = inputShape[2] // 99
//
//        val inputSize = 100 //= batchSize * seqLength * numFeatures     // 2970
//        //val inputSize = inputShape.reduce { acc, i -> acc * i }
//
//        // Prepare the input buffer correctly
//        val inputBuffer = ByteBuffer.allocateDirect(inputSize * 4).order(ByteOrder.nativeOrder())   // 2970 * 4 = 11880 바이트 필요 **
//
//        // Initialize input data with zeros and copy angle data
//        val inputData = FloatArray(inputSize) { 0.5f }
//
//        // Copy angle data to the input buffer in a way that expands it to the correct size
////        for (i in angle.indices) {
////            for (j in 0 until seqLength) {
////                //inputData[i + j * angle.size] = angle[i]
////                inputData[i * seqLength + j] = angle[i]
////            }
////        }
//
////        Log.d("translate","angle.indices: ${angle.indices}")
//
//        // Print inputData size and contents for debugging
//        Log.d("translate", "Input Data Size: ${inputData.size}")
//        Log.d("translate", "Input Data: ${inputData.joinToString()}")
//
//        inputBuffer.asFloatBuffer().put(inputData)
//        inputBuffer.rewind()    // 버퍼의 위치가 0으로 재설정
//
//        Log.d("translate", "inputBuffer: ${inputBuffer}")
//
//        // Prepare the output buffer
//        val outputShape = interpreter.getOutputTensor(0).shape()
//        val outputSize = outputShape.reduce { acc, i -> acc * i }
//        val outputBuffer = ByteBuffer.allocateDirect(outputSize * 4).order(ByteOrder.nativeOrder())
//        outputBuffer.rewind()   // 버퍼의 위치가 0으로 재설정
//
//
//        // 모델 실행
//        try {
//            runModel(interpreter, inputBuffer, outputBuffer)
//        } catch (e: Exception) {
//            Log.e("translate", "Model inference failed: ${e.message}")
//            return
//        }
//
//        // Convert ByteBuffer to FloatBuffer for output
//        val outputsFloatBuffer = outputBuffer.asFloatBuffer()
//        val outputs = FloatArray(outputSize)
//        outputsFloatBuffer.get(outputs)
//
//        // Get the index of the maximum value in the output
//        val maxIndex = outputs.indices.maxByOrNull { outputs[it] } ?: -1
//
//        // Log the results
//        Log.d("translate", "Max index: $maxIndex, Max value: ${outputs[maxIndex]}")
//    }
//
//
//}