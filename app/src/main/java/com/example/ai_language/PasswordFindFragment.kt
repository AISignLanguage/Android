package com.example.ai_language

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

class PasswordFindFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_find, container, false)
    }

    fun findPassword() {
        val id_edit = view?.findViewById<EditText>(R.id.find_id)
        val certification_edit = view?.findViewById<EditText>(R.id.certification_et)
        val find_btn = view?.findViewById<TextView>(R.id.find_id_btn)

        find_btn?.setOnClickListener {

        }
    }
}