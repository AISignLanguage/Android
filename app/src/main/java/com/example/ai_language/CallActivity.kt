package com.example.ai_language

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.view.SurfaceView;
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat

import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.*
import java.lang.Exception

class CallActivity : AppCompatActivity() {

    private val appId = "353bae93c92b4275bf34d1301ea06e42"
    private val channelName = "test"
    private val token = "007eJxTYJiZdkc6MvxkdZE9q2zCM4mFF3Y84Y0Til32sS0svG7tuxcKDMamxkmJqZbGyZZGSSZG5qZJacYmKYbGBoapiQZmqSZG8zx3pzYEMjJ4XBBgZmSAQBCfhaEktbiEgQEAOLwfLA=="
    private val uid = 0

    private var isJoined = false
    private var aqoraEngine : RtcEngine? = null
    private var localSurFaceView : SurfaceView? = null
    private var remoteSurFaceView : SurfaceView? = null


    private val PERISSION_ID = 12
    private val REQUESTED_PERMISSION =
        arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )

    private fun checkSelfPerrmission() : Boolean {
        return !(ContextCompat.checkSelfPermission(
            this, REQUESTED_PERMISSION[0]
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSION[1]
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
            aqoraEngine!!.enableVideo()
        } catch (e: Exception) {
            showMessage(e.message.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //통화하는 동안 화면 꺼지지 않게 유지

        if (!checkSelfPerrmission()) {
            ActivityCompat
                .requestPermissions(
                    this, REQUESTED_PERMISSION, PERISSION_ID
                )
        }
        setUpVideoSdkEngine()

        val joinButton = findViewById<Button>(R.id.joinButton).setOnClickListener{
            joinCall()
        }
        val leaveButton = findViewById<Button>(R.id.leaveButton).setOnClickListener{
            leaveCall()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        aqoraEngine!!.stopPreview()
        aqoraEngine!!.leaveChannel()

        Thread {
            RtcEngine.destroy()
            aqoraEngine = null
        }.start()
    }

    private fun joinCall() {

    }

    private fun leaveCall() {

    }

    private val mRtcEventHandler : IRtcEngineEventHandler =
        object : IRtcEngineEventHandler() {

            override fun onUserJoined(uid: Int, elapsed: Int) {
                showMessage("Reomte User Joined $uid")

                runOnUiThread { setupRemoteVideo(uid) }
            }

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                isJoined = true
                showMessage("Joined Channel $channel")
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                showMessage("user offline")
            }
        }

    private fun setupRemoteVideo(uid: Int) {
        val remoteUser = findViewById<FrameLayout>(R.id.remote_user)
        remoteSurFaceView = SurfaceView(baseContext)
        remoteSurFaceView!!.setZOrderMediaOverlay(true)
        remoteUser.addView(remoteSurFaceView)

        aqoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurFaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
    }

    private fun setupLocalVideo(uid: Int) {
        val remoteUser = findViewById<FrameLayout>(R.id.remote_user)
        localSurFaceView = SurfaceView(baseContext)

        aqoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurFaceView,
                VideoCanvas.RENDER_MODE_FIT,
                0
            )
        )
    }
}

