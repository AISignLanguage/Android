package com.example.ai_language

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallListPage : AppCompatActivity() {
    lateinit var call: Call<CallListDTO>
    lateinit var service: Service
    private val callViewModel: CallListViewModel by viewModels()
    private val inviteViewModel: InviteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_list)

        //RecyclerView 초기화 및 어댑터 설정 - 앱 사용자
        val rv_call = findViewById<RecyclerView>(R.id.rv_call)
        val callListAdapter = CallListAdapter(callViewModel)
        rv_call.adapter = callListAdapter
        rv_call.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //val call = service.getCallData(uri, installCheck)

        callListAdapter.setOnItemClickListener (object : CallListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Toast.makeText(applicationContext, "전화하기", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, CallActivity::class.java)
                startActivity(intent)
            }
        })

        //뷰 모델 observe
        callViewModel.callDataList.observe(this, Observer { callDataList ->
            callListAdapter.notifyDataSetChanged()
        })

        //RecyclerView 초기화 및 어댑터 설정 - 앱 비 사용자
        val rv_invite = findViewById<RecyclerView>(R.id.rv_invite)
        val InviteListAdapter = InviteListAdapter(inviteViewModel)
        rv_invite.adapter = InviteListAdapter
        rv_invite.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //초대버튼 클릭
        InviteListAdapter.setOnItemClickListener(object : InviteListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Toast.makeText(applicationContext, "초대하기", Toast.LENGTH_SHORT).show()
            }
        })

        //뷰 모델 observe
        inviteViewModel.inviteDataList.observe(this, Observer { inviteDataList ->
            InviteListAdapter.notifyDataSetChanged()
        })

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        homeButton.setOnClickListener{
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
        }

        fetchDataFromServer() //서버에서 데이터 갱신
    }

    // 서버에서 데이터를 가져오는 함수
    // fetchDataFromServer 함수 내에 수정된 코드
    private fun fetchDataFromServer() {
        // Retrofit 인스턴스 생성
        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()
        call = service.getCallData()

        // 서버로부터 데이터를 가져오는 요청 보내기
        call.enqueue(object : Callback<CallListDTO> {
            override fun onResponse(call: Call<CallListDTO>, response: Response<CallListDTO>) {
                if (response.isSuccessful) {
                    val callListDto = response.body() // 서버에서 받은 데이터
                    Log.d("로그", "onCreate 응답 성공")
                    Log.d("로그", "uri: ${callListDto?.uri.toString()} " +
                            " installCheck:${callListDto?.installCheck.toString()}")
                    // 받아온 데이터를 처리
                    // 예: 뷰 모델에 연동하여 UI 업데이트 등 수행
                } else {
                    // 요청 실패 처리
                    Log.d("로그", "데이터 요청 실패. 응답 코드: ${response.code()}, "
                            + "오류 메시지: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<CallListDTO>, t: Throwable) {
                // 통신 실패 처리
                Log.d("로그", "통신 실패: ${t.message}")
            }
        })
    }
}