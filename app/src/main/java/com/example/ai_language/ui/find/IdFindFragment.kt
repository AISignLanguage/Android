package com.example.ai_language.ui.find

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ai_language.R
import com.example.ai_language.Util.RetrofitClient
import com.example.ai_language.data.remote.Service
import com.example.ai_language.domain.model.request.FindEmailRequestDTO
import com.example.ai_language.domain.model.request.GetIdDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IdFindFragment : Fragment() {
    private lateinit var call: Call<ResponseBody>
    private lateinit var service: Service
    private lateinit var getIdDTO: GetIdDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) // 키보드가 UI 가리지 않게
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_id_find, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Retrofit Instance 생성
        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()

        val findBtn = view.findViewById<TextView>(R.id.find_id_btn)
        findBtn.setOnClickListener {
            fetchDataFromServer()
        }

    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return "${phoneNumber.substring(0, 3)}-${
            phoneNumber.substring(
                3,
                7
            )
        }-${phoneNumber.substring(7, 11)}"

    }

    private fun fetchDataFromServer() {
        val nameEditText = view?.findViewById<EditText>(R.id.put_name)
        val phoneNumberEditText = view?.findViewById<EditText>(R.id.put_phoneNumber)
        val name = nameEditText!!.text.toString()
        val phoneNumber = phoneNumberEditText!!.text.toString()

        // FindIdDTO 객체 생성
        val findEmailRequestDTO = FindEmailRequestDTO(name, formatPhoneNumber(phoneNumber))
        call = service.findEmail(findEmailRequestDTO)!!

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()

                    val email = responseBody?.replace("Your email is: ", "")
                    Log.d("로그인", "이메일: $email")

                    if (email == null) {
                        Toast.makeText(requireContext(), "잘못된 정보입니다", Toast.LENGTH_SHORT).show()
                        nameEditText.text = (null)
                        phoneNumberEditText.text = (null)
                    } else {
                        val intent = Intent(requireActivity(), FindEmail::class.java)
                        Log.d("로그", "name : ${name}, email : ${email}")
                        intent.putExtra("name", name)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        nameEditText.text = null
                        phoneNumberEditText.text = (null)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("로그", "아이디 찾기 서버 연결 실패")
            }
        })
    }

}