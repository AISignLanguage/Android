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

class KaKaoLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ka_kao_login)





        val signInBtn = findViewById<TextView>(R.id.sign_in_button)
        signInBtn.setOnClickListener {
            val intent = Intent(this, permissionActivity::class.java)
            startActivity(intent)
            finish()
        }


        val kakaoBtn = findViewById<ImageView>(R.id.kko_login_btn)
        kakaoBtn.setOnClickListener {
            kakaoLogin(this)
        }
    }




    private fun kakaoLogin(ctxt : Context) {
        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("결과", "카카오계정으로 로그인 실패 : ${error}")
            } else if (token != null) {
                //TODO: 최종적으로 카카오로그인 및 유저정보 가져온 결과
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("결과", "사용자 정보 요청 실패: ${error}")
                    } else if (user != null) {
                        val nickname = user.kakaoAccount?.profile?.nickname
                        val profileImageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl
                        Log.d("결과", "닉네임: $nickname")
                        Log.d("결과", "프로필 사진 URL: $profileImageUrl")

                    }
                }

            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token
                                                              , error ->
                if (error != null) {
                    Log.d("결과", "로그인 실패", error)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
// 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.e("결과", "로그인 성공")
                    // 사용자 정보 요청
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e("결과", "사용자 정보 요청 실패: ${error}")
                        } else if (user != null) {
                            val nickname = user.kakaoAccount?.profile?.nickname
                            val profileImageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl
                            Log.d("결과", "닉네임: $nickname")
                            Log.d("결과", "프로필 사진 URL: $profileImageUrl")
                        }
                    }

                    val kakaoBtn = findViewById<ImageView>(R.id.kko_login_btn)
                    kakaoBtn.setBackgroundResource(R.drawable.logoutkakao)
                    val intent = Intent(ctxt,TermsActivity::class.java)
                    startActivity(intent)
                }
            }

        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun kakaoLogout(){
        // 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("결과", "로그아웃 실패. SDK에서 토큰 삭제됨: ${error}")
            }
            else {
                Log.e("결과", "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }

    private fun kakaoUnlink(){
        // 연결 끊기
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e("결과", "연결 끊기 실패: ${error}")
            }
            else {
                Log.e("결과", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
        }
    }

}
