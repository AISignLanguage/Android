package com.example.ai_language

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class Unregister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unregister)

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        homeButton.setOnClickListener{
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }

        val unregisterButton = findViewById<Button>(R.id.unregisterButton)
        unregisterButton.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        unregisterButton.setOnClickListener{
            val unregisterDialog = UnregisterDialog(this)
            unregisterDialog.show()

            Handler().postDelayed({
                unregisterDialog.dismiss()
                navigateToHomeActivity()
            }, 3000)
        }

    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}