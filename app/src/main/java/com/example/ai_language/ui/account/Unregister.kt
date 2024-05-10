package com.example.ai_language.ui.account

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_language.R
import com.example.ai_language.Util.EncryptedSharedPreferencesManager
import com.example.ai_language.Util.RetrofitClient
import com.example.ai_language.domain.model.request.DeleteUserRequestDTO
import com.example.ai_language.domain.model.request.DeleteUserResponseDTO
import com.example.ai_language.ui.home.Home
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Unregister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unregister)

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        val items = resources.getStringArray(R.array.unregister)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val editText = findViewById<EditText>(R.id.editText)

        val adapter = object :
            ArrayAdapter<String>(this, R.layout.spinner_item_layout, items.toMutableList()) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                val editText = findViewById<EditText>(R.id.editText)
                if (position == 0) {
                    val textView = v.findViewById<TextView>(R.id.tvItemSpinner)
                    textView.text = ""
                    textView.hint = getItem(0)
                }
                return v
            }

            override fun getCount(): Int {
                return super.getCount()
            }
        }

        spinner.adapter = adapter

        val unregisterButton = findViewById<Button>(R.id.unregisterButton)
        unregisterButton.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = items[position]
                Log.d("Selected Item", selectedItem)
                if (position == 0) {
                    unregisterButton.isEnabled = false
                    unregisterButton.setBackgroundResource(R.drawable.unregister_btn2)
                } else {
                    unregisterButton.isEnabled = true
                    unregisterButton.setBackgroundResource(R.drawable.unregister_btn)
                    if (position == 5) {
                        editText.visibility = View.VISIBLE
                    } else {
                        editText.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        unregisterButton.setOnClickListener {
            if (editText.visibility == View.VISIBLE && editText.text.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "사유를 입력해 주세요", Toast.LENGTH_SHORT).show()
            }

            if (editText.visibility == View.GONE || (editText.visibility == View.VISIBLE && editText.text.isNotEmpty())) {
                userDelete()
                val unregisterDialog = UnregisterDialog(this)
                unregisterDialog.show()

                Handler().postDelayed({
                    unregisterDialog.dismiss()
                    navigateToHomeActivity()
                }, 3000)
            }
            disableAutoLogin() // 자동 로그인 해제
        }

    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    //로그인 이메일 불러오기
    private fun getSavedEmail(): String? {
        val sharedPreferencesManager = EncryptedSharedPreferencesManager(this)
        return sharedPreferencesManager.getUserEmail()
    }

    //유저 정보 DB 삭제
    private fun userDelete() {
        val userEmail = getSavedEmail()
        if (userEmail.isNullOrEmpty()) {
            Log.d("로그", "저장된 이메일이 없습니다")
            return
        }

        RetrofitClient.getInstance()
        val service = RetrofitClient.getUserRetrofitInterface()

        val call = service.deleteUser()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val deleteUserResponse = response.body()
                    if (deleteUserResponse != null && deleteUserResponse.success) {
                        Log.d("로그", "사용자 삭제 성공")
                    } else {
                        Log.d("로그", "사용자 삭제 실패")
                    }
                } else {
                    Log.d("로그", "서버 응답 실패")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("로그", "서버 요청 실패")
            }

        })

    }

    // 자동 로그인 해제 (EncryptedSharedPreferences에서 정보 삭제)
    fun disableAutoLogin() {
        val sharedPreferencesManager = EncryptedSharedPreferencesManager(this)
        sharedPreferencesManager.clearPreferences()
    }


}