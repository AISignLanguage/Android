package com.example.ai_language.ui.account

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.telephony.SmsManager
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.ai_language.R
import com.example.ai_language.Util.RetrofitClient
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.data.remote.Service
import com.example.ai_language.databinding.ActivityRegisterBinding
import com.example.ai_language.domain.model.request.ConfirmRequestDTO
import com.example.ai_language.domain.model.request.ConfirmedDTO
import com.example.ai_language.domain.model.request.JoinDTO
import com.example.ai_language.domain.model.request.LoginCheckDTO
import com.example.ai_language.ui.account.viewmodel.AccountViewModel
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.regex.Pattern
import kotlin.random.Random

data class LoginChecked(
    var nameCheck: Boolean,
    var idCheck: Boolean,
    var pwCheck: Boolean,
    var nickCheck: Boolean,
    var birthdayCheck: Boolean,
    var phoneCheck: Boolean,
    var finish: Boolean

)

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding>(R.layout.activity_register) {
    lateinit var call: Call<LoginCheckDTO>
    lateinit var conf: Observable<ConfirmedDTO>
    lateinit var confNick: Observable<ResponseBody>
    lateinit var service: Service
    private val disposables = CompositeDisposable()
    private val STORAGE_PERMISSION_CODE = 1
    private val SMS_PERMISSION_CODE = 2
    private val ACCOUNT_SID = "${R.string.SID}"
    private val AUTH_TOKEN = "${R.string.TK}"
    private val MESSAGIING_SERVICE = "${R.string.MS}"
    private var url: String = "https://cdn-icons-png.flaticon.com/128/149/149071.png"
    private lateinit var profile: ImageView

    // 회원가입 로직에 쓰이는 변수들 -> 함수로 분리하기 위해 전역변수로 선언
    private var nk = "nk"
    private var pn = "pn"
    private var phNum = ""

    private lateinit var progressBar: ProgressBar
    private lateinit var regName: EditText
    private lateinit var regEmail: EditText
    private lateinit var regPwd: EditText
    private lateinit var regBirthdate: EditText
    private lateinit var regNick: EditText
    private lateinit var send_certification_et: EditText
    private lateinit var send_certification_btn: Button
    private lateinit var certification_et: EditText
    private lateinit var certification_btn: Button
    private var randomSixDigitNumber: Int = 0

    private val accountViewModel by viewModels<AccountViewModel>()
    //private var end = false
    //var correct = 1

    private val loginChecked = LoginChecked(
        nameCheck = false,
        idCheck = false,
        pwCheck = false,
        nickCheck = false,
        birthdayCheck = false,
        phoneCheck = false,
        finish = false
    )

    private fun getStorageService(): Storage {
        val assetManager = this@RegisterActivity.assets
        val inputStream = assetManager.open("goyo-415004-d79f31fe39d5.json")
        val credentials = GoogleCredentials.fromStream(inputStream)
        return StorageOptions.newBuilder().setCredentials(credentials).build().service
    }


    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val selectedImageUri: Uri? = intent?.data
                if (selectedImageUri != null) {
                    Glide.with(this) // 'this'는 Context 객체를 참조해야 합니다. 필요에 따라 변경해주세요.
                        .load(selectedImageUri)
                        .circleCrop() // 이미지를 원형으로 잘라냅니다.
                        .into(profile) // 'profile'은 이미지를 설정할 ImageView의 ID입니다.
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val storage = getStorageService()
                    try {
                        val blobId = BlobId.of(
                            "goyo-storage",
                            "profile/${System.currentTimeMillis()}.png"
                        ) // 파일 확장자를 .png로 설정
                        val blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png")
                            .build() // ContentType을 image/png로 설정

                        val inputStream =
                            selectedImageUri?.let { contentResolver.openInputStream(it) }
                        val bitmap = BitmapFactory.decodeStream(inputStream) // Uri에서 Bitmap 생성
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.PNG,
                            100,
                            byteArrayOutputStream
                        ) // Bitmap을 PNG 형식으로 압축
                        val byteImage = byteArrayOutputStream.toByteArray()

                        val blob = storage.create(blobInfo, byteImage) // PNG 형식의 바이트 배열을 업로드
                        blob.createAcl(
                            Acl.of(
                                Acl.User.ofAllUsers(),
                                Acl.Role.READER
                            )
                        ) // 이미지를 공개적으로 설정

                        // UI 스레드로 전환하여 UI 작업 실행
                        withContext(Dispatchers.Main) {
                            url = blob.mediaLink // 공개 URL 가져오기
                            // 필요한 UI 업데이트 작업
                        }
                    } catch (e: Exception) {
                        // 오류 처리
                        Log.e("RegisterActivity", "Image upload failed", e)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "이미지 업로드 실패", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

    private fun sendSMS2(phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        val sentIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            Intent("SMS_SENT"),
            PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE 사용
        )

        smsManager.sendTextMessage(phoneNumber, null, message, sentIntent, null)
    }

    // SMS를 보내는 함수
    private fun sendSMS(to: String, sms: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                // 업데이트된 URI
                val url = "https://api.twilio.com/2010-04-01/Accounts/$ACCOUNT_SID/Messages"
                val base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    "$ACCOUNT_SID:$AUTH_TOKEN".toByteArray(),
                    Base64.NO_WRAP
                )

                val body = FormBody.Builder()
                    .add("From", MESSAGIING_SERVICE) // 메시징 서비스 ID
                    .add("To", to)
                    .add("Body", sms)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Authorization", base64EncodedCredentials)
                    .build()

                val response = client.newCall(request).execute()
                withContext(Dispatchers.Main) {
                    Log.d("RegisterActivity", "sendSMS: " + response.body?.string())
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Log.d("Error", "sendSMS: " + e.message)
                }
            }
        }
    }

    private fun formatDate(originalDate: String): String {
        // 연도, 월, 일을 추출합니다.
        val year = originalDate.substring(0, 4)
        val month = originalDate.substring(4, 6)
        val day = originalDate.substring(6, 8)

        // 추출한 값을 '-'로 연결하여 새로운 형식으로 조합합니다.
        return "$year-$month-$day"
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return "${phoneNumber.substring(0, 3)}-${
            phoneNumber.substring(
                3,
                7
            )
        }-${phoneNumber.substring(7, 11)}"

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainLoginActivity::class.java)
        startActivity(intent)
        finish() // RegisterActivity 종료
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun setLayout() {
        registerUser()
    }

    // 회원가입 로직에 사용되는 변수 초기화
    private fun initializeViews() {
        progressBar = binding.regProgressbar
        regName = binding.regName
        regEmail = binding.regEmail
        regPwd = binding.regPwd
        regBirthdate = binding.regBirthdate
        regNick = binding.regNick
        send_certification_et = binding.sendCertificationEt
        send_certification_btn = binding.sendCertificationBtn
        certification_et = binding.certificationEt
        certification_btn = binding.certificationBtn
    }

    // 핸드폰 번호로 인증번호 전송하는 함수
    private fun sendCertificationNumber() {
        send_certification_btn.setOnClickListener {
            phNum = send_certification_et.text.toString()
            if (!isValidPhoneNumber(phNum)) {
                // 전화번호가 "010"으로 시작하지 않거나 11자리가 아닌 경우 서버에서 false가 옴
                Toast.makeText(this, "올바른 전화번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                val random = Random.Default
                randomSixDigitNumber =
                    random.nextInt(100000, 999999) // 범위를 100000부터 999999까지로 지정하여 6자리 랜덤 숫자 생성
                val num = convertPhoneNumber(phNum) // 폰넘버
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.SEND_SMS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //sendSMS(num,"[손짓의 순간] 인증번호는 ${randomSixDigitNumber} 입니다.") -> 실제비용청구되므로 연습할땐 아래코드로
                    sendSMS2(num, "[손짓의 순간] 인증번호는 ${randomSixDigitNumber} 입니다.")
                    Log.d("왔", num)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.SEND_SMS),
                        SMS_PERMISSION_CODE // 권한 요청 코드를 정의해야 함
                    )
                }
            }
        }


    }

    // 인증번호 인증하는 함수
    private fun checkCertificationNumber() {
        certification_btn.setOnClickListener {
            if (randomSixDigitNumber.toString() == certification_et.text.toString()) {
                certification_et.setTextColor(Color.GREEN)
                certification_et.setText("인증되었습니다!")
                certification_et.isEnabled = false
                pn = formatPhoneNumber(send_certification_et.text.toString())
                loginChecked.phoneCheck = true
            } else {
                certification_et.setTextColor(Color.RED)
                certification_et.setText("인증번호가 잘못되었습니다.")
                certification_et.setText("")
            }
        }
    }

    // 프로필 이미지를 설정하는 함수
    private fun setProfileImage() {
        profile = binding.regPro
        val uriString: String? = intent.getStringExtra("profile")
        if (uriString != null) {
            val profilePx = dpToPx(this, 90)
            Glide.with(this)
                .load(uriString)
                .override(profilePx, profilePx)
                .circleCrop()
                .into(profile)
        } else {
            Log.e("uri 에러", "$uriString")
        }
        profile.setOnClickListener {
            val galleryPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (galleryPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                openGallery()
            }
        }

    }

    // 닉네임 중복 확인 함수
    private fun confirmNickname() {
        val confirmedNicknameBtn = binding.confirmNicknameBtn
        confirmedNicknameBtn.setOnClickListener {
            nk = regNick.text.toString() // 닉네임
            if (regNick.text.toString().length in 2..6) {
                val confirmRequestDTO = ConfirmRequestDTO("", regNick.text.toString())
                val call: Call<ResponseBody> = service.confirmNick(confirmRequestDTO)
                progressBar.visibility = View.VISIBLE

                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {

                        if (response.isSuccessful) {
                            val responseText = response.body()?.string()
                            Log.d("로그", "response.body(): ${responseText}")

                            if (responseText.equals("Nickname available for use.")) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "중복확인 완료!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loginChecked.nickCheck = true
                                regNick.isEnabled = false
                                regNick.setTextColor(Color.GREEN)
                                progressBar.visibility = View.GONE
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "중복된 닉네임이 존재합니다!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                regNick.setText("")
                                progressBar.visibility = View.GONE
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.d("로그", "서버 오류: ${response.code()}, 내용: $errorBody")
                            progressBar.visibility = View.GONE
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("로그", "닉네임 중복 서버 연결 실패")
                        progressBar.visibility = View.GONE
                    }
                })
            }
        }


