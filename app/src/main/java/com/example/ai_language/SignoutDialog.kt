package com.example.ai_language

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.LinearLayout
import android.widget.TextView

class SignoutDialog(context: Context){
    private val dialog = Dialog(context)
    fun exDialog(){
        dialog.setContentView(R.layout.sign_out_dialog)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)


        dialog.show()
    }


}