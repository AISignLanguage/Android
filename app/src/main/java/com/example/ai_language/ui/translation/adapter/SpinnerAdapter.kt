package com.example.ai_language.ui.translation.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerAdapter(
    context: Context,
    private val languageNames: Array<String>,
    private val languageMap: Map<String, String>
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, languageNames) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    // 스피너 닫혀 있을 때 뷰 생성
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(convertView, parent, position, android.R.layout.simple_spinner_item)
    }

    // 스피너 열려 있을 때 드롭다운 뷰 목록 생성
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(convertView, parent, position, android.R.layout.simple_spinner_dropdown_item)
    }

    private fun createViewFromResource(convertView: View?, parent: ViewGroup, position: Int, resource: Int): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = languageNames[position]
        return view
    }

    // 현재 위치 언어 코드 반환
    fun getLanguageCode(position: Int): String {
        return languageMap[languageNames[position]] ?: ""
    }

}