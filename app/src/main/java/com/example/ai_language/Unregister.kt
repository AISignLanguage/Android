package com.example.ai_language

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Unregister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unregister)

        val button = findViewById<Button>(R.id.unregisterButton)

        button.setOnClickListener{

        }
    }



}