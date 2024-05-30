package com.example.ai_language.ui.account

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import com.example.ai_language.R

class SignoutDialog(context: Context) {
    private val dialog = Dialog(context)
    fun exDialog() {


        dialog.setContentView(R.layout.sign_out_dialog)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        val ok_btn = dialog.findViewById<Button>(R.id.sign_out_ok)
        val no_btn = dialog.findViewById<Button>(R.id.sign_out_no)

        ok_btn.setOnClickListener {
            onClickListener.onClicked("ok")
            dialog.dismiss()
        }

        no_btn.setOnClickListener {
            onClickListener.onClicked("no")
            dialog.dismiss()
        }

        dialog.show()
    }

    interface ButtonClickListener {
        fun onClicked(result: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }

}