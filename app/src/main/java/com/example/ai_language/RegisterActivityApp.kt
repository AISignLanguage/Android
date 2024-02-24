package com.example.ai_language
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.kakao.sdk.talk.TalkApiClient
import android.widget.Button as B
import kotlin.random.Random
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.annotation.RequiresApi
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

data class LoginCheckedKaKao(
    var nameCheck: Boolean,
    var nickCheck: Boolean,
    var birthdayCheck: Boolean,
    var regCheck: Boolean,
    var finish: Boolean

)
class RegisterActivityApp : AppCompatActivity() {
    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 101
    }
    lateinit var call: Call<LoginCheckDTO>
    lateinit var service: Service
    lateinit var conf: Observable<ConfirmedDTO>
    private val disposables = CompositeDisposable()

    private val loginCheckedKaKao = LoginCheckedKaKao(
        nameCheck = false,
        nickCheck = false,
        birthdayCheck = false,
        regCheck = false,
        finish = false
    )
    var randomSixDigitNumber = "000000"
    lateinit var profile: ImageView

     var uriString: String? = ""
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
                            uriString = blob.mediaLink // 공개 URL 가져오기
                            // 필요한 UI 업데이트 작업
                        }
                    } catch (e: Exception) {
                        // 오류 처리
                        Log.e("RegisterActivity", "Image upload failed", e)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivityApp, "이미지 업로드 실패", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    private fun getStorageService(): Storage {
        val assetManager = this@RegisterActivityApp.assets
        val inputStream = assetManager.open("goyo-415004-d79f31fe39d5.json")
        val credentials = GoogleCredentials.fromStream(inputStream)
        return StorageOptions.newBuilder().setCredentials(credentials).build().service
    }
    private fun sendSMS(message: String) {
        // 텍스트 메시지 생성
        val templateId = 103256L // 카카오 개발자 콘솔에서 확인한 템플릿 ID

// User Argument에 해당하는 값을 전달하는 Map 생성
        val templateArgs = mapOf(
            "randomSixDigitNumber" to message // 동적으로 변경할 상품 설명
        )

        TalkApiClient.instance.sendCustomMemo(templateId, templateArgs) { error ->
            if (error != null) {
                Log.e(TAG, "메시지 보내기 실패", error)
            } else {
                Log.d("메세지","message + $templateId $templateArgs")
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_app)
        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()

        uriString = intent.getStringExtra("profile")

        val nick = intent.getStringExtra("nick")

        val name = "채널 이름"
        val descriptionText = "채널 설명"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("1", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val kakao_ok = findViewById<B>(R.id.kakao_ok)
        val kakaoConET = findViewById<EditText>(R.id.kakao_con_et)
        val kakaoKon = findViewById<B>(R.id.kakao_con)
        kakaoConET.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 사용자가 EditText에 다시 입력을 시작할 때
                kakaoConET.setTextColor(Color.BLACK) // 텍스트 색상을 검은색으로 변경
                kakaoConET.text.clear() // EditText의 텍스트를 지웁니다.
            }
        }
        kakaoConET.setOnClickListener {
            kakaoConET.setTextColor(Color.BLACK) // 텍스트 색상을 검은색으로 변경
            kakaoConET.text.clear() // EditText의 텍스트를 지웁니다.
        }
        kakao_ok.setOnClickListener {
            val random = Random.Default
            randomSixDigitNumber = random.nextInt(100000, 999999).toString() // 범위를 100000부터 999999까지로 지정하여 6자리 랜덤 숫자 생성
            sendSMS("인증번호는 $randomSixDigitNumber 입니다.")
            sendNotification(this, "카카오톡 -> 나와의 채팅에서 확인하실 수 있습니다.")
        }
        kakaoKon.setOnClickListener {
            if(kakaoConET.text.toString() == randomSixDigitNumber){
                kakaoConET.setTextColor(Color.GREEN)
                kakaoConET.setText("인증되었습니다!")
                kakaoConET.isEnabled = false
                Log.d("번호",randomSixDigitNumber  )
                Log.d("번호",randomSixDigitNumber  )
                kakaoKon.isEnabled = false
                loginCheckedKaKao.regCheck = true
            }
            else if(kakaoConET.text.toString() == "000000"){
                kakaoConET.setTextColor(Color.RED)
                kakaoConET.setText("인증 실패!")
                Log.d("번호",randomSixDigitNumber)
            }
            else{
                kakaoConET.setTextColor(Color.RED)
                kakaoConET.setText("인증 실패!")
                Log.d("번호",randomSixDigitNumber)
            }
        }

        val regName = findViewById<EditText>(R.id.regName)
        regName.setText(nick)

        profile = findViewById<ImageView>(R.id.regPro)

        if (uriString != null) {
            val profilePx = dpToPx(this, 90)
            Glide.with(this)
                .load(uriString)
                .override(profilePx,profilePx)
                .circleCrop()
                .into(profile)
        } else {
            Log.e("uri 에러","$uriString")
        }
        profile.setOnClickListener {
            val galleryPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            if (galleryPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                    STORAGE_PERMISSION_CODE
                )
            }
            else{
                openGallery()
            }
        }




        val regNick = findViewById<EditText>(R.id.regNick)
        var nickSpace = regNick.text.toString()
        val confirmNickBtn = findViewById<B>(R.id.confirm_nick)
        confirmNickBtn.setOnClickListener {
            nickSpace = regNick.text.toString()
            if(nickSpace.length in 2..6){
                conf = service.confirmNick(ConfirmDTO(nickSpace))
                //progressBar.visibility = View.VISIBLE
                disposables.add(
                    conf.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<ConfirmedDTO>(){
                            override fun onNext(t: ConfirmedDTO) {
                                val responseOK = t.ok
                                if(!responseOK){
                                    Log.d("서버로부터 받은 요청", "닉네임 : $responseOK")
                                    Toast.makeText(this@RegisterActivityApp, "중복확인 완료!", Toast.LENGTH_SHORT).show()
                                    loginCheckedKaKao.nickCheck = true
                                    regNick.isEnabled = false
                                    regNick.setTextColor(Color.GREEN)
                                }
                                else {
                                    Log.d("서버로부터 받은 요청", "닉네임 : $responseOK")
                                    Toast.makeText(this@RegisterActivityApp, "중복된 닉네임이 존재합니다!", Toast.LENGTH_SHORT).show()
                                    regNick.setText("")
                                }
                            }
                            override fun onError(e: Throwable) {
                                // onError
                                Log.d("RegisterActivity", "Showing ProgressBar")
                                //progressBar.visibility = View.GONE
                                Log.e("retrofit 연동", "실패", e)
                            }
                            override fun onComplete() {
                                // onComplete
                                Log.d("RegisterActivity", "Showing ProgressBar")
                                //progressBar.visibility = View.GONE
                            }
                        }

                        )
                )
            } else{
                Toast.makeText(this@RegisterActivityApp, "올바르지 않은 닉네임 형식입니다.", Toast.LENGTH_SHORT).show()

            }
        }


        val regBirth = findViewById<EditText>(R.id.regBirth)


        var nameSpace = regName.text.toString()

        var conSpace = kakaoConET.text.toString()
        var birthSpace = regBirth.text.toString()
        val birthdatePattern = "^\\d{8}$"
        val patternBD = Pattern.compile(birthdatePattern)

        var bd = ""
        val regNext = findViewById<TextView>(R.id.reg_next)
        regNext.setOnClickListener {
            nameSpace = regName.text.toString()
            nickSpace = regNick.text.toString()
            conSpace = kakaoConET.text.toString()
            birthSpace = regBirth.text.toString()

            try {
                val formattedDate = formatDate(birthSpace)
               bd = formattedDate
            }catch (e: StringIndexOutOfBoundsException) {
                // 예외 발생 시 사용자에게 토스트 메시지를 보여주고 LoginActivity로 이동
                Toast.makeText(this, "올바르지 않은 필드가 존재합니다.", Toast.LENGTH_SHORT).show()
            }
            if(loginCheckedKaKao.finish){
                val userDTO=UserDTO(
                    nameSpace,
                    bd,
                    "$nameSpace$birthSpace@kakao.com",
                    "$name$birthSpace",
                    nickSpace,
                    "000-0000-0000",
                    uriString.toString()
                )

                call = service.sendData(userDTO)
                call.clone().enqueue(object : Callback<LoginCheckDTO>{
                    override fun onResponse(
                        call : Call<LoginCheckDTO>,
                        response : Response<LoginCheckDTO>
                    ){
                        //progressBar.visibility = View.GONE
                        if(response.isSuccessful){
                            val intent = Intent(this@RegisterActivityApp,permissionActivity::class.java)
                            startActivity(intent)
                            showToast("회원가입에 성공하셨습니다!")
                            finish()
                        }
                        else{
                            showToast("서버 실패")
                        }
                    }

                    override fun onFailure(call: Call<LoginCheckDTO>, t: Throwable) {
                        TODO("Not yet implemented")
                        //progressBar.visibility = View.GONE

                    }

                }
                )
                this@RegisterActivityApp.finish()
            }else{
                if(nameSpace.length <= 5) {
                    loginCheckedKaKao.nameCheck = true
                }
                if (patternBD.matcher(birthSpace).matches()) {
                    loginCheckedKaKao.birthdayCheck = true
                }
                if (!loginCheckedKaKao.nameCheck) {
                    Toast.makeText(this, "이름을 확인해주세요${name}", Toast.LENGTH_SHORT).show()
                    regName.setText("")
                    //progressBar.visibility = View.GONE
                }
                else if (!loginCheckedKaKao.nickCheck) {
                    Toast.makeText(this, "닉네임을 확인해주세요", Toast.LENGTH_SHORT).show()
                    regNick.setText("")
                    //progressBar.visibility = View.GONE

                } else if (!loginCheckedKaKao.birthdayCheck) {
                    Toast.makeText(this, "생년월일을 확인해주세요", Toast.LENGTH_SHORT).show()
                    regBirth.setText("")
                    //progressBar.visibility = View.GONE
                }
                else if (!loginCheckedKaKao.regCheck) {
                    Toast.makeText(this, "카카오톡 인증을 확인해주세요", Toast.LENGTH_SHORT).show()
                    kakaoConET.setText("")
                    //progressBar.visibility = View.GONE
                }
                else {
                    loginCheckedKaKao.finish = true
                    //progressBar.visibility = View.GONE
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
    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
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


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun sendNotification(context: Context, message: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.kakao.talk")
        val notificationId = Random.nextInt()
        val pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder = NotificationCompat.Builder(context, "1")
            .setSmallIcon(R.drawable.invite_message) // 알림 아이콘으로 교체하세요
            .setContentTitle("인증번호가 도착하였습니다!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // 여기에 추가
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_POST_NOTIFICATIONS
            )
            return
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}