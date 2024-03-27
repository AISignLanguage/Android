package com.example.ai_language.ui.call

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_language.R

class InviteListAdapter(private val viewModel: InviteViewModel) :
    RecyclerView.Adapter<InviteListAdapter.InviteListViewHolder>() {

    //커스텀 클릭 이벤트 (CallListPage에서 클릭 이벤트)
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    private lateinit var mOnItemClickListener: OnItemClickListener //객체 저장 변수
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) { //객체 전달 메서드
        mOnItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.invite_item, parent, false)
        return InviteListViewHolder(view)
    }

    override fun onBindViewHolder(holder: InviteListViewHolder, position: Int) {
        val item = viewModel.inviteDataList.value?.get(position)
        item?.let {
            holder.name.text = it.name
            holder.phoneNumber.text = it.callNumber
        }
    }

    override fun getItemCount(): Int {
        return viewModel.inviteDataList.value?.size ?: 0
    }

    inner class InviteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val phoneNumber = itemView.findViewById<TextView>(R.id.phoneNumber)
        val inviteBtn = itemView.findViewById<ImageButton>(R.id.inviteBtn)

        init {
            inviteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(itemView, position)
                }
            }
        }
    }
}
