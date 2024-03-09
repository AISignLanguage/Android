package com.example.ai_language.call

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ai_language.R

class CallListAdapter (private val viewModel: CallListViewModel) : RecyclerView.Adapter<CallListAdapter.CallListViewHolder>() {

    //커스텀 클릭 이벤트 (CallListPage에서 클릭 이벤트)
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
    private lateinit var mOnItemClickListener: OnItemClickListener //객체 저장 변수
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) { //객체 전달 메서드
        mOnItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.call_item, parent, false)
        return CallListViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallListViewHolder, position: Int) {
        val item = viewModel.callDataList.value?.get(position)
        item?.let {
            holder.name.text = it.name
            holder.phoneNumber.text = it.callNumber
            Glide.with(holder.itemView.context)
                .load(it.imageUrl) // 이미지의 URL을 전달하여 로드
                //.error(R.drawable.error) // 이미지 로딩 실패 시 표시할 이미지
                .circleCrop()
                .into(holder.profileImageView) // 이미지를 설정할 ImageView
        }
    }

    override fun getItemCount(): Int {
        return viewModel.callDataList.value?.size ?: 0
    }

    inner class CallListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val phoneNumber = itemView.findViewById<TextView>(R.id.phoneNumber)
        val profileImageView = itemView.findViewById<ImageView>(R.id.profileImageView)
        val callBtn = itemView.findViewById<ImageButton>(R.id.callBtn)

        init {
            callBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(itemView, position)
                }
            }
        }
    }
}