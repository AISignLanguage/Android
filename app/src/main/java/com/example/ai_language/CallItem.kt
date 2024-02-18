package com.example.ai_language

import android.app.Activity
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.widget.LinearLayout

class CallItem : AppCompatActivity() {
    var density = 0.0f
    var standardSize_X = 0
    var standardSize_Y = 0

    fun getScreenSize(activity: Activity): Point {
        val metrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
            val bounds = windowMetrics.bounds
            Point(bounds.width() - insets.left - insets.right, bounds.height() - insets.top - insets.bottom)
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
        return metrics
    }
    fun getStandardSize() {
        val ScreenSize = getScreenSize(this)
        density = resources.displayMetrics.density
        standardSize_X = (ScreenSize.x / density).toInt()
        standardSize_Y = (ScreenSize.y / density).toInt()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_item)
        getStandardSize()  // 이 함수가 standardSize_X를 초기화한다고 가정

        val layout = findViewById<LinearLayout>(R.id.clt_manager)
        val params = layout.layoutParams
        params.width = standardSize_X  // 여기에 원하는 너비를 설정
        layout.layoutParams = params
    }

}