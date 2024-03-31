package com.example.ai_language.ui.call.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ai_language.databinding.CallItemBinding
import com.example.ai_language.domain.model.request.PhoneDTO
import com.example.ai_language.ui.call.viewmodel.CallListViewModel

class CallListAdapter(private val viewModel: CallListViewModel) :
    RecyclerView.Adapter<CallListAdapter.CallListViewHolder>() {
        private val items = mutableListOf<PhoneDTO>()

    //커스텀 클릭 이벤트 (CallListPage에서 클릭 이벤트)
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    private lateinit var mOnItemClickListener: OnItemClickListener //객체 저장 변수
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) { //객체 전달 메서드
        mOnItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallListViewHolder {
        val binding = CallItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallListViewHolder(binding)
    }

    fun update(newItems: PhoneDTO) {
        items.add(newItems)
        notifyItemInserted(items.size)
    }

    override fun onBindViewHolder(holder: CallListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CallListViewHolder(private val binding: CallItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.callBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(binding.root, position)
                }
            }
        }

        fun bind(item: PhoneDTO) {
            binding.name.text = item.name
            binding.phoneNumber.text = item.phoneNumbers
            Glide.with(binding.root.context)
                .load(item.profileImageUrl)
                .circleCrop()
                .into(binding.profileImageView)
        }
    }

}