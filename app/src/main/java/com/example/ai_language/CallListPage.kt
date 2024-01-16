package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class CallListItem(val name: String, val callNumber: String)
data class InviteListItem(val name: String, val callNumber: String)
class CallListPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_list)

        val rv_call = findViewById<RecyclerView>(R.id.rv_call)
        val rv_invite = findViewById<RecyclerView>(R.id.rv_invite)
        val CallList = ArrayList<CallListItem>()
        val InviteList = ArrayList<InviteListItem>()
        val CallListAdapter = CallListAdapter(CallList)
        val InviteListAdapter = InviteListAdapter(InviteList)

        CallList.add(CallListItem("박지원", "010-1234-5678"))
        CallList.add(CallListItem("임다솔", "010-5322-1345"))
        CallList.add(CallListItem("신정인", "010-1209-0084"))
        CallList.add(CallListItem("신정인", "010-1209-0084"))

        InviteList.add(InviteListItem("방경원", "010-8219-5021"))
        InviteList.add(InviteListItem("김모모", "010-8884-1821"))
        InviteList.add(InviteListItem("임멍멍", "010-1523-8445"))

        CallListAdapter.notifyDataSetChanged()
        InviteListAdapter.notifyDataSetChanged()

        rv_call.adapter = CallListAdapter
        rv_invite.adapter = InviteListAdapter
        rv_call.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_invite.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        CallListAdapter.itemClickListener = object : CallListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val item = CallList[position]
                Toast.makeText(applicationContext, "클릭함", Toast.LENGTH_SHORT).show()
            }
        }

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        homeButton.setOnClickListener{
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}