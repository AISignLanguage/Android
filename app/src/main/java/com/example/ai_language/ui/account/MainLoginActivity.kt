package com.example.ai_language.ui.account

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.ai_language.MyApp
import com.example.ai_language.R
import com.example.ai_language.Util.EncryptedSharedPreferencesManager
import com.example.ai_language.Util.RetrofitClient
import com.example.ai_language.Util.extensions.datastore
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityMainLoginBinding
import com.example.ai_language.ui.find.FindIdPwd
import com.example.ai_language.ui.home.Home
import com.example.ai_language.ui.vibration.SoundDetectionService
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class MainLoginActivity : BaseActivity<ActivityMainLoginBinding>(R.layout.activity_main_login) {

    private lateinit var userEmail: EditText
    private lateinit var userPw: EditText
    private lateinit var progressBar: ProgressBar
    private val disposables = CompositeDisposable()
    private var isServiceRunning = false // 서비스 실행 여부를 추적하는 변수
    private var soundDetectionService: SoundDetectionService? = null // 서비스 인스턴스를 추적하는 변수
    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    // 자동 로그인 함수 -> 아이디, 비밀번호가 자동으로 입력됨
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

    // 로그인 함수
    private fun loginUser(inputUserEmail: String, inputUserPw: String) {
        progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE

        RetrofitClient.getInstance()
        val service = RetrofitClient.getUserRetrofitInterface()
        val encryptedSharedPreferencesManager = EncryptedSharedPreferencesManager(this)
        encryptedSharedPreferencesManager.saveUserEmail(inputUserEmail) //유저 정보 요청 목적

        val call = service.login(inputUserEmail, inputUserPw)
        val intent = Intent(this, Home::class.java)
        call!!.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val accessToken = response.headers()["access"]
                    val refreshCookie = response.headers().get("Set-Cookie")
                    val refreshRegex = "refresh=([^;]*)".toRegex()
                    val matchResult = refreshCookie?.let { refreshRegex.find(it) }
                    val refreshToken = matchResult?.groupValues?.getOrNull(1)

                    if (accessToken != null && refreshToken != null) {
                        lifecycleScope.launch {
                            MyApp.getInstance().tokenManager.saveJwtToken(accessToken, refreshToken)

                            Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                            Log.d("로그", "로그인 성공")
                            encryptedSharedPreferencesManager.saveUserEmail(inputUserEmail)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.d("로그", "로그인 실패")
                    }
                } else {
                    Toast.makeText(applicationContext, "서버 응답 실패", Toast.LENGTH_SHORT).show()
                    Log.d("로그", "서버 응답 실패")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "통신 실패", Toast.LENGTH_SHORT).show()
                Log.d("로그", "통신 실패")
            }
        })
    }


    private fun setOnClickMapBtn() {
//        val mapBtn = binding.btMap
//        mapBtn.setOnClickListener {
//            startActivity(Intent(this, MapActivity::class.java))
//        }
//        val speechFlowBtn = binding.btnTestSpeechFlow
//        speechFlowBtn.setOnClickListener {
//            startActivity(Intent(this,TranslationActivity::class.java))
//        }
    }

    private fun startDetectionService() {
        val serviceIntent = Intent(this, SoundDetectionService::class.java)
        if (!isServiceRunning) {
            ContextCompat.startForegroundService(this, serviceIntent)
            isServiceRunning = true
        } else {
            Toast.makeText(this, "서비스가 이미 실행 중입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopDetectionService() {
        val serviceIntent = Intent(this, SoundDetectionService::class.java)
        if (isServiceRunning) {
            stopService(serviceIntent)
            isServiceRunning = false
        } else {
            Toast.makeText(this, "서비스가 실행 중이지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }


//    // -> 모델 확인용 코드
//    private val MODEL_NAME = "converted_model_raw.tflite"
//    lateinit var interpreter: Interpreter
//
//    // -> 모델 확인용 코드
//    fun init() {
//        val model = loadModelFile(MODEL_NAME)
//        model.order(ByteOrder.nativeOrder())
//        interpreter = Interpreter(model)
//        initModelShape()
//    }
//
//    // 2. 모델 로드 하는 코드(2) -> 모델 확인용 코드
//    private fun loadModelFile(modelName: String): ByteBuffer {
//        val am = this.assets
//        val afd = am.openFd(modelName)
//        val fis = FileInputStream(afd.fileDescriptor)
//        val fc = fis.channel
//        val startOffset = afd.startOffset
//        val declaredLength = afd.declaredLength
//        return fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }
//
//    // 3. interpreter를 통하여 0 번째 인덱스의 Tensor 를 가져오는 코드 -> 모델 확인용 코드
//    private fun initModelShape() {
//        val inputTensor = interpreter.getInputTensor(0)
//        val inputShape: IntArray = inputTensor.shape()
//        val modelInputChannel = inputShape[0]
//        val modelInputWidth = inputShape[1]
//        val modelInputHeight = inputShape[2]
//        val outputTensor = interpreter.getOutputTensor(0)
//        val outputShape: IntArray = outputTensor.shape()
//        val modelOutputClasses = outputShape[1]
//
//        // 입력 텐서 관련 로그
//        Log.d("모델", "입력 텐서 채널 수: $modelInputChannel")
//        Log.d("모델", "입력 텐서 너비: $modelInputWidth")
//        Log.d("모델", "입력 텐서 높이: $modelInputHeight")
//
//        // 출력 텐서 관련 로그
//        Log.d("모델", "출력 텐서 클래스 수: $modelOutputClasses")
//    }

    override fun setLayout() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        }

        //init() // 모델 확인용 코드

        setOnClickMapBtn()
        //아이디 잃어버렸을 때
        val forgetPage = binding.forgetPage
        forgetPage.setOnClickListener {
            val intent = Intent(this, FindIdPwd::class.java)
            startActivity(intent)
        }

        //테스트 용 홈 화면 이동
        val button3 = binding.button3
        button3.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        //로그인 비동기 처리 - retrofit
        userEmail = binding.userEmail
        userPw = binding.userPw

        val signInBtn = binding.signInButton
        signInBtn.setOnClickListener {
            val inputUserEmail = userEmail.text.toString()
            val inputUserPw = userPw.text.toString()
            val encryptedSharedPreferencesManager = EncryptedSharedPreferencesManager(this)
            val autoLoginCheckBtn = binding.radioButton

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

        val sinUpBtn = binding.signUpButton
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

        //카카오로그인
        val kakaoBtn = binding.kkoLoginBtn
        kakaoBtn.setOnClickListener {
            validAccessToken()
        }
    }

    private fun kakaoLogin(ctxt: Context) {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("결과", "카카오계정으로 로그인 실패: $error")
                } else if (token != null) {
                    requestUserInfoAndStartRegisterActivity(ctxt)
                    Log.e("성공", "로그인 성공 ${token.accessToken}")
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(ctxt)) {
                UserApiClient.instance.loginWithKakaoTalk(ctxt) { token, error ->
                    if (error != null) {
                        Log.d("결과", "카카오톡 로그인 실패", error)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        else {
                            UserApiClient.instance.loginWithKakaoAccount(
                                this,
                                callback = callback
                            ) // 카카오 이메일 로그인
                        }
                    } else if (token != null) {
                        requestUserInfoAndStartRegisterActivity(ctxt)
                        lifecycleScope.launch {
                            Log.e("토큰", "${datastore.data.toString()}")
                            // Call suspend functions within the coroutine scope
                            MyApp.getInstance().tokenManager.saveToken(token)
                            MyApp.getInstance().tokenManager.findToken.collect{
                                Log.d("토큰", "User ID: $it")
                                Log.d("토큰", "Access Token: ${token.accessToken}")
                                Log.d("토큰", "Access Token Expires At: ${token.accessTokenExpiresAt}")
                                Log.d("토큰", "Refresh Token: ${token.refreshToken}")
                                Log.d("토큰", "Refresh Token Expires At: ${token.refreshTokenExpiresAt}")
                                Log.d("토큰", "ID Token: ${token.idToken}")
                                Log.d("토큰", "Scopes: ${token.scopes}")
                                MyApp.getInstance().tokenManager.findToken.collect { token ->
                                    Log.d("데이터", token ?: "토큰이 없습니다.") // token이 null인 경우에 대비하여 null 체크
                                }

                            }
                        }
                    }
                }
            } else {
                // Use callback to attempt login
                UserApiClient.instance.loginWithKakaoAccount(ctxt, callback = callback)
            }
    }

    private fun validAccessToken(){
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, "토큰 실패", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    kakaoLogin(this@MainLoginActivity)
                }
                Log.d("카카오버튼", "눌림")
            }
            else if (tokenInfo != null) {
                Log.d("토큰정보","$tokenInfo")
                Toast.makeText(this, "토큰 성공", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Home::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
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