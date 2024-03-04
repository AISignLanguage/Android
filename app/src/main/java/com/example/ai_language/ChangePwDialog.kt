package com.example.ai_language

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity

class ChangePwDialog (context: Context) {
    private val dialog = Dialog(context)

    private lateinit var onDismissListener: DialogDismissListener
    interface DialogDismissListener {
        fun onDialogDismiss()
    }

    fun dialogShow(){
        dialog.setContentView(R.layout.activity_change_pw_dialog)
        dialog.setCancelable(false)
        dialog.show()

        val checkButton = dialog.findViewById<Button>(R.id.checkButton)

        checkButton.setOnClickListener{
            dialog.dismiss()
            onDismissListener.onDialogDismiss()
        }
    }

    fun setOnDismissListener(listener: DialogDismissListener) {
        onDismissListener = listener
    }

}