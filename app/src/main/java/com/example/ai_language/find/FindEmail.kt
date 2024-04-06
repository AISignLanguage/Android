package com.example.ai_language.find

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_language.ui.account.MainLoginActivity
import com.example.ai_language.R
import java.util.regex.Pattern

class FindEmail : AppCompatActivity() {
    private lateinit var intent: Intent
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var findPwdBtn: Button
    private lateinit var checkButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_email)

        showId()
        btnClick()
    }

    private fun btnClick() {
        findPwdBtn = findViewById(R.id.findPw)
        findPwdBtn.setOnClickListener {
            val intent = Intent(this, FindIdPwd::class.java)
            intent.putExtra("fromFindEmail", true)
            startActivity(intent)
        }
        checkButton = findViewById(R.id.checkButton)
        checkButton.setOnClickListener {
            val intent = Intent(this, MainLoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showId() {
        intent = getIntent()
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)

        name.text = intent.getStringExtra("name").toString()
        val userEmail = intent.getStringExtra("email").toString()
        email.text = emailMasking(userEmail)
    }

    private fun emailMasking(email: String): String {
        val result = StringBuffer()
        val REGEX_EMAIL =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(REGEX_EMAIL, Pattern.CASE_INSENSITIVE)

        if (!pattern.matcher(email).matches()) {
            return email
        }

        val split = email.split("@")
        if (split.size == 2) { // id와 도메인 나눠진 경우
            if (split[0].length > 3) {
                result.append(split[0].substring(0, 3))
            } else {
                result.append(split[0])
            }
            result.append("***")
                .append("@")
                .append(split[1])
        }
        Log.d("로그", "${result.toString()}")
        return result.toString()
    }

}