package com.example.ai_language

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class KaKaoLoginActivity : AppCompatActivity() {

    private lateinit var userEmail: EditText
    private lateinit var userPw: EditText
    private lateinit var progressBar: ProgressBar
    private val disposables = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ka_kao_login)

        //테스트 용 홈 화면 이동
        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        //로그인 비동기 처리 - retrofit
        userEmail = findViewById(R.id.userEmail)
        userPw = findViewById(R.id.userPw)
        progressBar = findViewById(R.id.progressBar)

        val signInBtn = findViewById<TextView>(R.id.sign_in_button)
        signInBtn.setOnClickListener {

            val inputUserEmail : String
            val inputUserPw : String

            val sharedPreferencesManager = EncryptedSharedPreferencesManager()
            val loginInfo = sharedPreferencesManager.getLoginInfo(applicationContext)
            if (loginInfo.isNotEmpty()) {
                inputUserEmail = loginInfo["email"].toString()
                inputUserPw = loginInfo["password"].toString()

                if (!inputUserEmail.isNullOrEmpty() && !inputUserPw.isNullOrEmpty()) {

                }

            } else {
                inputUserEmail = userEmail.text.toString()
                inputUserPw = userPw.text.toString()
            }

            val autoLoginCheckBtn = findViewById<RadioButton>(R.id.radioButton)
            if (autoLoginCheckBtn.isChecked) {
                // 이메일과 비밀번호를 SharedPreferences에 저장
                sharedPreferencesManager.setLoginInfo(applicationContext, inputUserEmail, inputUserPw)
                Log.d("로그", "setLoginInfo")
            }

            progressBar.visibility = View.VISIBLE

            RetrofitClient.getInstance()
            val service = RetrofitClient.getUserRetrofitInterface()

            val call = service.login(LoginRequestDTO(inputUserEmail, inputUserPw))
            val intent = Intent(this, Home::class.java)
            call.enqueue(object : Callback<LoginResponseDTO>{
                override fun onResponse(call: Call<LoginResponseDTO>,response: Response<LoginResponseDTO>) {
                    progressBar.visibility = View.GONE
                    if(response.isSuccessful){
                        val loginResponseDTO = response.body()
                        if(loginResponseDTO != null && loginResponseDTO.success){
                            Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                            Log.d("로그", "로그인 성공")
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                            Log.d("로그", "로그인 실패")
                        }
                    }
                    else{
                        Toast.makeText(applicationContext, "서버 응답 실패", Toast.LENGTH_SHORT).show()
                        Log.d("로그", "서버 응답 실패")
                    }
                }

                override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "통신 실패", Toast.LENGTH_SHORT).show()
                    Log.d("로그", "통신 실패")
                }
            })

        }


        //로그인 버튼 -> 아이디 비번 확인만 없으면 없다고 메세지 (DB확인)
        //카카오 버튼, 회원가입 버튼 -> 회원가입 버튼은 바로, 카카오 버튼은 DB확인 후 사용자가 처음접속이면 회원가입으로, 아니면 바로 HOME

        /*val signInBtn = findViewById<TextView>(R.id.sign_in_button)
        signInBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("nick", "사용자${ Random.nextInt(10000)}")
            intent.putExtra("profile", "https://cdn-icons-png.flaticon.com/128/149/149071.png")
            startActivity(intent)
        }*/

        val sinUpBtn = findViewById<TextView>(R.id.sign_up_button)
        sinUpBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("nick", "사용자${ Random.nextInt(10000)}")
            intent.putExtra("profile", "https://ff2dd35ee65da880a23ef33fbebbd708af3c75da75d75ad390d43a6-apidata.googleusercontent.com/download/storage/v1/b/goyo-storage/o/profile%2Fedit_profile.png?jk=ATxpoHe09QglPf0jIp5BsIqm8PIXiGs85cvCTn12UOzNrMCdQ3VtQjLnNT4Yw_BEyoB6mRrox6eNrcdYWQW95_UHAWaXCu6huhh9rQ2MxwewTkoRnLXzkgxSlE_8xPsRsIXpKj76tFpkLpDlGUDDsr8DJDE5s9TkIQQBdct68xs-WBLhWY5kJpQfqN5yOGIluMTGxheCLfFarff6aTsTFYyA7KROFlebPenVzrbmh2UZsaRTofnOMYf3dOjy99133n8A8j-UdhdA3eEga5qxeXFO9Ln_6Um6eefiTiBz6BieUuTI7VsQyuvJ65bN5yCe2xY8fVoiA-NgxMOZqTdz37NMu3V1l5P3NoGRlnlj2tqkwJQSlWJZi-bheMGQb4GVcL7eFswhKUXD406JqZC_A-SK_YbI1WIoKSpYsuzK87C1Yyhq16_O4wS3g5w-rLMDfQAQgu6lMGvPt_qmaYUt4Zer8lLtXKAVdlxJ2lx87WqrwryjUhIVzs0yQN4xB6rs81VqwoW3pyIBxg0X3xf7a1O7bakrkDW4GHJu1R2-FXmzH0mnM-NyGe4ByUX_IN_mBsUVT6ysfg4KYHKP77HUMyMQaShArWwo_6fIWq0Cj8IZWFZbo44oXrPiYuve1gp9roPUlOoOw-f5ewf8C6KeangPm0R_6B6EyCAbrHk6HdhMOVIVHdt0bcCyXHpnc7apTowmjLuYxJup2N6HymJJrqOpRr2ayxesZ9Z40apkPL-Nqd13TeIWu-jbbxU2YgaRKjPuzBzut7sEHfzzjXzSkcdlpZKxHpUFQpwXYBzPAXO97swr3276pCI6dw5JdyxThpp81OJEFUV9ZiahRzH_t7vj7nee3rDpF_8HOPMR-cjTqk7gDezmCkXsqC-oRdT0gdocKN_9H5VGezqpC2G_F1cWpuvNrjzsP7eVYKPc5EWZHRm2DgYHFu4beHfO2bDfSS1fQEoiESTfkELVFMFrgIQwqoSoNsFU-PAZGIzcVY0BvCdQhg8mbJr4oB630r906Bcs1jEatXa-EDacslOVG18fGNaCPzLFkRm21R5KLxImyXWpmpKM68HOpMHih90e-O0XKedv7PPMu-mDiCuR1xxn3pH07jvkB5zWbwmwG4Ca3oKCfs6c9wTzqIj3kA6sNKTAtZvbHv81OBpv0QODXg&isca=1")
            startActivity(intent)
            finish()
        }
        val kakaoBtn = findViewById<ImageView>(R.id.kko_login_btn)
        kakaoBtn.setOnClickListener {
            kakaoLogin(this)
            Log.d("카카오버튼","눌림")
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