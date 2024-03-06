package com.example.ai_language

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class TermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val term = findViewById<TextView>(R.id.term)
        val content = findViewById<LinearLayout>(R.id.content)
        val termLayout = findViewById<LinearLayout>(R.id.termLayout)
        term.setOnClickListener{
            if (content.visibility == View.VISIBLE) {
                content.visibility = View.GONE
                termLayout.setBackgroundResource(R.drawable.term);
            } else {
                content.visibility = View.VISIBLE
                termLayout.setBackgroundResource(R.drawable.term1);
            }
        }

        val term1 = findViewById<TextView>(R.id.term1)
        val content1 = findViewById<LinearLayout>(R.id.content1)
        val termLayout1 = findViewById<LinearLayout>(R.id.termLayout1)
        term1.setOnClickListener{
            if (content1.visibility == View.VISIBLE) {
                content1.visibility = View.GONE
                termLayout1.setBackgroundResource(R.drawable.term);
            } else {
                content1.visibility = View.VISIBLE
                termLayout1.setBackgroundResource(R.drawable.term1);
            }
        }

        val term2 = findViewById<TextView>(R.id.term2)
        val content2 = findViewById<LinearLayout>(R.id.content2)
        val termLayout2 = findViewById<LinearLayout>(R.id.termLayout2)
        term2.setOnClickListener{
            if (content2.visibility == View.VISIBLE) {
                content2.visibility = View.GONE
                termLayout2.setBackgroundResource(R.drawable.term);
            } else {
                content2.visibility = View.VISIBLE
                termLayout2.setBackgroundResource(R.drawable.term1);
            }
        }

        val term3 = findViewById<TextView>(R.id.term3)
        val content3 = findViewById<LinearLayout>(R.id.content3)
        val termLayout3 = findViewById<LinearLayout>(R.id.termLayout3)
        term3.setOnClickListener{
            if (content3.visibility == View.VISIBLE) {
                content3.visibility = View.GONE
                termLayout3.setBackgroundResource(R.drawable.term);
            } else {
                content3.visibility = View.VISIBLE
                termLayout3.setBackgroundResource(R.drawable.term1);
            }
        }

        val term4 = findViewById<TextView>(R.id.term4)
        val content4 = findViewById<LinearLayout>(R.id.content4)
        val termLayout4 = findViewById<LinearLayout>(R.id.termLayout4)
        term4.setOnClickListener{
            if (content4.visibility == View.VISIBLE) {
                content4.visibility = View.GONE
                termLayout4.setBackgroundResource(R.drawable.term);
            } else {
                content4.visibility = View.VISIBLE
                termLayout4.setBackgroundResource(R.drawable.term1);
            }
        }

        val term5 = findViewById<TextView>(R.id.term5)
        val content5 = findViewById<LinearLayout>(R.id.content5)
        val termLayout5 = findViewById<LinearLayout>(R.id.termLayout5)
        term5.setOnClickListener{
            if (content5.visibility == View.VISIBLE) {
                content5.visibility = View.GONE
                termLayout5.setBackgroundResource(R.drawable.term);
            } else {
                content5.visibility = View.VISIBLE
                termLayout5.setBackgroundResource(R.drawable.term1);
            }
        }

        val term6 = findViewById<TextView>(R.id.term6)
        val content6 = findViewById<LinearLayout>(R.id.content6)
        val termLayout6 = findViewById<LinearLayout>(R.id.termLayout6)
        term6.setOnClickListener{
            if (content6.visibility == View.VISIBLE) {
                content6.visibility = View.GONE
                termLayout6.setBackgroundResource(R.drawable.term);
            } else {
                content6.visibility = View.VISIBLE
                termLayout6.setBackgroundResource(R.drawable.term1);
            }
        }

        val term7 = findViewById<TextView>(R.id.term7)
        val content7 = findViewById<LinearLayout>(R.id.content7)
        val termLayout7 = findViewById<LinearLayout>(R.id.termLayout7)
        term7.setOnClickListener{
            if (content7.visibility == View.VISIBLE) {
                content7.visibility = View.GONE
                termLayout7.setBackgroundResource(R.drawable.term);
            } else {
                content7.visibility = View.VISIBLE
                termLayout7.setBackgroundResource(R.drawable.term1);
            }
        }
    }

}