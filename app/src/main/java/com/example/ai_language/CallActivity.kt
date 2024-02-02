package com.example.ai_language

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView
import java.util.Collections

import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class CallActivity : AppCompatActivity() {

    private lateinit var textureView: TextureView      // 카메라 미리보기를 위한 TextureView
    private lateinit var cameraDevice : CameraDevice    // 카메라 디바이스를 참조하기 위한 객체
    private lateinit var captureRequestBuilder: CaptureRequest.Builder  // 캡처 요청을 만들기 위한 Builder
    private lateinit var cameraCaptureSession: CameraCaptureSession      // 카메라 캡처 세션

    private val appId = "353bae93c92b4275bf34d1301ea06e42"
    private val channelName = "test"
    private val token = "007eJxTYJiZdkc6MvxkdZE9q2zCM4mFF3Y84Y0Til32sS0svG7tuxcKDMamxkmJqZbGyZZGSSZG5qZJacYmKYbGBoapiQZmqSZG8zx3pzYEMjJ4XBBgZmSAQBCfhaEktbiEgQEAOLwfLA=="

    private val uid = 0
    private var isJoined = false

    private var agoraEngine : RtcEngine? = null
    private var localSurfaceView : SurfaceView? = null
    private var remoteSurfaceView : SurfaceView? = null


    private val PERMISSION_ID = 12
    private val REQUESTED_PERMISSIONS = arrayOf<String>(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )

    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSIONS[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    REQUESTED_PERMISSIONS[1]
                ) != PackageManager.PERMISSION_GRANTED)
    }


    private fun showMessage(message : String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpVideoSdkEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine!!.enableVideo()
        } catch (e: Exception) {
            showMessage(e.message.toString() + "setUpVideoSdkEngine 에러")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //통화하는 동안 화면 꺼지지 않게 유지

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_ID);
        }
        //setUpVideoSdkEngine()

        val joinButton = findViewById<Button>(R.id.joinButton).setOnClickListener{
            setUpVideoSdkEngine()
            joinCall()
        }
        val leaveButton = findViewById<Button>(R.id.leaveButton).setOnClickListener{
            leaveCall()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun setupRemoteVideo(uid: Int) {
        val remoteUser = findViewById<FrameLayout>(R.id.remote_user)
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        remoteUser.addView(remoteSurfaceView)

        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        remoteSurfaceView!!.visibility = View.VISIBLE
    }

    private fun setupLocalVideo() {
        val localUser = findViewById<FrameLayout>(R.id.local_user)
        localSurfaceView = SurfaceView(baseContext)
        localUser.addView((localSurfaceView))

        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

    private fun joinCall() {
        if (checkSelfPermission()) {
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            localSurfaceView!!.visibility = VISIBLE
            agoraEngine!!.startPreview()
            agoraEngine!!.joinChannel(token, channelName, uid, option)
        } else {
            Toast.makeText(this, "permission not grant", Toast.LENGTH_SHORT).show()
        }
    }

    private fun leaveCall() {

        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            agoraEngine!!.leaveChannel()
            showMessage("you left in channel")
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
        }
    }

    private val mRtcEventHandler : IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
            override fun onUserJoined(uid: Int, elapsed: Int) {
                showMessage("Reomte User Joined $uid")
                runOnUiThread { setupRemoteVideo(uid) }
            }

            override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                isJoined = true
                showMessage("Joined Channel $channel")
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                showMessage("user offline")
                runOnUiThread { remoteSurfaceView!!.visibility = View.GONE }
            }
        }
}

