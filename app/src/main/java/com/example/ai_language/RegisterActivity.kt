package com.example.ai_language
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.SmsManager

import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlin.random.Random
import android.Manifest
import android.content.ContentValues.TAG
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.user.model.User
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
import retrofit2.Retrofit
import java.io.IOException

data class UserDTO(
    @SerializedName("username")
    val username: String,

    @SerializedName("usernick")
    val usernick: String
)

data class ResponseDTO(
    @SerializedName("logIn_ok")
    val response: Boolean
)



class RegisterActivity : AppCompatActivity() {
    lateinit var call : Call<ResponseDTO>
    lateinit var service: Service
    private val STORAGE_PERMISSION_CODE = 1
    private val SMS_PERMISSION_CODE = 2
    private val ACCOUNT_SID = "${R.string.SID}"
    private val AUTH_TOKEN = "${R.string.TK}"
    private val MESSAGIING_SERVICE = "${R.string.MS}"

    lateinit var profile: ImageView
    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val selectedImageUri: Uri? = intent?.data
                profile.setImageURI(selectedImageUri)
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
                val base64EncodedCredentials = "Basic " + Base64.encodeToString("$ACCOUNT_SID:$AUTH_TOKEN".toByteArray(), Base64.NO_WRAP)

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val send_certification_et = findViewById<EditText>(R.id.send_certification_et)
        val send_certification_btn = findViewById<Button>(R.id.send_certification_btn)
        val certification_et= findViewById<EditText>(R.id.certification_et)
        val certification_btn = findViewById<Button>(R.id.certification_btn)

        send_certification_btn.setOnClickListener {
            val phNum = send_certification_et.text.toString()
            if (!isValidPhoneNumber(phNum)) {
                // 전화번호가 "010"으로 시작하지 않거나 11자리가 아닌 경우
                Toast.makeText(this, "올바른 전화번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val random = Random.Default
                val randomSixDigitNumber: Int = random.nextInt(100000, 999999) // 범위를 100000부터 999999까지로 지정하여 6자리 랜덤 숫자 생성
                val num = convertPhoneNumber(phNum) // 폰넘버
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    //sendSMS(num,"[손짓의 순간] 인증번호는 ${randomSixDigitNumber} 입니다.") -> 실제비용청구되므로 연습할땐 아래코드로
                    sendSMS2(num,"[손짓의 순간] 인증번호는 ${randomSixDigitNumber} 입니다.")


                    Log.d("왔",num)
                }
                else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.SEND_SMS),
                        SMS_PERMISSION_CODE // 권한 요청 코드를 정의해야 함
                    )
                }
            }
        }
        val nick = intent.getStringExtra("nick")
        val regNick = findViewById<EditText>(R.id.reg_nick)
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

        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()



        val regNext = findViewById<TextView>(R.id.reg_next)
        regNext.setOnClickListener {


            val userDTO = UserDTO(regName.text.toString(),regNick.text.toString())
            val gson = Gson()
            val userInfo = gson.toJson(userDTO)
            Log.e("JSON", userInfo);


            call = service.sendData(userDTO)
            Log.d("콜","${userDTO}")

            call.clone().enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    if (response.isSuccessful) {
                        Log.d("서버로부터 받은 요청","${response.body()?.response}")
                        Toast.makeText(this@RegisterActivity, "회원가입에 성공하셨습니다!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity,permissionActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Log.d(TAG,"실패ㅅ")
                    }
                }
                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {

                    Log.e("retrofit 연동", "실패", t)
                }

            }
            )

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