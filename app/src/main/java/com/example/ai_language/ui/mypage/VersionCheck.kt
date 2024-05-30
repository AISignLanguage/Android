package com.example.ai_language.ui.mypage

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.example.ai_language.R
import com.example.ai_language.ui.home.Home

class VersionCheck : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version_check)

        val update = findViewById<TextView>(R.id.updateText) //어플리케이션 업데이트
        update.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        //update.setOnClickListener {  } //이후 추가

        val homeButton = findViewById<ImageButton>(R.id.homeButton4)
        homeButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}