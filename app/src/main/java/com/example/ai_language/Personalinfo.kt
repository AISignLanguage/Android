package com.example.ai_language
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PersonalInfo : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 1

    lateinit var progressBar: ProgressBar

    private lateinit var encryptedSharedPreferencesManager: EncryptedSharedPreferencesManager
    private lateinit var service: Service
    private lateinit var call: Call<GetProfileDTO>
    private lateinit var getProfileDTO: GetProfileDTO

    lateinit var profileImage: ImageButton

    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val selectedImageUri: Uri? = intent?.data
                if(selectedImageUri != null)
                    loadImage(selectedImageUri)
            }
            else{
                progressBar.visibility = ProgressBar.GONE
            }
        }

    private fun sendRequestProfile() {

        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()

        encryptedSharedPreferencesManager = EncryptedSharedPreferencesManager(this)
        val userEmail = encryptedSharedPreferencesManager.getUserEmail()

        val email = findViewById<TextView>(R.id.user_email)
        email.text = userEmail

        val url = findViewById<ImageButton>(R.id.user_profile_image)
        val name = findViewById<TextView>(R.id.user_name)
        val nickName = findViewById<TextView>(R.id.user_nick_name)
        val password = findViewById<TextView>(R.id.user_password)
        val birthdate = findViewById<TextView>(R.id.user_birthdate)
        val phoneNumber = findViewById<TextView>(R.id.user_phone_number)

        val profileRequestDTO = ProfileRequestDTO(email.text.toString())
        call = service.requestProfile(profileRequestDTO)

        call.enqueue(object : Callback<GetProfileDTO>{

            override fun onResponse(call: Call<GetProfileDTO>, response: Response<GetProfileDTO>) {

                if (response.isSuccessful) {
                    getProfileDTO = response.body()!!

                    val imageUrl = getProfileDTO?.url.let { Uri.parse(it) }
                    loadImage(imageUrl)

                    name.text = getProfileDTO?.name
                    nickName.text = getProfileDTO?.nickName
                    password.text = getProfileDTO?.password
                    birthdate.text = getProfileDTO?.birthdate
                    phoneNumber.text = getProfileDTO?.phoneNumber
                    Log.d("로그", "${imageUrl}, ${nickName.text}, ${password.text}, ${birthdate.text}, ${phoneNumber.text}")
                } else {
                    Log.d("로그", "false")
                }
            }

            override fun onFailure(call: Call<GetProfileDTO>, t: Throwable) {
                Log.d("로그", "PersonalInfo 서버 연결 실패")
            }
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info)

        sendRequestProfile()

        val homeButton2 = findViewById<ImageButton>(R.id.homeButton2)
        homeButton2.setOnClickListener{
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }

        val editDate = findViewById<TextView>(R.id.user_password)
        editDate.setOnClickListener{
            val intent = Intent(this,ChangePw::class.java)
            startActivity(intent)
            finish()
        }

        profileImage = findViewById(R.id.user_profile_image)
        profileImage.setOnClickListener {
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
        progressBar = findViewById(R.id.progressBar)

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        intent.action = Intent.ACTION_PICK
        progressBar.visibility = ProgressBar.VISIBLE

        Observable.fromCallable {
            galleryLauncher.launch(intent)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
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

    //RxJava
    private fun loadImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = ProgressBar.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = ProgressBar.GONE
                    return false
                }

            })
            .into(profileImage)
    }


}