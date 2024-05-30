package com.example.ai_language.ui.extensions

import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.databinding.BindingAdapter
import com.example.ai_language.R



@BindingAdapter("setFormattedText", "wordLimit")
fun setFormattedText(view: TextView, input: String?, wordLimit: Int) {
    input?.let {
        val words = it.split(" ")
        val formattedText = if (words.size > wordLimit) {
            words.chunked(wordLimit) { chunk -> chunk.joinToString(" ") }
                .joinToString("\n")
        } else {
            it
        }
        view.text = formattedText
    }
}

@BindingAdapter("setDateFormat")
fun TextView.setDateFormat(input: String?) {
    if (input != null) {
        val date = input.substring(0, 10)
        text = date
    }
}

//
//
//@BindingAdapter("setRealMoneyHelped")
//fun TextView.setRealMoneyHelped(input: Long?) {
//    if (input != null) {
//        val text = getString(context, R.string.text_guide_helped).format((input * 2500))
//        setText(text)
//    }