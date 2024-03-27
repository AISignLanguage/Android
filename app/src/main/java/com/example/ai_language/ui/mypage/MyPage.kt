package com.example.ai_language.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_language.ui.account.change.PersonalInfo
import com.example.ai_language.R
import com.example.ai_language.ui.account.SignoutDialog
import com.example.ai_language.ui.account.Unregister
import com.example.ai_language.Util.EncryptedSharedPreferencesManager
import com.example.ai_language.ui.account.LoginActivity

class MyPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        //val home = findViewById<ImageButton>(R.id.homeButton3)
//        home.setOnClickListener{
//            val intent = Intent(this,Home::class.java)
//            startActivity(intent)
//            finish()
//        }

        val my_inform_edit = findViewById<TextView>(R.id.my_inform_edit_btn)
        my_inform_edit.setOnClickListener {
            val intent = Intent(this, PersonalInfo::class.java)
            startActivity(intent)
        }

        val withDrawer = findViewById<TextView>(R.id.withdrawal_btn)
        withDrawer.setOnClickListener {
            val intent = Intent(this, Unregister::class.java)
            startActivity(intent)
        }

        val faq = findViewById<TextView>(R.id.FAQ_btn) //자주 묻는 질문
        faq.setOnClickListener {
            val intent = Intent(this, FaqPage::class.java)
            startActivity(intent)
        }

        val version = findViewById<TextView>(R.id.versionCheck) //버전 확인
        version.setOnClickListener {
            val intent = Intent(this, VersionCheck::class.java)
            startActivity(intent)
        }

        val term = findViewById<TextView>(R.id.terms_btn)
        term.setOnClickListener {
            val intent = Intent(this, TermsActivity::class.java)
            startActivity(intent)
        }

        val sign_out = findViewById<TextView>(R.id.sign_out_btn)
        sign_out.setOnClickListener {
            val dialog = SignoutDialog(this)
            dialog.exDialog()

            dialog.setOnClickedListener(object : SignoutDialog.ButtonClickListener {
                override fun onClicked(result: String) {
                    when (result) {
                        "ok" -> {
                            disableAutoLogin() //로그아웃 하면 자동 로그인 해제됨
                            val intent = Intent(this@MyPage, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        "no" -> {

                        }
                    }
                }
            })
        }

    }

    // 자동 로그인 해제 (EncryptedSharedPreferences에서 정보 삭제)
    fun disableAutoLogin() {
        val sharedPreferencesManager = EncryptedSharedPreferencesManager(this)
        sharedPreferencesManager.clearPreferences()
    }

}