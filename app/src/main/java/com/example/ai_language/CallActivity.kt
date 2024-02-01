package com.example.ai_language

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.view.SurfaceView;
import android.view.View.GONE
import android.view.View.VISIBLE
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
    private var localSurfaceView : SurfaceView? = null
    private var remoteSurfaceView : SurfaceView? = null


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
        if (checkSelfPerrmission()) {
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            localSurfaceView!!.visibility = VISIBLE
            aqoraEngine!!.startPreview()
            aqoraEngine!!.joinChannel(token, channelName, uid, option)
        } else {
            Toast.makeText(this, "permission not grant", Toast.LENGTH_SHORT).show()
        }
    }

    private fun leaveCall() {

        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            aqoraEngine!!.leaveChannel()
            showMessage("you left in channel")
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = GONE
            if (localSurfaceView != null) localSurfaceView!!.visibility = GONE
        }
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

                runOnUiThread {
                    remoteSurfaceView!!.visibility = GONE
                }
            }
        }

    private fun setupRemoteVideo(uid: Int) {
        val remoteUser = findViewById<FrameLayout>(R.id.remote_user)
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        remoteUser.addView(remoteSurfaceView)

        aqoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
    }

    private fun setupLocalVideo() {
        val localUser = findViewById<FrameLayout>(R.id.local_user)
        localSurfaceView = SurfaceView(baseContext)
        localUser.addView((localSurfaceView))


        aqoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                0
            )
        )
    }
}

