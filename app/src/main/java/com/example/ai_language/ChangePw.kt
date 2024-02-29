package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.AppCompatButton

class ChangePw : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pw)

        val checkButton = findViewById<AppCompatButton>(R.id.checkButton)

        checkButton.setOnClickListener {
            val dialog = ChangePwDialog(this)
            dialog.show()
        }


    }
}