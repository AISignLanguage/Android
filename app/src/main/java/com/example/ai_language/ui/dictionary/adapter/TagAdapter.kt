package com.example.ai_language.ui.dictionary.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_language.R
import com.example.ai_language.ui.dictionary.viewmodel.Tagdata

class TagAdapter(val itemList: ArrayList<Tagdata>) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tag_txt = itemView.findViewById<Button>(R.id.tag)
        private var isButtonPressed = false

        init {
            tag_txt.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    isButtonPressed = !isButtonPressed // 상태 토글
                    setButtonState(isButtonPressed) // 버튼 상태 업데이트

                }
            }
        }

        private fun setButtonState(isPressed: Boolean) {
            isButtonPressed = isPressed
            if (isButtonPressed) {
                // 첫 번째 상태: 배경 #9999FF, 글씨 흰색
                tag_txt.setBackgroundResource(R.drawable.tag)
                tag_txt.setTextColor(Color.WHITE)
            } else {
                // 두 번째 상태: 배경 투명 & 테두리 #9999FF, 글씨 #9999FF
                tag_txt.setBackgroundResource(R.drawable.nottag)
                tag_txt.setTextColor(Color.parseColor("#9999FF"))
            }
            tag_txt.invalidate() // 뷰를 강제로 다시 그리도록 함
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_tag_item, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val td = itemList[position]
        holder.tag_txt.text = td.tag
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}

