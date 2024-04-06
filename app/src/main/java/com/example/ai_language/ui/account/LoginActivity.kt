package com.example.ai_language.ui.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.ai_language.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, KaKaoLoginActivity::class.java)
            startActivity(intent)
        }*/

        Handler().postDelayed({
            val intent = Intent(this, MainLoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)


    }
}