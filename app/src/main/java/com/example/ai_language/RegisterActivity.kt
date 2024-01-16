package com.example.ai_language
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nick = intent.getStringExtra("nick")

        val regName = findViewById<EditText>(R.id.reg_name)
        regName.setText(nick)

        val profile = findViewById<ImageView>(R.id.reg_pro)
        val uriString: String? = intent.getStringExtra("profile")
            if (uriString != null) {

                val profilePx = dpToPx(this, 90)

                Glide.with(this)
                    .load(uriString)
                    .override(profilePx,profilePx)
                    .into(profile)
            } else {
               Log.e("uri 에러","$uriString")
            }

        val regNext = findViewById<TextView>(R.id.reg_next)
        regNext.setOnClickListener {
            val intent = Intent(this,TermsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}