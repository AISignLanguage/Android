package com.example.ai_language

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val terms1 = findViewById<Button>(R.id.viewButton1)
        val terms2 = findViewById<Button>(R.id.viewButton2)
        val terms3 = findViewById<Button>(R.id.viewButton3)
        val terms4 = findViewById<Button>(R.id.viewButton4)
        val terms5 = findViewById<Button>(R.id.viewButton5)
        val terms6 = findViewById<Button>(R.id.viewButton6)
        val terms7 = findViewById<Button>(R.id.viewButton7)

        terms1.setOnClickListener{
            showTermsDialog("[필수] 서비스 이용 권리", R.string.terms_detail_1)
        }
        terms2.setOnClickListener{
            showTermsDialog("[필수] 신원 정보 제공", R.string.terms_detail_1)
        }
        terms3.setOnClickListener{
            showTermsDialog("[필수] 약관 게시", R.string.terms_detail_1)
        }
        terms4.setOnClickListener{
            showTermsDialog("[필수] 약관 개정", R.string.terms_detail_1)
        }
        terms5.setOnClickListener{
            showTermsDialog("[필수] 회원정보 변경", R.string.terms_detail_1)
        }
        terms6.setOnClickListener{
            showTermsDialog("[필수] 회원정보 관리 의무", R.string.terms_detail_1)
        }
        terms7.setOnClickListener{
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