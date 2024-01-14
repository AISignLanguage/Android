package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.widget.ImageButton
import android.widget.TextView

class MyPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        val home = findViewById<ImageButton>(R.id.homeButton3)
        home.setOnClickListener{
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }


        val my_inform_edit = findViewById<TextView>(R.id.my_inform_edit)
        my_inform_edit.setOnClickListener {
            val intent = Intent(this,PersonalInfo::class.java)
            startActivity(intent)
        }


        val withDrawer = findViewById<TextView>(R.id.withdrawal)
        withDrawer.setOnClickListener {
            val intent = Intent(this,Unregister::class.java)
            startActivity(intent)
        }

        val sign_out = findViewById<TextView>(R.id.sign_out)
        sign_out.setOnClickListener {
            val dialog = SignoutDialog(this)
            dialog.exDialog()

            dialog.setOnClickedListener(object : SignoutDialog.ButtonClickListener{
                override fun onClicked(result: String) {
                    when(result){
                        "ok"->{
                            val intent = Intent(this@MyPage,LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        "no"->{

                        }
                    }
                }
            })
        }

    }

}