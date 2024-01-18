package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class permissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        val checkButton = findViewById<ImageButton>(R.id.checkButton)
        checkButton.setOnClickListener {
            val intent = Intent(this, TermsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}