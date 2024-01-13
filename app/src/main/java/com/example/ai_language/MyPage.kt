package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MyPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)


        val my_inform_edit = findViewById<TextView>(R.id.my_inform_edit)
        my_inform_edit.setOnClickListener {
            val intent = Intent(this,PersonalInfo::class.java)
            startActivity(intent)
        }


        val withDrawer = findViewById<TextView>(R.id.withdrawal)
        withDrawer.setOnClickListener {
            val intent = Intent(this,Unregister::class.java)
            startActivity(intent)
        }

        val sign_out = findViewById<TextView>(R.id.sign_out)
        sign_out.setOnClickListener {

        }

    }
}