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
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityChangePwBinding
import com.example.ai_language.databinding.ActivityMainLoginBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePw : BaseActivity<ActivityChangePwBinding>(R.layout.activity_change_pw) {

    private lateinit var password: EditText
    private lateinit var newPassword: EditText
    private lateinit var checkPassword: EditText

    override fun setLayout() {
        changePassword()
    }

    //비밀번호 변경
    private fun changePassword() {

        RetrofitClient.getInstance()
        val service = RetrofitClient.getUserRetrofitInterface()

        val checkButton = binding.checkButton
        checkButton.setOnClickListener {

            val curPwd = binding.pw.text.toString()
            val newPwd = binding.newPw.text.toString()
            val newPwdCheck = binding.checkPw.text.toString()

            if (newPwd != newPwdCheck) {
                Toast.makeText(
                    applicationContext,
                    "비밀번호가 일치하지 않습니다.",
                    Toast.LENGTH_SHORT
                ).show()

                binding.newPw.setText("")
                binding.checkPw.setText("")
            }

            else {
                val changePasswordRequestDTO = ChangePasswordRequestDTO(
                    binding.pw.text.toString(),
                    binding.checkPw.text.toString()
                )
                val call = service.changePassword(changePasswordRequestDTO)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val resBody = response.body()?.string()

                            if (resBody != null) {
                                if (resBody.equals("Password changed successfully.")) {
                                    Log.d("로그", "비밀번호가 성공적으로 변경되었습니다.")
                                    Toast.makeText(
                                        applicationContext,
                                        "비밀번호가 성공적으로 변경되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showChangeDialog()
                                } else {
                                    Log.d("로그", "현재 비밀번호가 일치하지 않습니다.")

                                }
                            }

                        } else {
                            Log.d("로그", "비밀번호 변경 응답에 실패했습니다.")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("로그", "비밀번호 변경 요청에 실패했습니다.")
                    }
                })
            }
        }
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