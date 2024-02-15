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
import androidx.annotation.RequiresApi

class RegisterActivityApp : AppCompatActivity() {
    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 101
    }
    var randomSixDigitNumber = "000000"
    lateinit var profile: ImageView
    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val selectedImageUri: Uri? = intent?.data
                profile.setImageURI(selectedImageUri)
            }
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

        val regName = findViewById<EditText>(R.id.reg_name)
        regName.setText(nick)

        profile = findViewById<ImageView>(R.id.reg_pro)
        val uriString: String? = intent.getStringExtra("profile")
        if (uriString != null) {
                val profilePx = dpToPx(this, 90)
                Glide.with(this)
                    .load(uriString)
                    .override(profilePx,profilePx)
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
        val regNext = findViewById<TextView>(R.id.reg_next)
        regNext.setOnClickListener {
            val intent = Intent(this,permissionActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "회원가입에 성공하셨습니다!", Toast.LENGTH_SHORT).show()
            finish()
        }
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