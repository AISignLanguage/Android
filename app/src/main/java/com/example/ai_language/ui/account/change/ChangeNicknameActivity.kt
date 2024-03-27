package com.example.ai_language.ui.account.change

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_language.domain.model.request.ChangeNickNameDTO
import com.example.ai_language.domain.model.request.ChangeNickNameResultDTO
import com.example.ai_language.R
import com.example.ai_language.Util.RetrofitClient
import com.example.ai_language.data.remote.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeNicknameActivity : AppCompatActivity() {

    private lateinit var call: Call<ChangeNickNameResultDTO>
    private lateinit var service: Service
    private lateinit var changeNickNameResultDTO: ChangeNickNameResultDTO

    private lateinit var originalNickname: String
    private lateinit var changeNicknameBtn: Button
    private lateinit var editNickname: EditText
    private lateinit var homeBtn: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_nickname)

        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()

        homeBtn = findViewById(R.id.homeButton)
        homeBtn.setOnClickListener {
            val intent = Intent(this, PersonalInfo::class.java)
            startActivity(intent)
        }

        changeNicknameBtn = findViewById(R.id.change_nickname_btn)
        changeNicknameBtn.setOnClickListener {
            editNickname = findViewById(R.id.edit_user_email)
            if (editNickname.text.length in 2..6) {
                changeNickName(editNickname.text.toString()) // 닉네임 변경
            } else {
                makeToastMsg("올바르지 않은 닉네임 형식입니다.")
                editNickname.text = null
            }
        }

    }

    private fun makeToastMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    // 서버 DB에서 닉네임 변경
    private fun changeNickName(changeNickname: String) {

        editNickname = findViewById<EditText>(R.id.edit_user_email)
        originalNickname = intent.getStringExtra("originalNickname")!!

        val changeNickNameDTO = ChangeNickNameDTO(originalNickname, changeNickname)
        call = service.changeNickName(changeNickNameDTO)

        call.enqueue(object : Callback<ChangeNickNameResultDTO> {

            override fun onResponse(
                call: Call<ChangeNickNameResultDTO>,
                response: Response<ChangeNickNameResultDTO>
            ) {
                if (response.isSuccessful) {
                    Log.d("로그", "isSuccessful")
                    changeNickNameResultDTO = response.body()!!
                    val result = changeNickNameResultDTO.result

                    when (result) {
                        1 -> {
                            editNickname.text = null
                            makeToastMsg("변경할 닉네임이 존재하지 않습니다!")
                        }

                        2 -> {
                            editNickname.text = null
                            makeToastMsg("중복된 닉네임이 존재합니다!")
                        }

                        3 -> {
                            makeToastMsg("닉네임이 변경되었습니다.")
                            val intent = Intent(applicationContext, PersonalInfo::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ChangeNickNameResultDTO>, t: Throwable) {
                Log.d("로그", "changeNickName 서버 연결 실패")
            }


        })
    }
}