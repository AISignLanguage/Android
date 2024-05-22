package com.example.ai_language.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityStreamingBinding
import com.google.common.util.concurrent.ListenableFuture
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URISyntaxException
import java.nio.ByteBuffer
import java.util.concurrent.ExecutionException


class StreamingActivity : BaseActivity<ActivityStreamingBinding>(R.layout.activity_streaming) {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private lateinit var socket: Socket
    private val handler = Handler(Looper.getMainLooper())
    private var isStreaming = false

    override fun onStart() {
        super.onStart()
        socket = IO.socket("http://34.64.202.194:5000")
        socket.connect()
        socket.on(Socket.EVENT_CONNECT) {
            Log.d("MainActivity", "Connected to server")
        }
    }
    override fun setLayout() {
        try {
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                Log.e("MainActivity", "Camera permission denied")
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.captureButton.setOnClickListener {
            Log.d("MainActivity", "Capture button clicked")
            takePhoto()
        }

        binding.startStreamingButton.setOnClickListener {
            Log.d("MainActivity", "Start streaming button clicked")
            isStreaming = true
            streamVideo()
        }

        binding.stopStreamingButton.setOnClickListener {
            Log.d("MainActivity", "Stop streaming button clicked")
            isStreaming = false
        }
    }


    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            try {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
                Log.d("MainActivity", "Camera started successfully")
            } catch (e: ExecutionException) {
                Log.e("MainActivity", "Error starting camera: ${e.message}", e)
            } catch (e: InterruptedException) {
                Log.e("MainActivity", "Error starting camera: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()

        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val previewView: PreviewView = findViewById(R.id.previewView)
        preview.setSurfaceProvider(previewView.surfaceProvider)

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        Log.d("MainActivity", "Preview bound to lifecycle")
    }

    private fun takePhoto() {
        if (!::imageCapture.isInitialized) {
            Log.e("MainActivity", "ImageCapture not initialized")
            return
        }

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    var bitmap = imageToBitmap(image)
                    bitmap = rotateBitmapIfNeeded(bitmap, image)
                    Log.d("MainActivity", "Photo captured")
                    uploadFrame(bitmap)
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("MainActivity", "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    private fun imageToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        return Bitmap.createScaledBitmap(
            originalBitmap,
            originalBitmap.width / 4,
            originalBitmap.height / 4,
            true
        ) // 이미지 크기 줄이기
    }

    private fun rotateBitmapIfNeeded(bitmap: Bitmap, image: ImageProxy): Bitmap {
        val rotationDegrees = image.imageInfo.rotationDegrees
        if (rotationDegrees == 0) return bitmap

        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun streamVideo() {
        if (!::imageCapture.isInitialized) {
            Log.e("MainActivity", "ImageCapture not initialized")
            return
        }

        handler.post(object : Runnable {
            override fun run() {
                if (isStreaming) {
                               imageCapture.takePicture(
                        ContextCompat.getMainExecutor(this@StreamingActivity),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                var bitmap = imageToBitmap(image)
                                bitmap = rotateBitmapIfNeeded(bitmap, image)
                                uploadFrame(bitmap)
                                image.close()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e(
                                    "MainActivity",
                                    "Frame capture failed: ${exception.message}",
                                    exception
                                )
                            }
                        })
                    handler.postDelayed(this, 200) // 100ms 간격으로 프레임 캡처 (프레임 레이트 조정)
                }
            }
        })
    }

    private fun uploadFrame(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream) // 30% 압축률 (JPEG 압축률 조정)
        val imageBytes = outputStream.toByteArray()
        val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        val jsonRequest = JSONObject()
        jsonRequest.put("file", encodedImage)

        socket.emit("stream_frame", jsonRequest)
    }
}
