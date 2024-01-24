package com.example.ai_language

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlin.random.Random

class KaKaoLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ka_kao_login)


        //로그인 버튼 -> 아이디 비번 확인만 없으면 없다고 메세지 (DB확인)
        //카카오 버튼, 회원가입 버튼 -> 회원가입 버튼은 바로, 카카오 버튼은 DB확인 후 사용자가 처음접속이면 회원가입으로, 아니면 바로 HOME

        val signInBtn = findViewById<TextView>(R.id.sign_in_button)
        signInBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("nick", "사용자${ Random.nextInt(10000)}")
            intent.putExtra("profile", "https://cdn-icons-png.flaticon.com/128/149/149071.png")
            startActivity(intent)
        }

        val sinUpBtn = findViewById<TextView>(R.id.sign_up_button)
        sinUpBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("nick", "사용자${ Random.nextInt(10000)}")
            intent.putExtra("profile", "https://cdn-icons-png.flaticon.com/128/149/149071.png")
            startActivity(intent)
            finish()
        }


        val kakaoBtn = findViewById<ImageView>(R.id.kko_login_btn)
        kakaoBtn.setOnClickListener {
            kakaoLogin(this)
        }
    }
    private var isLoggingIn = false

    private fun kakaoLogin(ctxt: Context) {

        if (isLoggingIn) return // 중복 로그인 방지

        isLoggingIn = true
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            isLoggingIn = false
            if (error != null) {
                Log.e("결과", "카카오계정으로 로그인 실패 : ${error}")
            } else if (token != null) {

                requestUserInfoAndStartRegisterActivity(ctxt)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.d("결과", "카카오톡 로그인 실패", error)
                    // 사용자에게 카카오 계정 로그인 옵션 제공
                } else if (token != null) {

                    requestUserInfoAndStartRegisterActivity(ctxt)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }


    private fun kakaoLogout() {
        // 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("결과", "로그아웃 실패. SDK에서 토큰 삭제됨: ${error}")
            } else {
                Log.e("결과", "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }

    private fun kakaoUnlink() {
        // 연결 끊기
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e("결과", "연결 끊기 실패: ${error}")
            } else {
                Log.e("결과", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
        }
    }

    private fun requestUserInfoAndStartRegisterActivity(ctxt: Context) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("결과", "사용자 정보 요청 실패: ${error}")
            } else if (user != null) {
                val nickname = user.kakaoAccount?.profile?.nickname
                val profileImageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl
                Log.d("결과", "닉네임: $nickname, 프로필 사진 URL: $profileImageUrl")

                val intent = Intent(ctxt, RegisterActivityApp::class.java).apply {
                    putExtra("nick", nickname)
                    putExtra("profile", profileImageUrl)
                }
                ctxt.startActivity(intent)
            }
        }
    }
}
