package com.example.ai_language.ui.qrcode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter

object QRCodeUtil {
    fun generateQRCode(text: String, size: Int): Bitmap? {

        return try {
            val bitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if(bitMatrix.get(x,y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt() )
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }

    }
}
