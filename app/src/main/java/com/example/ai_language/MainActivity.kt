package com.example.ai_language

import com.example.ai_language.DictionaryPage
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bt = findViewById<Button>(R.id.button)
        bt.setOnClickListener{
            val intent = Intent(this, DictionaryPage::class.java)
            startActivity(intent)
        }
    }
}