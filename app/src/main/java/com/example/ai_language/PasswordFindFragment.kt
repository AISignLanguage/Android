package com.example.ai_language

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordFindFragment : Fragment() {
    private lateinit var call: Call<FindPwdOk>
    private lateinit var service: Service
    private lateinit var findPwdOk: FindPwdOk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_find, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Retrofit Instance 생성
        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()

        val certBtn = view.findViewById<Button>(R.id.cert_btn) //전송, 확인 버튼
        certBtn.setOnClickListener {
            fetchDataFromServer()
            certBtn.setBackgroundResource(R.drawable.confrim_num)
        }

    }

    private fun fetchDataFromServer() {
        val nameEditText = view?.findViewById<EditText>(R.id.put_name2)
        val emailEditText = view?.findViewById<EditText>(R.id.put_email)
        val name = nameEditText!!.text.toString()
        val email = emailEditText!!.text.toString()

        val findPwdDTO = FindPwdDTO(name, email)
        call = service.findPwd(findPwdDTO)

        call.enqueue(object : Callback<FindPwdOk> {
            override fun onResponse(call: Call<FindPwdOk>, response: Response<FindPwdOk>) {
                if (response.isSuccessful) {
                    findPwdOk = response.body()!!
                    val isOk = findPwdOk.success_find_pwd
                    Toast.makeText(requireContext(), "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show()

                    if (isOk) {
                        //이메일 인증
                    }
                }
            }

            override fun onFailure(call: Call<FindPwdOk>, t: Throwable) {
                Log.d("로그", "서버 연결 실패 (PasswordFindFragment)")
            }
        })

    }

}