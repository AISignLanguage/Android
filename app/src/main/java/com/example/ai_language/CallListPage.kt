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

class CallListPage : AppCompatActivity() {
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

        callListAdapter.setOnItemClickListener (object : CallListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Toast.makeText(applicationContext, "전화하기", Toast.LENGTH_SHORT).show()
                callViewModel.addListItem(CallListItem("신정인", "010-1113-0088"))
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
                inviteViewModel.addListItem(InviteListItem("신정인", "011-1266-8923"))
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
}