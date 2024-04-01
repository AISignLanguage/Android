package com.example.ai_language.ui.dictionary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_language.databinding.ActivityDicItemBinding
import com.example.ai_language.domain.model.response.Item
import com.example.ai_language.domain.model.response.ResponseBodys
class DicAdapter : RecyclerView.Adapter<DicAdapter.DicViewHolder>() {
    private val items = mutableListOf<Item>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DicViewHolder {
        return DicViewHolder.from(parent)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DicViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun update(newItems: ResponseBodys) {
        val positionStart = newItems.response?.body?.items?.item?.size
        newItems.response?.body?.items?.item?.let { items.addAll(it) }
        if (positionStart != null) {
            notifyItemRangeInserted(positionStart, items.size)
        }
    }

    class DicViewHolder(
        private val binding: ActivityDicItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.dicPic = item
        }

        companion object {
            fun from(parent: ViewGroup): DicViewHolder {
                return DicViewHolder(
                    ActivityDicItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}
