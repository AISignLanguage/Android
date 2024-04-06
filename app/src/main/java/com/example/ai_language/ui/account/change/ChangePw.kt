package com.example.ai_language.ui.account.change

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.ai_language.domain.model.request.ChangePasswordRequestDTO
import com.example.ai_language.domain.model.request.ChangePasswordResponseDTO
import com.example.ai_language.domain.model.request.CheckPasswordRequestDTO
import com.example.ai_language.domain.model.request.CheckPasswordResponseDTO
import com.example.ai_language.Util.EncryptedSharedPreferencesManager
import com.example.ai_language.ui.account.MainLoginActivity
import com.example.ai_language.R
import com.example.ai_language.Util.RetrofitClient
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
    }

    //로그인 이메일 불러오기
    private fun getSavedEmail(): String? {
        val sharedPreferencesManager = EncryptedSharedPreferencesManager(this)
        return sharedPreferencesManager.getUserEmail()
    }


    //현재 비밀 번호 확인
    private fun checkPassword(inputPw: String, inputNewPw: String, inputCheckPw: String) {
        val savedEmail = getSavedEmail()
        if (savedEmail.isNullOrEmpty()) {
            Log.d("로그", "저장된 이메일이 없습니다")
            return
        }

        RetrofitClient.getInstance()
        val service = RetrofitClient.getUserRetrofitInterface()

        val call = service.checkPassword(CheckPasswordRequestDTO(savedEmail))
        call.enqueue(object : Callback<CheckPasswordResponseDTO> {
            override fun onResponse(
                call: Call<CheckPasswordResponseDTO>,
                response: Response<CheckPasswordResponseDTO>
            ) {
                val passwordFromServer = response.body()?.password
                if (passwordFromServer != null) {
                    if (passwordFromServer == inputPw) {
                        Log.d("로그", "현재 비밀번호 일치")

                        //비밀번호 변경 함수 호출
                        changePassword(inputNewPw, inputCheckPw)

                    } else {
                        Log.d("로그", "현재 비밀번호 불일치")
                        Toast.makeText(applicationContext, "현재 비밀번호 불일치", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<CheckPasswordResponseDTO>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }


    //비밀번호 변경
    private fun changePassword(inputNewPw: String, inputCheckPw: String) {
        val newPassword = inputNewPw.trim()
        val confirmPassword = inputCheckPw.trim()

        if (newPassword != confirmPassword) {
            Log.d("로그", "새 비밀번호와 확인용 비밀번호가 일치하지 않습니다.")
            return
        }

        val savedEmail = getSavedEmail()
        if (savedEmail.isNullOrEmpty()) {
            // SharedPreferences에서 이메일을 가져오지 못했을 때 처리
            Toast.makeText(this, "저장된 이메일이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getInstance()
        val service = RetrofitClient.getUserRetrofitInterface()

        val call = service.changePassword(ChangePasswordRequestDTO(savedEmail, newPassword))
        call.enqueue(object : Callback<ChangePasswordResponseDTO> {
            override fun onResponse(
                call: Call<ChangePasswordResponseDTO>,
                response: Response<ChangePasswordResponseDTO>
            ) {
                Log.d("로그", "$savedEmail")
                if (response.isSuccessful) {
                    val changePasswordResponseDTO = response.body()
                    if (changePasswordResponseDTO != null && changePasswordResponseDTO.success) {
                        Log.d("로그", "비밀번호가 성공적으로 변경되었습니다.")
                        Toast.makeText(
                            applicationContext,
                            "비밀번호가 성공적으로 변경되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        showChangeDialog()
                    } else {
                        Log.d("로그", "비밀번호 변경에 실패했습니다.")

                    }

                } else {
                    Log.d("로그", "비밀번호 변경 응답에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponseDTO>, t: Throwable) {
                Log.d("로그", "비밀번호 변경 요청에 실패했습니다.")
            }
        })

    }

    private fun showChangeDialog() {
        val changePwDialog = ChangePwDialog(this)
        changePwDialog.setOnDismissListener(object : ChangePwDialog.DialogDismissListener {

            override fun onDialogDismiss() {
                val intent = Intent(this@ChangePw, MainLoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        changePwDialog.dialogShow()
    }
}