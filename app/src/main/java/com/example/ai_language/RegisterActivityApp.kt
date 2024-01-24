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
class RegisterActivityApp : AppCompatActivity() {
    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001 // 이 부분을 추가하세요
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_app)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }



        val nick = intent.getStringExtra("nick")



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
            /*sendNotification(this, "인증번호가 발송되었습니다.\n 카카오톡 -> 나와의 채팅에서 확인하실 수 있습니다.") // 알림 전송 호출*/
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
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인된 경우, 알림을 보낼 수 있습니다.
            } else {
                // 권한이 거부된 경우, 사용자에게 알림을 보낼 수 없음을 알려야 합니다.
            }
        }
    }



    /*private fun sendNotification(context: Context, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MyChannelName"
            val descriptionText = "MyChannelDescription"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("MY_CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "MY_CHANNEL_ID")
            .setContentTitle("[손짓의 순간 인증번호가 도착하였습니다.]")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(0, builder.build())
    }

*/

}