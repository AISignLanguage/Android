package com.example.ai_language

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Unregister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unregister)

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        homeButton.setOnClickListener{
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }

        val items = resources.getStringArray(R.array.unregister)
        val spinner = findViewById<Spinner>(R.id.spinner)

        val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_item_layout, items.toMutableList()) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == 0) {
                    val textView = v.findViewById<TextView>(R.id.tvItemSpinner)
                    textView.text = ""
                    textView.hint = getItem(0)
                }
                return v
            }

            override fun getCount(): Int {
                return super.getCount()
            }
        }

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = items[position]
                Log.d("Selected Item", selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val unregisterButton = findViewById<Button>(R.id.unregisterButton)
        unregisterButton.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        unregisterButton.setOnClickListener{
            val unregisterDialog = UnregisterDialog(this)
            unregisterDialog.show()

            Handler().postDelayed({
                unregisterDialog.dismiss()
                navigateToHomeActivity()
            }, 3000)
        }

    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}