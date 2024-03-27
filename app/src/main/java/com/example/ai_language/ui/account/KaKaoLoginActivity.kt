package com.example.ai_language.ui.account

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
import com.example.ai_language.R
import com.example.ai_language.Util.RetrofitClient
import com.example.ai_language.Util.EncryptedSharedPreferencesManager
import com.example.ai_language.domain.model.request.LoginRequestDTO
import com.example.ai_language.domain.model.request.LoginResponseDTO
import com.example.ai_language.find.FindIdPwd
import com.example.ai_language.ui.home.Home
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

    private fun attemptLogin() {

        val encryptedSharedPreferences = EncryptedSharedPreferencesManager(this)
        val loginInfo = encryptedSharedPreferences.getLoginInfo()

        //sharedPreferencesManager에 id, password 있는 경우
        if (loginInfo.isNotEmpty()) {
            val inputUserEmail = loginInfo["email"].toString()
            val inputUserPw = loginInfo["password"].toString()

            if (inputUserEmail.isNotEmpty() && inputUserPw.isNotEmpty()) {
                userEmail.setText(inputUserEmail)
                userPw.setText(inputUserPw)
            }
        }
    }

    private fun loginUser(inputUserEmail: String, inputUserPw: String) {
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        RetrofitClient.getInstance()
        val service = RetrofitClient.getUserRetrofitInterface()
        val encryptedSharedPreferencesManager = EncryptedSharedPreferencesManager(this)
        encryptedSharedPreferencesManager.saveUserEmail(inputUserEmail) //유저 정보 요청 목적

        val call = service.login(LoginRequestDTO(inputUserEmail, inputUserPw))
        val intent = Intent(this, Home::class.java)
        call.enqueue(object : Callback<LoginResponseDTO> {
            override fun onResponse(
                call: Call<LoginResponseDTO>,
                response: Response<LoginResponseDTO>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val loginResponseDTO = response.body()
                    if (loginResponseDTO != null && loginResponseDTO.success) {
                        Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                        Log.d("로그", "로그인 성공")

                        encryptedSharedPreferencesManager.saveUserEmail(inputUserEmail)

                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.d("로그", "로그인 실패")
                    }
                } else {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ka_kao_login)

        val forgetPage = findViewById<TextView>(R.id.forgetPage)
        forgetPage.setOnClickListener {
            val intent = Intent(this, FindIdPwd::class.java)
            startActivity(intent)
        }

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

        val signInBtn = findViewById<TextView>(R.id.sign_in_button)
        signInBtn.setOnClickListener {
            val inputUserEmail = userEmail.text.toString()
            val inputUserPw = userPw.text.toString()
            val encryptedSharedPreferencesManager = EncryptedSharedPreferencesManager(this)
            val autoLoginCheckBtn = findViewById<RadioButton>(R.id.radioButton)

            if (autoLoginCheckBtn.isChecked) {
                Log.d("로그", "첫 로그인, id: ${inputUserEmail}, pwd: ${inputUserPw}")
                encryptedSharedPreferencesManager.setLoginInfo(
                    inputUserEmail,
                    inputUserPw
                ) //자동 로그인 목적
            }
            loginUser(inputUserEmail, inputUserPw)
        }

        attemptLogin()

        //로그인 버튼 -> 아이디 비번 확인만 없으면 없다고 메세지 (DB확인)
        //카카오 버튼, 회원가입 버튼 -> 회원가입 버튼은 바로, 카카오 버튼은 DB확인 후 사용자가 처음접속이면 회원가입으로, 아니면 바로 HOME

        val sinUpBtn = findViewById<TextView>(R.id.sign_up_button)
        sinUpBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("nick", "사용자${Random.nextInt(10000)}")
            intent.putExtra(
                "profile",
                "https://storage.googleapis.com/goyo-storage/profile/edit_profile.png"
            )
            startActivity(intent)
            finish()
        }
        val kakaoBtn = findViewById<ImageView>(R.id.kko_login_btn)
        kakaoBtn.setOnClickListener {
            kakaoLogin(this)
            Log.d("카카오버튼", "눌림")
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