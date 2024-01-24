package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class TermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val tvTermsTitle: TextView = findViewById(R.id.termsButton)
        val layoutTermsContent: LinearLayout = findViewById(R.id.layoutTermsContent)
        val imageView = findViewById<ImageView>(R.id.imageView)
        tvTermsTitle.setOnClickListener {
            if (layoutTermsContent.visibility == View.VISIBLE) {
                layoutTermsContent.visibility = View.GONE
                imageView.setImageResource(R.drawable.toggle)
            } else {
                layoutTermsContent.visibility = View.VISIBLE
                imageView.setImageResource(R.drawable.toggle2)
            }
        }

        val tvTermsTitle1: TextView = findViewById(R.id.termsButton1)
        val layoutTermsContent1: LinearLayout = findViewById(R.id.layoutTermsContent1)
        val imageView1 = findViewById<ImageView>(R.id.imageView1)
        tvTermsTitle1.setOnClickListener {
            if (layoutTermsContent1.visibility == View.VISIBLE) {
                layoutTermsContent1.visibility = View.GONE
                imageView1.setImageResource(R.drawable.toggle)
            } else {
                layoutTermsContent1.visibility = View.VISIBLE
                imageView1.setImageResource(R.drawable.toggle2)
            }
        }

        val tvTermsTitle2: TextView = findViewById(R.id.termsButton2)
        val layoutTermsContent2: LinearLayout = findViewById(R.id.layoutTermsContent2)
        val imageView2 = findViewById<ImageView>(R.id.imageView2)
        tvTermsTitle2.setOnClickListener {
            if (layoutTermsContent2.visibility == View.VISIBLE) {
                layoutTermsContent2.visibility = View.GONE
                imageView2.setImageResource(R.drawable.toggle)
            } else {
                layoutTermsContent2.visibility = View.VISIBLE
                imageView2.setImageResource(R.drawable.toggle2)
            }
        }

        val tvTermsTitle3: TextView = findViewById(R.id.termsButton3)
        val layoutTermsContent3: LinearLayout = findViewById(R.id.layoutTermsContent3)
        val imageView3 = findViewById<ImageView>(R.id.imageView3)
        tvTermsTitle3.setOnClickListener {
            if (layoutTermsContent3.visibility == View.VISIBLE) {
                layoutTermsContent3.visibility = View.GONE
                imageView3.setImageResource(R.drawable.toggle)
            } else {
                layoutTermsContent3.visibility = View.VISIBLE
                imageView3.setImageResource(R.drawable.toggle2)
            }
        }

        val tvTermsTitle4: TextView = findViewById(R.id.termsButton4)
        val layoutTermsContent4: LinearLayout = findViewById(R.id.layoutTermsContent4)
        val imageView4 = findViewById<ImageView>(R.id.imageView4)
        tvTermsTitle4.setOnClickListener {
            if (layoutTermsContent4.visibility == View.VISIBLE) {
                layoutTermsContent4.visibility = View.GONE
                imageView4.setImageResource(R.drawable.toggle)
            } else {
                layoutTermsContent4.visibility = View.VISIBLE
                imageView4.setImageResource(R.drawable.toggle2)
            }
        }

        val tvTermsTitle5: TextView = findViewById(R.id.termsButton5)
        val layoutTermsContent5: LinearLayout = findViewById(R.id.layoutTermsContent5)
        val imageView5 = findViewById<ImageView>(R.id.imageView5)
        tvTermsTitle5.setOnClickListener {
            if (layoutTermsContent5.visibility == View.VISIBLE) {
                layoutTermsContent5.visibility = View.GONE
                imageView5.setImageResource(R.drawable.toggle)
            } else {
                layoutTermsContent5.visibility = View.VISIBLE
                imageView5.setImageResource(R.drawable.toggle2)
            }
        }

        val tvTermsTitle6: TextView = findViewById(R.id.termsButton6)
        val layoutTermsContent6: LinearLayout = findViewById(R.id.layoutTermsContent6)
        val imageView6 = findViewById<ImageView>(R.id.imageView6)
        tvTermsTitle6.setOnClickListener {
            if (layoutTermsContent6.visibility == View.VISIBLE) {
                layoutTermsContent6.visibility = View.GONE
                imageView6.setImageResource(R.drawable.toggle)
            } else {
                layoutTermsContent6.visibility = View.VISIBLE
                imageView6.setImageResource(R.drawable.toggle2)
            }
        }

        val tvTermsTitle7: TextView = findViewById(R.id.termsButton7)
        val layoutTermsContent7: LinearLayout = findViewById(R.id.layoutTermsContent7)
        val imageView7 = findViewById<ImageView>(R.id.imageView7)
        tvTermsTitle7.setOnClickListener {
            if (layoutTermsContent7.visibility == View.VISIBLE) {
                layoutTermsContent7.visibility = View.GONE
                imageView7.setImageResource(R.drawable.toggle)
            } else {
                layoutTermsContent7.visibility = View.VISIBLE
                imageView7.setImageResource(R.drawable.toggle2)
            }
        }
    }

}