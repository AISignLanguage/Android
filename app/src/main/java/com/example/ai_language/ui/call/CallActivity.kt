package com.example.ai_language.ui.call

import android.content.pm.PackageManager
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityCallBinding
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class CallActivity : BaseActivity<ActivityCallBinding>(R.layout.activity_call) {

    private val appId = "353bae93c92b4275bf34d1301ea06e42"
    private val channelName = "ta"
    private val token =
        "007eJxTYIhgy7vTzVkkLHig+NUCj/yvqdoh78yq3O98md8ucmNlsa4Cg7GpcVJiqqVxsqVRkomRuWlSmrFJiqGxgWFqooFZqonRgoV7UhsCGRlYtiYzMEIhiM/EUJLIwAAA06Ed7Q=="

    private val uid = 0
    private var isJoined = false

    private var agoraEngine: RtcEngine? = null
    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null

    companion object {
        protected const val PERMISSION_REQ_ID = 22
        protected val REQUESTED_PERMISSIONS = arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )
    }

    private fun checkSelfPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSIONS[0]
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    REQUESTED_PERMISSIONS[1]
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun showMessage(message: String) {
        this.runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpVideoSdkEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = this.baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine!!.enableVideo()
        } catch (e: Exception) {
            showMessage(e.message.toString() + "setUpVideoSdkEngine 에러")
        }
    }

    override fun setLayout() {
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //통화하는 동안 화면 꺼지지 않게 유지

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }

//        val joinButton = this.findViewById<Button>(R.id.joinButton).setOnClickListener {
//            setUpVideoSdkEngine()
//            joinCall()
//        }
//        val leaveButton = this.findViewById<Button>(R.id.leaveButton).setOnClickListener {
//            leaveCall()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (agoraEngine != null) {
            agoraEngine!!.stopPreview()
            agoraEngine!!.leaveChannel()

            Thread {
                RtcEngine.destroy()
                agoraEngine = null
            }.start()
        }
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(this.baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        binding.remoteUser.addView(remoteSurfaceView)

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

        localSurfaceView = SurfaceView(this.baseContext)
        binding.localUser.addView((localSurfaceView))

        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
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

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Reomte User Joined $uid")
            runOnUiThread {
                setupRemoteVideo(uid)
                Log.d("로그", "ㅇㅇ")
            }
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

