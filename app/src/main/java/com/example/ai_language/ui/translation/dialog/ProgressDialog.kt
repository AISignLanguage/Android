package com.example.ai_language.ui.translation.dialog

import android.app.AlertDialog
import android.content.Context
import com.example.ai_language.R

class ProgressDialog(context: Context) {

    private val dialog: AlertDialog

    init {
        val builder = AlertDialog.Builder(context)
        val inflater = builder.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater
        val view = inflater.inflate(R.layout.dialog_progress, null)

        builder.setView(view)
        builder.setCancelable(true) // 사용자가 다이얼로그를 취소할 수 없게 설정

        dialog = builder.create()
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}