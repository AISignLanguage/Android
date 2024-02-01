package com.example.ai_language

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.view.SurfaceView;

import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.*

class CallActivity : AppCompatActivity() {

    private val appId = ""
    private val channelName = ""
    private val token = ""
    private val uid = 0

    private var isJoined = false
    private var aqoraEngine : RtcEngine? = null
    private var localSurfaceView : SurfaceView? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //통화하는 동안 화면 꺼지지 않게 유지

        val joinButton = findViewById<Button>(R.id.joinButton).setOnClickListener{
            joinCall()
        }
        val leaveButton = findViewById<Button>(R.id.leaveButton).setOnClickListener{
            leaveCall()
        }
    }

    private fun joinCall() {

    }

    private fun leaveCall() {

    }
}

