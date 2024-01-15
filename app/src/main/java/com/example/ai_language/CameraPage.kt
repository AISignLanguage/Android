package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class CameraPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_page)

        val homeBtn = findViewById<ImageButton>(R.id.homeButton)
        homeBtn.setOnClickListener {
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}