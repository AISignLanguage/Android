package com.example.ai_language.ui.account.change

import android.app.Dialog
import android.content.Context
import android.widget.Button
import com.example.ai_language.R

class ChangePwDialog(context: Context) {
    private val dialog = Dialog(context)

    private lateinit var onDismissListener: DialogDismissListener

    interface DialogDismissListener {
        fun onDialogDismiss()
    }

    fun dialogShow() {
        dialog.setContentView(R.layout.activity_change_pw_dialog)
        dialog.setCancelable(false)
        dialog.show()

        val checkButton = dialog.findViewById<Button>(R.id.checkButton)

        checkButton.setOnClickListener {
            dialog.dismiss()
            onDismissListener.onDialogDismiss()
        }
    }

    fun setOnDismissListener(listener: DialogDismissListener) {
        onDismissListener = listener
    }

}