package com.example.ai_language

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

data class CallListItem(val name: String, val callNumber: String)
data class InviteListItem(val name: String, val callNumber: String)
class CallListPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_list)
    }
}