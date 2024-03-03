package com.example.ai_language

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePw : AppCompatActivity() {

    private lateinit var password: EditText
    private lateinit var newpassword: EditText
    private lateinit var checkpassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pw)

        val checkButton = findViewById<AppCompatButton>(R.id.checkButton)

        password = findViewById(R.id.pw)
        newpassword = findViewById(R.id.newPw)
        checkpassword = findViewById(R.id.checkPw)

        checkButton.setOnClickListener {
            val inputPassword = password.text.toString()
            val inputNewPassword = newpassword.text.toString()
            val inputCheckPassword = checkpassword.text.toString()

            checkPassword(inputPassword, inputNewPassword, inputCheckPassword)
        }










        //성공시 다이얼로그
        /*checkButton.setOnClickListener {
            val dialog = ChangePwDialog(this)
            dialog.show()
        }*/


    }

    //현재 비밀 번호 확인
    private fun checkPassword(inputPw: String, inputNewPw: String, inputCheckPw: String) {
        val savedEmail = getSavedEmail()
        if (savedEmail.isNullOrEmpty()) {
            // SharedPreferences에서 이메일을 가져오지 못했을 때 처리
            Toast.makeText(this, "저장된 이메일이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getInstance()
        val service = RetrofitClient.getUserRetrofitInterface()

        val call = service.checkPassword(CheckPasswordRequestDTO(savedEmail))
        call.enqueue(object : Callback<CheckPasswordResponseDTO> {
            override fun onResponse(call: Call<CheckPasswordResponseDTO>,response: Response<CheckPasswordResponseDTO>) {
                val passwordFromServer = response.body()?.password
                if (passwordFromServer != null) {
                    if (passwordFromServer == inputPw){
                        Log.d("로그", "현재 비밀번호 일치")

                    }
                    else{
                        Log.d("로그", "현재 비밀번호 불일치")
                    }
                }
            }

            override fun onFailure(call: Call<CheckPasswordResponseDTO>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getSavedEmail(): String? {
        val sharedPreferencesManager = EncryptedSharedPreferencesManager(this)
        val loginInfo = sharedPreferencesManager.getLoginInfo()
        return loginInfo["email"]
    }



}