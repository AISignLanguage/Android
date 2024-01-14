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
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()

            //선택된 체크 박스 약관 번호 저장
            isCheck();
        }

        termsAllButton = findViewById<CheckBox>(R.id.termsAllButton)
        termsAllButton.setOnClickListener { onCheckChanged(termsAllButton) }

        termsButton1 = findViewById<CheckBox>(R.id.termsButton1)
        termsButton1.setOnClickListener { onCheckChanged(termsButton1) }

        termsButton2 = findViewById<CheckBox>(R.id.termsButton2)
        termsButton2.setOnClickListener { onCheckChanged(termsButton2) }

        termsButton3 = findViewById<CheckBox>(R.id.termsButton3)
        termsButton3.setOnClickListener { onCheckChanged(termsButton3) }

        termsButton4 = findViewById<CheckBox>(R.id.termsButton4)
        termsButton4.setOnClickListener { onCheckChanged(termsButton4) }

        termsButton5 = findViewById<CheckBox>(R.id.termsButton5)
        termsButton5.setOnClickListener { onCheckChanged(termsButton5) }

        termsButton6 = findViewById<CheckBox>(R.id.termsButton6)
        termsButton6.setOnClickListener { onCheckChanged(termsButton6) }

        termsButton7 = findViewById<CheckBox>(R.id.termsButton7)
        termsButton7.setOnClickListener { onCheckChanged(termsButton7) }


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

    private fun onCheckChanged(checkBox: CheckBox) {
        when(checkBox.id){
            R.id.termsAllButton -> {
                if(termsAllButton.isChecked){
                    termsButton1.isChecked = true
                    termsButton2.isChecked = true
                    termsButton3.isChecked = true
                    termsButton4.isChecked = true
                    termsButton5.isChecked = true
                    termsButton6.isChecked = true
                    termsButton7.isChecked = true
                }
                else{
                    termsButton1.isChecked = false
                    termsButton2.isChecked = false
                    termsButton3.isChecked = false
                    termsButton4.isChecked = false
                    termsButton5.isChecked = false
                    termsButton6.isChecked = false
                    termsButton7.isChecked = false
                }
            }
            else -> {
                termsAllButton.isChecked = (termsButton1.isChecked && termsButton2.isChecked &&
                        termsButton3.isChecked && termsButton4.isChecked && termsButton5.isChecked &&
                        termsButton6.isChecked && termsButton7.isChecked)
            }
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

    private fun isCheck(){
        val isCheckedAll: Boolean = termsAllButton.isChecked
        val isChecked1: Boolean = termsButton1.isChecked
        val isChecked2: Boolean = termsButton2.isChecked
        val isChecked3: Boolean = termsButton3.isChecked
        val isChecked4: Boolean = termsButton4.isChecked
        val isChecked5: Boolean = termsButton5.isChecked
        val isChecked6: Boolean = termsButton6.isChecked
        val isChecked7: Boolean = termsButton7.isChecked

        val logMessage = buildString {
            if (isCheckedAll) append("CheckBox All checked")
            else{
                if (isChecked1) append("CheckBox1 is checked, ")
                if (isChecked2) append("CheckBox2 is checked, ")
                if (isChecked3) append("CheckBox3 is checked, ")
                if (isChecked4) append("CheckBox4 is checked, ")
                if (isChecked5) append("CheckBox5 is checked, ")
                if (isChecked6) append("CheckBox6 is checked, ")
                if (isChecked7) append("CheckBox7 is checked, ")
            }


            if (isNotEmpty()) delete(length - 2, length)
        }

        Log.d("CheckBoxLog", logMessage)
    }


}