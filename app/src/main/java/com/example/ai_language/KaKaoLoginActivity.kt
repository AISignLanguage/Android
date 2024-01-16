package com.example.ai_language

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.user.UserApiClient

class KaKaoLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ka_kao_login)
        

        val signInBtn = findViewById<TextView>(R.id.sign_in_button)
        signInBtn.setOnClickListener {
            val intent = Intent(this,TermsActivity::class.java)
            startActivity(intent)
            finish()
        }


        val kakaoBtn = findViewById<ImageView>(R.id.kko_login_btn)
        kakaoBtn.setOnClickListener {
            // 카카오 로그인 요청
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e("KaKaoLoginActivity", "로그인 실패", error)
                } else if (token != null) {
                    Log.i("KaKaoLoginActivity", "로그인 성공 ${token.accessToken}")

                    // 로그인 성공 시 원하는 작업 수행
                }
            }
        }
    }
}
