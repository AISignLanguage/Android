package com.example.ai_language.ui.mypage

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_language.R

class FaqPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        //질문 1
        val question1 = findViewById<TextView>(R.id.question1)
        val answer1 = findViewById<LinearLayout>(R.id.answer1)
        val questionLayout1 = findViewById<LinearLayout>(R.id.questionLayout1)
        question1.setOnClickListener {
            if (answer1.visibility == View.VISIBLE) {
                answer1.visibility = View.GONE
                questionLayout1.setBackgroundResource(R.drawable.question);
            } else {
                answer1.visibility = View.VISIBLE
                questionLayout1.setBackgroundResource(R.drawable.question1);
            }
        }

        //질문 2
        val question2 = findViewById<TextView>(R.id.question2)
        val answer2 = findViewById<LinearLayout>(R.id.answer2)
        val questionLayout2 = findViewById<LinearLayout>(R.id.questionLayout2)
        question2.setOnClickListener {
            if (answer2.visibility == View.VISIBLE) {
                answer2.visibility = View.GONE
                questionLayout2.setBackgroundResource(R.drawable.question);
            } else {
                answer2.visibility = View.VISIBLE
                questionLayout2.setBackgroundResource(R.drawable.question1);
            }
        }

        //질문 3
        val question3 = findViewById<TextView>(R.id.question3)
        val answer3 = findViewById<LinearLayout>(R.id.answer3)
        val questionLayout3 = findViewById<LinearLayout>(R.id.questionLayout3)
        question3.setOnClickListener {
            if (answer3.visibility == View.VISIBLE) {
                answer3.visibility = View.GONE
                questionLayout3.setBackgroundResource(R.drawable.question);
            } else {
                answer3.visibility = View.VISIBLE
                questionLayout3.setBackgroundResource(R.drawable.question1);
            }
        }

        //질문 4
        val question4 = findViewById<TextView>(R.id.question4)
        val answer4 = findViewById<LinearLayout>(R.id.answer4)
        val questionLayout4 = findViewById<LinearLayout>(R.id.questionLayout4)
        question4.setOnClickListener {
            if (answer4.visibility == View.VISIBLE) {
                answer4.visibility = View.GONE
                questionLayout4.setBackgroundResource(R.drawable.question);
            } else {
                answer4.visibility = View.VISIBLE
                questionLayout4.setBackgroundResource(R.drawable.question1);
            }
        }

        //질문 5
        val question5 = findViewById<TextView>(R.id.question5)
        val answer5 = findViewById<LinearLayout>(R.id.answer5)
        val questionLayout5 = findViewById<LinearLayout>(R.id.questionLayout5)
        question5.setOnClickListener {
            if (answer5.visibility == View.VISIBLE) {
                answer5.visibility = View.GONE
                questionLayout5.setBackgroundResource(R.drawable.question);
            } else {
                answer5.visibility = View.VISIBLE
                questionLayout5.setBackgroundResource(R.drawable.question1);
            }
        }

        //질문 6
        val question6 = findViewById<TextView>(R.id.question6)
        val answer6 = findViewById<LinearLayout>(R.id.answer6)
        val questionLayout6 = findViewById<LinearLayout>(R.id.questionLayout6)
        question6.setOnClickListener {
            if (answer6.visibility == View.VISIBLE) {
                answer6.visibility = View.GONE
                questionLayout6.setBackgroundResource(R.drawable.question);
            } else {
                answer6.visibility = View.VISIBLE
                questionLayout6.setBackgroundResource(R.drawable.question1);
            }
        }

        //질문 7
        val question7 = findViewById<TextView>(R.id.question7)
        val answer7 = findViewById<LinearLayout>(R.id.answer7)
        val questionLayout7 = findViewById<LinearLayout>(R.id.questionLayout7)
        question7.setOnClickListener {
            if (answer7.visibility == View.VISIBLE) {
                answer7.visibility = View.GONE
                questionLayout7.setBackgroundResource(R.drawable.question);
            } else {
                answer7.visibility = View.VISIBLE
                questionLayout7.setBackgroundResource(R.drawable.question1);
            }
        }

    }
}