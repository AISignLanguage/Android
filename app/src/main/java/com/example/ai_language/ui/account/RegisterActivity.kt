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
import android.os.Bundle
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ai_language.R
import com.example.ai_language.Util.RetrofitClient
import com.example.ai_language.data.remote.Service
import com.example.ai_language.domain.model.request.ConfirmDTO
import com.example.ai_language.domain.model.request.ConfirmedDTO
import com.example.ai_language.domain.model.request.LoginCheckDTO
import com.example.ai_language.domain.model.request.UserDTO
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
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

class RegisterActivity : AppCompatActivity() {
    lateinit var call: Call<LoginCheckDTO>
    lateinit var conf: Observable<ConfirmedDTO>
    lateinit var service: Service
    private val disposables = CompositeDisposable()
    private lateinit var progressBar: ProgressBar
    private val STORAGE_PERMISSION_CODE = 1
    private val SMS_PERMISSION_CODE = 2
    private val ACCOUNT_SID = "${R.string.SID}"
    private val AUTH_TOKEN = "${R.string.TK}"
    private val MESSAGIING_SERVICE = "${R.string.MS}"
    private var url: String = "https://cdn-icons-png.flaticon.com/128/149/149071.png"
    private lateinit var profile: ImageView
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

    fun sendSMS2(phoneNumber: String, message: String) {
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

    fun formatDate(originalDate: String): String {
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
        val intent = Intent(this, KaKaoLoginActivity::class.java)
        startActivity(intent)
        finish() // RegisterActivity 종료
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        RetrofitClient.getInstance()
        progressBar = findViewById(R.id.reg_progressbar)
        service = RetrofitClient.getUserRetrofitInterface()
        var end = false
        var pn = "pn"
        var correct = 1
        val nick = intent.getStringExtra("nick")
        val regName = findViewById<EditText>(R.id.reg_name)
        val regEmail = findViewById<EditText>(R.id.reg_email)
        val regPwd = findViewById<EditText>(R.id.reg_pwd)
        val regBirthdate = findViewById<EditText>(R.id.reg_birthdate)
        val regNick = findViewById<EditText>(R.id.reg_nick)
        val send_certification_et = findViewById<EditText>(R.id.send_certification_et)
        val send_certification_btn = findViewById<Button>(R.id.send_certification_btn)
        val certification_et = findViewById<EditText>(R.id.certification_et)
        val certification_btn = findViewById<Button>(R.id.certification_btn)
        var randomSixDigitNumber = 0
        var phNum = ""

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


        regName.setText(nick)

        profile = findViewById(R.id.reg_pro)
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


        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$"
        val patternPW = Pattern.compile(passwordPattern)
        var birthday = "bd"
        val birthdatePattern = "^\\d{8}$"
        val patternBD = Pattern.compile(birthdatePattern)


        var name = regName.text.toString()
        var em = "em"
        var nk = "nk"
        var bd = "bd"
        var pw = "pw"
        val regNext = findViewById<TextView>(R.id.reg_next)

        Log.d("RegisterActivity", "Showing ProgressBar")
        val confirmedNicknameBtn = findViewById<Button>(R.id.confirm_nickname_btn)
        confirmedNicknameBtn.setOnClickListener {
            nk = regNick.text.toString() // 닉네임
            if (regNick.text.toString().length in 2..6) {
                conf = service.confirmNick(ConfirmDTO(nk))
                progressBar.visibility = View.VISIBLE
                disposables.add(
                    conf.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<ConfirmedDTO>() {
                            override fun onNext(confirmedDTO: ConfirmedDTO) {
                                // onSuccess
                                val responseOK = confirmedDTO.ok
                                if (!responseOK) {
                                    Log.d("서버로부터 받은 요청", "닉네임 : $responseOK")
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "중복확인 완료!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loginChecked.nickCheck = true
                                    regNick.isEnabled = false
                                    regNick.setTextColor(Color.GREEN)
                                } else {
                                    Log.d("서버로부터 받은 요청", "닉네임 : $responseOK")
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "중복된 닉네임이 존재합니다!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    regNick.setText("")
                                }
                            }

                            override fun onError(e: Throwable) {
                                // onError
                                Log.d("RegisterActivity", "Showing ProgressBar")
                                progressBar.visibility = View.GONE
                                Log.e("retrofit 연동", "실패", e)
                            }

                            override fun onComplete() {
                                // onComplete
                                Log.d("RegisterActivity", "Showing ProgressBar")
                                progressBar.visibility = View.GONE
                            }
                        })
                )
            } else {
                Toast.makeText(this@RegisterActivity, "올바르지 않은 닉네임 형식입니다.", Toast.LENGTH_SHORT)
                    .show()

            }
        }


        val confirmedEmailBtn = findViewById<Button>(R.id.confirm_email_btn)
        confirmedEmailBtn.setOnClickListener {
            val em = regEmail.text.toString() // 이메일
            if (Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                val conf = service.confirmEmail(ConfirmDTO(em))
                progressBar.visibility = View.VISIBLE
                disposables.add(
                    conf.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<ConfirmedDTO>() {
                            override fun onNext(confirmedDTO: ConfirmedDTO) {
                                // onSuccess
                                val responseOK = confirmedDTO.ok
                                if (!responseOK) {
                                    Log.d("서버로부터 받은 요청", "이메일 : $responseOK")
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "중복확인 완료!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loginChecked.idCheck = true
                                    regEmail.isEnabled = false
                                    regEmail.setTextColor(Color.GREEN)
                                } else {
                                    Log.d("서버로부터 받은 요청", "이메일 : $responseOK")
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "중복된 이메일이 존재합니다!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    regEmail.setText("")
                                }
                            }

                            override fun onError(e: Throwable) {
                                // onError
                                Log.d("RegisterActivity", "Showing ProgressBar")
                                progressBar.visibility = View.GONE
                                Log.e("retrofit 연동", "실패", e)
                            }

                            override fun onComplete() {
                                // onComplete
                                Log.d("RegisterActivity", "Showing ProgressBar")
                                progressBar.visibility = View.GONE
                            }
                        })
                )
            } else {
                Toast.makeText(this@RegisterActivity, "올바르지 않은 이메일 형식입니다.", Toast.LENGTH_SHORT)
                    .show()

            }
        }


