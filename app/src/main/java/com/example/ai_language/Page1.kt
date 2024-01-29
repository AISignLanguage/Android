package com.example.ai_language

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class Page1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_page1, container, false)
        view.setOnClickListener {
            val intent = Intent(activity, Poster::class.java)
            startActivity(intent)
        }
        return view
    }
}