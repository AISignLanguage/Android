package com.example.ai_language.ui.qrcode

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.preferencesOf
import com.example.ai_language.R
import com.google.zxing.qrcode.QRCodeWriter

class QrCodeActivity : AppCompatActivity() {

    private val predefinedUrl = "https://www.google.co.kr/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        val qrCodeImageView: ImageView = findViewById(R.id.qrCodeImageView)
        val generateButton: Button = findViewById(R.id.generateButton)

        generateButton.setOnClickListener{
            val bitmap: Bitmap? = QRCodeUtil.generateQRCode(predefinedUrl, 500)
            qrCodeImageView.setImageBitmap(bitmap)
        }

    }
}