        try {

            regNext.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                name = regName.text.toString() // 이름
                em = regEmail.text.toString() // 이메일
                pw = regPwd.text.toString() //비번
                nk = regNick.text.toString() // 닉네임
                birthday = regBirthdate.text.toString() //생일
                try {
                    val formattedDate = formatDate(birthday)
                    bd = formattedDate
                } catch (e: StringIndexOutOfBoundsException) {
                    // 예외 발생 시 사용자에게 토스트 메시지를 보여주고 LoginActivity로 이동
                    Toast.makeText(this, "올바르지 않은 필드가 존재합니다.", Toast.LENGTH_SHORT).show()
                }
                if (loginChecked.finish) {
                    val userDTO = UserDTO(
                        name, //이름 => 공백이 아니어야함
                        bd, //생일 => xxxx-xx-xx 형태
                        em, // => 이메일 xxx@xxx.xxx
                        pw, // 비밀 번호 => 최소 8자, 최대 15자
                        nk, // 닉네임
                        pn, //핸드폰 번호 => 010-xxxx-xxxx
                        // 정규식 =>  @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다.")
                        url //프로필 사진 url
                    )

                    call = service.sendData(userDTO)
                    call.clone().enqueue(object : Callback<LoginCheckDTO> {
                        override fun onResponse(
                            call: Call<LoginCheckDTO>,
                            response: Response<LoginCheckDTO>
                        ) {
                            progressBar.visibility = View.GONE
                            if (response.isSuccessful) {
                                Log.d("서버로부터 받은 요청", "닉네임 : ${response.body()?.nickName}")
                                Log.d("서버로부터 받은 요청", "이름 : ${response.body()?.name}")
                                Log.d("서버로부터 받은 요청", "이메일 : ${response.body()?.email}")
                                Log.d("서버로부터 받은 요청", "생일 : ${response.body()?.birthdate}")
                                Log.d("서버로부터 받은 요청", "폰넘버 : ${response.body()?.phoneNumber}")
                                Log.d("서버로부터 받은 요청", "프로필 : ${response.body()?.profileImageUrl}")
                                Log.d("서버로부터 받은 요청", "날짜 : ${response.body()?.registerdAt}")
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "회원가입에 성공하셨습니다!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                val intent =
                                    Intent(this@RegisterActivity, permissionActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.d("서버실패?", "실패")
                            }
                        }

                        override fun onFailure(call: Call<LoginCheckDTO>, t: Throwable) {
                            progressBar.visibility = View.GONE
                            Log.e("retrofit 연동", "실패", t)
                        }

                    }
                    )

                } else {

                    if (name.length <= 5) {
                        loginChecked.nameCheck = true
                    }

                    if (patternPW.matcher(regPwd.text.toString()).matches()) {

                        loginChecked.pwCheck = true
                    }

                    if (patternBD.matcher(birthday).matches()) {

                        loginChecked.birthdayCheck = true
                    }

                    if (!loginChecked.nameCheck) {
                        Toast.makeText(this, "이름을 확인해주세요${name}", Toast.LENGTH_SHORT).show()
                        regName.setText("")
                        progressBar.visibility = View.GONE
                    } else if (!loginChecked.idCheck) {
                        Toast.makeText(this, "아이디 형식이나, 인증여부를 확인해주세요.${em}", Toast.LENGTH_SHORT)
                            .show()
                        regEmail.setText("")
                        progressBar.visibility = View.GONE

                    } else if (!loginChecked.pwCheck) {
                        Toast.makeText(this, "비밀 번호 형식을 확인해주세요", Toast.LENGTH_SHORT).show()
                        regPwd.setText("")
                        progressBar.visibility = View.GONE

                    } else if (!loginChecked.nickCheck) {
                        Toast.makeText(this, "닉네임을 확인해주세요", Toast.LENGTH_SHORT).show()
                        regNick.setText("")
                        progressBar.visibility = View.GONE

                    } else if (!loginChecked.birthdayCheck) {
                        Toast.makeText(this, "생년월일을 확인해주세요", Toast.LENGTH_SHORT).show()
                        regBirthdate.setText("")
                        progressBar.visibility = View.GONE
                    } else if (!loginChecked.phoneCheck) {
                        Toast.makeText(this, "휴대폰 인증을 확인해주세요", Toast.LENGTH_SHORT).show()
                        send_certification_et.setText("")
                        progressBar.visibility = View.GONE
                    } else {
                        loginChecked.finish = true
                        progressBar.visibility = View.GONE
                    }

                }
            }
        } catch (e: StringIndexOutOfBoundsException) {
            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
        }

        val signInBtn = findViewById<TextView>(R.id.signin_button)
        signInBtn.setOnClickListener {
            val intent = Intent(this, KaKaoLoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun convertPhoneNumber(phoneNumber: String): String {
        // 번호가 "010"으로 시작하고 11자리인 경우 "+82"를 추가
        if (phoneNumber.startsWith("010") && phoneNumber.length == 11) {
            return phoneNumber.replaceFirst("010", "+8210")
        }
        return phoneNumber
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
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