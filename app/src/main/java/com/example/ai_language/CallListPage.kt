package com.example.ai_language

import android.content.Intent
import android.os.Bundle
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
    }

    // 서버에서 데이터를 가져오는 함수
    private fun fetchDataFromServer() {
        val uri = "http://hello.com" // 서버에서 가져올 이미지 URI
        val installCheck = true // 서버에서 가져올 앱 설치 여부 T/F

        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()
        val callListDto = CallListDTO(uri, installCheck)
        call = service.getCallData()

        call.clone().enqueue(object : Callback<CallListDTO> {
            override fun onResponse(call: Call<CallListDTO>, response: Response<CallListDTO>) {
                if (response.isSuccessful) {
                    //val callListDto = response.body()
                    // 받아온 데이터를 처리
                    // 뷰 모델에 연동하여 UI 업데이트 등 수행
                } else {
                    // 요청 실패 처리
                }
            }

            override fun onFailure(call: Call<CallListDTO>, t: Throwable) {
                // 통신 실패 처리
            }
        })
    }
}