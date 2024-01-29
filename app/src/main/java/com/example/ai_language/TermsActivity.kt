package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class TermsActivity : AppCompatActivity() {

    private lateinit var termsAllButton: CheckBox
    private lateinit var termsButton1: CheckBox
    private lateinit var termsButton2: CheckBox
    private lateinit var termsButton3: CheckBox
    private lateinit var termsButton4: CheckBox
    private lateinit var termsButton5: CheckBox
    private lateinit var termsButton6: CheckBox
    private lateinit var termsButton7: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val tvTermsTitle: TextView = findViewById(R.id.termsButton)
        val layoutTermsContent: LinearLayout = findViewById(R.id.layoutTermsContent)
        tvTermsTitle.setOnClickListener {
            if (layoutTermsContent.visibility == View.VISIBLE) {
                layoutTermsContent.visibility = View.GONE
            } else {
                layoutTermsContent.visibility = View.VISIBLE
            }
        }

        val tvTermsTitle1: TextView = findViewById(R.id.termsButton1)
        val layoutTermsContent1: LinearLayout = findViewById(R.id.layoutTermsContent1)
        tvTermsTitle1.setOnClickListener {
            if (layoutTermsContent1.visibility == View.VISIBLE) {
                layoutTermsContent1.visibility = View.GONE
            } else {
                layoutTermsContent1.visibility = View.VISIBLE
            }
        }

        val tvTermsTitle2: TextView = findViewById(R.id.termsButton2)
        val layoutTermsContent2: LinearLayout = findViewById(R.id.layoutTermsContent2)
        tvTermsTitle2.setOnClickListener {
            if (layoutTermsContent2.visibility == View.VISIBLE) {
                layoutTermsContent2.visibility = View.GONE
            } else {
                layoutTermsContent2.visibility = View.VISIBLE
            }
        }

        val tvTermsTitle3: TextView = findViewById(R.id.termsButton3)
        val layoutTermsContent3: LinearLayout = findViewById(R.id.layoutTermsContent3)
        tvTermsTitle3.setOnClickListener {
            if (layoutTermsContent3.visibility == View.VISIBLE) {
                layoutTermsContent3.visibility = View.GONE
            } else {
                layoutTermsContent3.visibility = View.VISIBLE
            }
        }

        val tvTermsTitle4: TextView = findViewById(R.id.termsButton4)
        val layoutTermsContent4: LinearLayout = findViewById(R.id.layoutTermsContent4)
        tvTermsTitle4.setOnClickListener {
            if (layoutTermsContent4.visibility == View.VISIBLE) {
                layoutTermsContent4.visibility = View.GONE
            } else {
                layoutTermsContent4.visibility = View.VISIBLE
            }
        }

        val tvTermsTitle5: TextView = findViewById(R.id.termsButton5)
        val layoutTermsContent5: LinearLayout = findViewById(R.id.layoutTermsContent5)
        tvTermsTitle5.setOnClickListener {
            if (layoutTermsContent5.visibility == View.VISIBLE) {
                layoutTermsContent5.visibility = View.GONE
            } else {
                layoutTermsContent5.visibility = View.VISIBLE
            }
        }

        val tvTermsTitle6: TextView = findViewById(R.id.termsButton6)
        val layoutTermsContent6: LinearLayout = findViewById(R.id.layoutTermsContent6)
        tvTermsTitle6.setOnClickListener {
            if (layoutTermsContent6.visibility == View.VISIBLE) {
                layoutTermsContent6.visibility = View.GONE
            } else {
                layoutTermsContent6.visibility = View.VISIBLE
            }
        }

        val tvTermsTitle7: TextView = findViewById(R.id.termsButton7)
        val layoutTermsContent7: LinearLayout = findViewById(R.id.layoutTermsContent7)
        tvTermsTitle7.setOnClickListener {
            if (layoutTermsContent7.visibility == View.VISIBLE) {
                layoutTermsContent7.visibility = View.GONE
            } else {
                layoutTermsContent7.visibility = View.VISIBLE
            }
        }
    }

}