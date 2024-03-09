package com.example.ai_language

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeNicknameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_nickname)
    }

    private fun makeToastMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


//    private fun changeNickName() {
//
//        nickName = findViewById(R.id.user_nick_name)
//
//        val changeName = nickName.text
//        val changeNickNameDTO = ChangeNickNameDTO(changeName.toString())
//        Log.d("로그", "changeName: ${changeNickNameDTO.nickname}")
//        changeNickNameCall = service.changeNickName(changeNickNameDTO)
//
//        changeNickNameCall.enqueue(object : Callback<ChangeNickNameResultDTO> {
//            override fun onResponse(call: Call<ChangeNickNameResultDTO>, response: Response<ChangeNickNameResultDTO>) {
//
//                if (response.isSuccessful) {
//                    Log.d("로그", "isSuccessful")
//                    changeNickNameResultDTO = response.body()!!
//                    val result = changeNickNameResultDTO.result
//
//                    when (result) {
//                        1 -> {
//                            nickName.text = null
//                            makeToastMsg("빈 문자열입니다. 다시 입력하세요.")
//                        }
//                        2 -> {
//                            nickName.text = null
//                            makeToastMsg("이미 존재하는 닉네임입니다.")
//                        }
//                        3 -> {
//                            makeToastMsg("비밀번호가 변경되었습니다.")
//                        }
//                    }
//
//                }
//            }
//
//            override fun onFailure(call: Call<ChangeNickNameResultDTO>, t: Throwable) {
//                Log.d("로그", "onFailure")
//            }
//        })
//    }

}