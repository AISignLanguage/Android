package com.example.ai_language.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("/create_room")
    fun createRoom(): Call<RoomResponse>
}

data class RoomResponse(val room_id: Int)

class StreamingActivity : BaseActivity<ActivityStreamingBinding>(R.layout.activity_streaming) {

    private lateinit var previewView: PreviewView
    private var imageCapture: ImageCapture? = null
    private lateinit var socket: Socket
    private var roomId: Int = 0
    private var isStreaming = false

    override fun setLayout() {
        previewView = findViewById(R.id.previewView)
        requestPermissions()
        connectToServer()
        createRoom()
        startCamera()

        binding.startStreamingButton.setOnClickListener {
            isStreaming = true
            startStreaming()
        }

        binding.stopStreamingButton.setOnClickListener {
            isStreaming = false
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            // 권한 거부됨
        }
    }

    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder().build()

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (e: ExecutionException) {
                // 에러 처리
            } catch (e: InterruptedException) {
                // 에러 처리
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureAndUploadFrame() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = imageProxyToBitmap(image)
                val rotatedBitmap = rotateBitmap(bitmap, image.imageInfo.rotationDegrees.toFloat())
                image.close()
                // 비동기 처리
                AsyncTask.execute {
                    uploadFrame(rotatedBitmap)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                // 에러 처리
            }
        })
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(rotationDegrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun uploadFrame(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // 압축 품질을 50으로 변경
        val imageBytes = outputStream.toByteArray()
        val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        val jsonRequest = JSONObject().apply {
            put("file", encodedImage)
            put("room_id", roomId) // Use dynamic room ID

        }

        socket.emit("stream_frame", jsonRequest)
    }

    private fun connectToServer() {
        try {
            val opts = IO.Options().apply {
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 2000 // 2초
            }
            socket = IO.socket("http://34.64.202.194:5000", opts)
            socket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun createRoom() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.64.202.194:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.createRoom().enqueue(object : Callback<RoomResponse> {
            override fun onResponse(call: Call<RoomResponse>, response: Response<RoomResponse>) {
                if (response.isSuccessful) {
                    roomId = response.body()?.room_id ?: 0
                    binding.tvRoomId.text = "Room ID: $roomId"
                }
            }

            override fun onFailure(call: Call<RoomResponse>, t: Throwable) {
                // 에러 처리
            }
        })
    }

    private fun startStreaming() {
        val handler = Handler(Looper.getMainLooper())
        val delay = 500L // 500 밀리초로 변경 (0.5초)

        handler.post(object : Runnable {
            override fun run() {
                if (isStreaming) {
                    captureAndUploadFrame()
                    handler.postDelayed(this, delay)
                }
            }
        })
    }
}