//        confirmedNicknameBtn.setOnClickListener {
//            nk = regNick.text.toString() // 닉네임
//            if (regNick.text.toString().length in 2..6) {
//                conf = service.confirmNick(ConfirmDTO(nk))
//                progressBar.visibility = View.VISIBLE
//                disposables.add(
//                    conf.subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(object : DisposableObserver<ConfirmedDTO>() {
//                            override fun onNext(confirmedDTO: ConfirmedDTO) {
//                                // onSuccess
//                                val responseOK = confirmedDTO.ok
//                                if (!responseOK) {
//                                    Log.d("서버로부터 받은 요청", "닉네임 : $responseOK")
//                                    Toast.makeText(
//                                        this@RegisterActivity,
//                                        "중복확인 완료!",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    loginChecked.nickCheck = true
//                                    regNick.isEnabled = false
//                                    regNick.setTextColor(Color.GREEN)
//                                } else {
//                                    Log.d("서버로부터 받은 요청", "닉네임 : $responseOK")
//                                    Toast.makeText(
//                                        this@RegisterActivity,
//                                        "중복된 닉네임이 존재합니다!",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    regNick.setText("")
//                                }
//                            }
//
//                            override fun onError(e: Throwable) {
//                                // onError
//                                Log.d("RegisterActivity", "Showing ProgressBar")
//                                progressBar.visibility = View.GONE
//                                Log.e("retrofit 연동", "실패", e)
//                            }
//
//                            override fun onComplete() {
//                                // onComplete
//                                Log.d("RegisterActivity", "Showing ProgressBar")
//                                progressBar.visibility = View.GONE
//                            }
//                        })
//                )
//            } else {
//                Toast.makeText(this@RegisterActivity, "올바르지 않은 닉네임 형식입니다.", Toast.LENGTH_SHORT)
//                    .show()
//
//            }
//        }
    }

    // 이메일 중복 확인 함수
    private fun confirmEmail() {
        val confirmedEmailBtn = binding.confirmEmailBtn
        confirmedEmailBtn.setOnClickListener {
            val em = regEmail.text.toString() // 이메일

            if (Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                val confirmRequestDTO = ConfirmRequestDTO(em, "")
                val call: Call<ResponseBody> = service.confirmEmail(confirmRequestDTO)
                progressBar.visibility = View.VISIBLE

                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {

                        if (response.isSuccessful) {
                            val responseText = response.body()?.string()
                            Log.d("로그", "response.body(): ${responseText}")

                            if (responseText.equals("Email available for use.")) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "중복확인 완료!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loginChecked.idCheck = true
                                regEmail.isEnabled = false
                                regEmail.setTextColor(Color.GREEN)
                                progressBar.visibility = View.GONE
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "중복된 이메일이 존재합니다!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                regEmail.setText("")
                                progressBar.visibility = View.GONE
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.d("로그", "서버 오류: ${response.code()}, 내용: $errorBody")
                            progressBar.visibility = View.GONE
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("로그", "아이디 중복 확인 서버 연결 실패")
                        progressBar.visibility = View.GONE
                    }
                })
            }
        }

