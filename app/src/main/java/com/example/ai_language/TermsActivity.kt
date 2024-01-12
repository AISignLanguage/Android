package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            val intent = Intent(this, home::class.java)
            startActivity(intent)
        }

        termsAllButton = findViewById<CheckBox>(R.id.termsAllButton)
        termsButton1 = findViewById<CheckBox>(R.id.termsButton1)
        termsButton2 = findViewById<CheckBox>(R.id.termsButton2)
        termsButton3 = findViewById<CheckBox>(R.id.termsButton3)
        termsButton4 = findViewById<CheckBox>(R.id.termsButton4)
        termsButton5 = findViewById<CheckBox>(R.id.termsButton5)
        termsButton6 = findViewById<CheckBox>(R.id.termsButton6)
        termsButton7 = findViewById<CheckBox>(R.id.termsButton7)

        termsAllButton.setOnClickListener { onCheckChanged(termsAllButton) }
        termsButton1.setOnClickListener { onCheckChanged(termsButton1) }
        termsButton2.setOnClickListener { onCheckChanged(termsButton2) }
        termsButton3.setOnClickListener { onCheckChanged(termsButton3) }
        termsButton4.setOnClickListener { onCheckChanged(termsButton4) }
        termsButton5.setOnClickListener { onCheckChanged(termsButton5) }
        termsButton6.setOnClickListener { onCheckChanged(termsButton6) }
        termsButton7.setOnClickListener { onCheckChanged(termsButton7) }


        val termsDetail1 = findViewById<Button>(R.id.viewButton1)
        val termsDetail2 = findViewById<Button>(R.id.viewButton2)
        val termsDetail3 = findViewById<Button>(R.id.viewButton3)
        val termsDetail4 = findViewById<Button>(R.id.viewButton4)
        val termsDetail5 = findViewById<Button>(R.id.viewButton5)
        val termsDetail6 = findViewById<Button>(R.id.viewButton6)
        val termsDetail7 = findViewById<Button>(R.id.viewButton7)

        termsDetail1.setOnClickListener{
            showTermsDialog("[필수] 서비스 이용 권리", R.string.terms_detail_1)
        }
        termsDetail2.setOnClickListener{
            showTermsDialog("[필수] 신원 정보 제공", R.string.terms_detail_1)
        }
        termsDetail3.setOnClickListener{
            showTermsDialog("[필수] 약관 게시", R.string.terms_detail_1)
        }
        termsDetail4.setOnClickListener{
            showTermsDialog("[필수] 약관 개정", R.string.terms_detail_1)
        }
        termsDetail5.setOnClickListener{
            showTermsDialog("[필수] 회원정보 변경", R.string.terms_detail_1)
        }
        termsDetail6.setOnClickListener{
            showTermsDialog("[필수] 회원정보 관리 의무", R.string.terms_detail_1)
        }
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
    
    
}