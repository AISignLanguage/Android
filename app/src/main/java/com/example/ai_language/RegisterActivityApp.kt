package com.example.ai_language
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

class RegisterActivityApp : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE = 1

    lateinit var profile: ImageView
    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val selectedImageUri: Uri? = intent?.data
                profile.setImageURI(selectedImageUri)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_app)

        val nick = intent.getStringExtra("nick")

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
}