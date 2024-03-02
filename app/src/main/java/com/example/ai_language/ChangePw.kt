package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton

class ChangePw : AppCompatActivity() {

    private lateinit var password: EditText
    private lateinit var newpasword: EditText
    private lateinit var checkpassword: EditText

    //현재 비밀 번호 확인
    private fun loginUser(inputPw: String) {
        RetrofitClient.getInstance()
        val service = RetrofitClient.getUserRetrofitInterface()


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pw)

        val checkButton = findViewById<AppCompatButton>(R.id.checkButton)

        password = findViewById(R.id.pw)
        newpasword = findViewById(R.id.newPw)
        checkpassword = findViewById(R.id.checkPw)

        checkButton.setOnClickListener {
            val inputPassword = password.text.toString()
            val inputNewPassword = newpasword.text.toString()
            val inputCheckPassword = checkpassword.text.toString()


        }










        //성공시 다이얼로그
        /*checkButton.setOnClickListener {
            val dialog = ChangePwDialog(this)
            dialog.show()
        }*/


    }
}