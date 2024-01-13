package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val dic_btn = findViewById<ImageButton>(R.id.dic_btn)
        dic_btn.setOnClickListener {
            val intent = Intent(this,DictionaryPage::class.java)
            startActivity(intent)
        }
    }
}