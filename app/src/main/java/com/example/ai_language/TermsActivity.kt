package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog

class TermsActivity : AppCompatActivity() {

    private lateinit var termsAllButton: CheckBox
    private lateinit var termsButton1: CheckBox
    private lateinit var termsButton2: CheckBox
    private lateinit var termsButton3: CheckBox
    private lateinit var termsButton4: CheckBox
    private lateinit var termsButton5: CheckBox
    private lateinit var termsButton6: CheckBox
    private lateinit var termsButton7: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val submit = findViewById<Button>(R.id.submitButton)

        submit.setOnClickListener{
            val intent = Intent(this, MyPage::class.java)
            startActivity(intent)
            finish()
        }

        val termsDetail1 = findViewById<Button>(R.id.viewButton1)
        termsDetail1.setOnClickListener{
            showTermsDialog("[필수] 서비스 이용 권리", R.string.terms_detail_1)
        }

        val termsDetail2 = findViewById<Button>(R.id.viewButton2)
        termsDetail2.setOnClickListener{
            showTermsDialog("[필수] 신원 정보 제공", R.string.terms_detail_1)
        }

        val termsDetail3 = findViewById<Button>(R.id.viewButton3)
        termsDetail3.setOnClickListener{
            showTermsDialog("[필수] 약관 게시", R.string.terms_detail_1)
        }

        val termsDetail4 = findViewById<Button>(R.id.viewButton4)
        termsDetail4.setOnClickListener{
            showTermsDialog("[필수] 약관 개정", R.string.terms_detail_1)
        }

        val termsDetail5 = findViewById<Button>(R.id.viewButton5)
        termsDetail5.setOnClickListener{
            showTermsDialog("[필수] 회원정보 변경", R.string.terms_detail_1)
        }

        val termsDetail6 = findViewById<Button>(R.id.viewButton6)
        termsDetail6.setOnClickListener{
            showTermsDialog("[필수] 회원정보 관리 의무", R.string.terms_detail_1)
        }

        val termsDetail7 = findViewById<Button>(R.id.viewButton7)
        termsDetail7.setOnClickListener{
            showTermsDialog("[필수] 우리의 의무", R.string.terms_detail_1)
        }
    }



    private fun showTermsDialog(title: String, contentResId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(getString(contentResId))
        builder.setPositiveButton("확인") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

}