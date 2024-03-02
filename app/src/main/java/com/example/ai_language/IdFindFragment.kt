package com.example.ai_language

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IdFindFragment : Fragment() {
    private lateinit var call : Call<GetIdDTO>
    private lateinit var service: Service
    private lateinit var getIdDTO: GetIdDTO
    //private lateinit var findIdDTO: FindIdDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_id_find, container, false)
        return rootView

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

    private fun fetchDataFromServer() {

        val nameEditText = view?.findViewById<EditText>(R.id.put_name)
        val phoneNumberEditText = view?.findViewById<EditText>(R.id.put_phoneNumber)
        val name = nameEditText!!.text.toString()
        val phoneNumber = phoneNumberEditText!!.text.toString()

        // FindIdDTO 객체 생성
        val findIdDTO = FindIdDTO(name, phoneNumber)
        call = service.sendIdName(findIdDTO)

        call.enqueue(object : Callback<GetIdDTO> {
            override fun onResponse(call: Call<GetIdDTO>, response: Response<GetIdDTO>) {
                if (response.isSuccessful) {
                    getIdDTO = response.body()!!
                    val email = getIdDTO.email

                    if (email == null) {
                        Toast.makeText(requireContext(), "잘못된 정보입니다", Toast.LENGTH_SHORT).show()
                        nameEditText.text = (null)
                        phoneNumberEditText.text =(null)
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

            override fun onFailure(call: Call<GetIdDTO>, t: Throwable) {
                Log.d("로그", "아이디 찾기 서버 연결 실패")
            }
        })
    }

}