//        val confirmedEmailBtn = binding.confirmEmailBtn
//        confirmedEmailBtn.setOnClickListener {
//            val em = regEmail.text.toString() // 이메일
//            if (Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
//                confNick = service.confirmEmail(em)
//                progressBar.visibility = View.VISIBLE
//                disposables.add(
//                    confNick.subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(object : DisposableObserver<ResponseBody>() {
//                            override fun onNext(resBody: ResponseBody) {
//                                // onSuccess
//                                Log.d("responseText", "onSuccess")
//                                val responseText = resBody.toString()
//                                val gson = Gson()
//                                val resText = gson.fromJson(responseText, ChangeNickNameResultDTO::class.java)
//
//                                Log.d("로그", "$responseText")
//                                if (resText.equals("Email is confirmed.")) {
//                                    Log.d("서버로부터 받은 요청", "이메일 : $resText")
//                                    Toast.makeText(
//                                        this@RegisterActivity,
//                                        "중복확인 완료!",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    loginChecked.idCheck = true
//                                    regEmail.isEnabled = false
//                                    regEmail.setTextColor(Color.GREEN)
//                                } else {
//                                    Log.d("서버로부터 받은 요청", "이메일 : $responseText")
//                                    Toast.makeText(
//                                        this@RegisterActivity,
//                                        "중복된 이메일이 존재합니다!",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    regEmail.setText("")
//                                }
//                            }
//
//                            override fun onError(e: Throwable) {
//                                // onError
//                                Log.d("RegisterActivity", "Showing ProgressBar")
//                                progressBar.visibility = View.GONE
//                                Log.e("retrofit 연동", "실패", e)
//                            }
//
//                            override fun onComplete() {
//                                // onComplete
//                                Log.d("RegisterActivity", "Showing ProgressBar")
//                                progressBar.visibility = View.GONE
//                            }
//                        })
//                )
//            } else {
//                Toast.makeText(this@RegisterActivity, "올바르지 않은 이메일 형식입니다.", Toast.LENGTH_SHORT)
//                    .show()
//
//            }
//        }
    }

    // 로그인 버튼 클릭시 로그인 화면으로 이동하는 함수
    private fun startLoginActivity() {
        val signInBtn = binding.signinButton
        signInBtn.setOnClickListener {
            val intent = Intent(this, MainLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // 회원가입 버튼 클릭시 회원가입 처리하는 함수
    private fun performRegistration() {

        val nick = intent.getStringExtra("nick")
        regName.setText(nick)

        val regNext = binding.regNext

        try {
            regNext.setOnClickListener {
                progressBar.visibility = View.VISIBLE

                // 각 입력 필드의 값 가져오기
                val name = binding.regName.text.toString()
                val em = binding.regEmail.text.toString()
                val pw = binding.regPwd.text.toString()
                val nk = binding.regNick.text.toString()
                val birthday = binding.regBirthdate.text.toString()

                // 유효성 검사 수행
                validateAndRegister(name, em, pw, birthday, nk)
            }
        } catch (e: StringIndexOutOfBoundsException) {
            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateAndRegister(name: String, em: String, pw: String, birthday: String, nk: String) {
        // 유효성 검사를 위한 패턴 및 로직 설정
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$"
        val patternPW = Pattern.compile(passwordPattern)
        val birthdatePattern = "^\\d{8}$"
        val patternBD = Pattern.compile(birthdatePattern)
        var bd = ""

        try {
            val formattedDate = formatDate(birthday)
            bd = formattedDate
        } catch (e: StringIndexOutOfBoundsException) {
            // 예외 발생 시 사용자에게 토스트 메시지를 보여주고 LoginActivity로 이동
            Toast.makeText(this, "올바르지 않은 필드가 존재합니다.", Toast.LENGTH_SHORT).show()
        }

        // 각 항목에 대한 유효성 검사 수행
        val isNameValid = name.length <= 5
        val isPasswordValid = patternPW.matcher(pw).matches()
        val isBirthdayValid = patternBD.matcher(birthday).matches()
        val isIdValid = loginChecked.idCheck
        val isNickNameValid = loginChecked.nickCheck
        val isPhoneValid = loginChecked.phoneCheck

        if (name.length <= 5) {
            loginChecked.nameCheck = true
        }

        if (patternPW.matcher(regPwd.text.toString()).matches()) {
            loginChecked.pwCheck = true
        }

        if (patternBD.matcher(birthday).matches()) {
            loginChecked.birthdayCheck = true
        }


        // 모든 항목의 유효성 검사가 통과
        if (isNameValid && isPasswordValid && isBirthdayValid && isIdValid && isNickNameValid && isPhoneValid) {
            loginChecked.finish = true

            val joinDTO = JoinDTO(
                em, // => 이메일 xxx@xxx.xxx
                pw, // 비밀 번호 => 최소 8자, 최대 15자
                name, //이름 => 공백이 아니어야함
                pn, //핸드폰 번호 => 010-xxxx-xxxx
                // 정규식 =>  @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다.")
                bd, //생일 => xxxx-xx-xx 형태
                nk, // 닉네임
                url //프로필 사진 url
            )

            // 회원가입 처리 등 추가 작업 수행
            lifecycleScope.launch {
                try {
                    val joinDTO = JoinDTO(
                        em, // => 이메일 xxx@xxx.xxx
                        pw, // 비밀 번호 => 최소 8자, 최대 15자
                        name, //이름 => 공백이 아니어야함
                        pn, //핸드폰 번호 => 010-xxxx-xxxx
                        // 정규식 =>  @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다.")
                        bd, //생일 => xxxx-xx-xx 형태
                        nk, // 닉네임
                        url //프로필 사진 url
                    )

                    //  회원가입을 시도 (중복 가입 요청 방지)
                    if (accountViewModel.joinOKDTO.value == null) {
                        Log.d("로그", "회원가입 시도")
                        accountViewModel.register(joinDTO)
                    }

                    accountViewModel.joinOKDTO.collect { joinOKDTO ->
                        if (joinOKDTO?.ok.equals("ok")) {
                            progressBar.visibility = View.GONE
                            Log.d("로그", "응답성공 : $joinOKDTO")
                            Toast.makeText(
                                this@RegisterActivity,
                                "회원가입에 성공하셨습니다!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(this@RegisterActivity, permissionActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("로그", "응답 오류: ${e.message}")
                }
            }
        } else {
            if (!loginChecked.nameCheck) {
                Toast.makeText(this, "이름을 확인해주세요${name}", Toast.LENGTH_SHORT).show()
                regName.setText("")
                progressBar.visibility = View.GONE
            } else if (!isIdValid) {
                Toast.makeText(this, "아이디 형식이나, 인증여부를 확인해주세요.${em}", Toast.LENGTH_SHORT)
                    .show()
                regEmail.setText("")
                progressBar.visibility = View.GONE

            } else if (!loginChecked.pwCheck) {
                Toast.makeText(this, "비밀 번호 형식을 확인해주세요", Toast.LENGTH_SHORT).show()
                regPwd.setText("")
                progressBar.visibility = View.GONE

            } else if (!isNickNameValid) {
                Toast.makeText(this, "닉네임을 확인해주세요", Toast.LENGTH_SHORT).show()
                regNick.setText("")
                progressBar.visibility = View.GONE

            } else if (!loginChecked.birthdayCheck) {
                Toast.makeText(this, "생년월일을 확인해주세요", Toast.LENGTH_SHORT).show()
                regBirthdate.setText("")
                progressBar.visibility = View.GONE
            } else if (!isPhoneValid) {
                Toast.makeText(this, "휴대폰 인증을 확인해주세요", Toast.LENGTH_SHORT).show()
                send_certification_et.setText("")
                progressBar.visibility = View.GONE
            } else {
                //loginChecked.finish = true
                progressBar.visibility = View.GONE
            }
        }
    }

    // 회원가입 로직 처리 함수
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun registerUser() {

        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()

        initializeViews() // 회원가입에 사용되는 변수 초기화
        setProfileImage() // 프로필 이미지를 설정하는 함수
        confirmEmail()    // 이메일 중복 확인하는 함수
        confirmNickname() // 닉네임 중복 확인하는 함수
        sendCertificationNumber() // 핸드폰 번호로 인증번호 전송 및 인증번호 확인하는 함수
        checkCertificationNumber() // 인증번호 확인하는 함수
        performRegistration()          //회원가입 수행 -> 필드 유효성 및 실제 회원가입 처리하는 함수 호출
        startLoginActivity() // 로그인 버튼 클릭시 로그인 화면으로 이동하는 함수
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    private fun convertPhoneNumber(phoneNumber: String): String {
        // 번호가 "010"으로 시작하고 11자리인 경우 "+82"를 추가
        if (phoneNumber.startsWith("010") && phoneNumber.length == 11) {
            return phoneNumber.replaceFirst("010", "+8210")
        }
        return phoneNumber
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.startsWith("010") && phoneNumber.length == 11
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        intent.action = Intent.ACTION_PICK
        galleryLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        }
    }

}