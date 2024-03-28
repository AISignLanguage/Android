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
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.ActivityMyPageBinding
import com.example.ai_language.ui.account.LoginActivity

class MyPage :BaseFragment<ActivityMyPageBinding>(R.layout.activity_my_page) {
    override fun setLayout() {

        // val home = findViewById<ImageButton>(R.id.homeButton3)
//        home.setOnClickListener{
//            val intent = Intent(this,Home::class.java)
//            startActivity(intent)
//            finish()
//        }

        with(binding) {
            myInformEdit.setOnClickListener {
                val intent = Intent(requireContext(), PersonalInfo::class.java)
                startActivity(intent)
            }


            withdrawal.setOnClickListener {
                val intent = Intent(requireContext(), Unregister::class.java)
                startActivity(intent)
            }


            FAQBtn.setOnClickListener {
                val intent = Intent(requireContext(), FaqPage::class.java)
                startActivity(intent)
            }

            versionCheck.setOnClickListener {
                val intent = Intent(requireContext(), VersionCheck::class.java)
                startActivity(intent)
            }


            termsBtn.setOnClickListener {
                val intent = Intent(requireContext(), TermsActivity::class.java)
                startActivity(intent)
            }

            signOutBtn.setOnClickListener {
                val dialog = SignoutDialog(requireContext())
                dialog.exDialog()

                dialog.setOnClickedListener(object : SignoutDialog.ButtonClickListener {
                    override fun onClicked(result: String) {
                        when (result) {
                            "ok" -> {
                                disableAutoLogin() //로그아웃 하면 자동 로그인 해제됨
                                val intent = Intent(requireContext(), LoginActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }

                            "no" -> {

                            }
                        }
                    }
                })
            }

        }

        // 자동 로그인 해제 (EncryptedSharedPreferences에서 정보 삭제)


    }
    fun disableAutoLogin() {
        val sharedPreferencesManager = EncryptedSharedPreferencesManager(requireContext())
        sharedPreferencesManager.clearPreferences()
    }
}