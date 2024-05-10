package com.example.ai_language.ui.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_language.databinding.ItemDirectionDetailBinding
import com.example.ai_language.domain.model.response.DirectionsResponse
import com.example.ai_language.domain.model.response.Step

class DirectionDetailAdapter : RecyclerView.Adapter<DirectionDetailAdapter.DirectionViewHolder>() {
    private val items = mutableListOf<Step>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectionViewHolder {
        return DirectionViewHolder.from(parent)
    }
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: DirectionViewHolder, position: Int) {
        holder.bind(items[position])
    }
    fun update(newItems: List<Step>) {
        val diffUtil = PostListDiffUtil(items, newItems)
        val result = DiffUtil.calculateDiff(diffUtil)
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)

    }
    class PostListDiffUtil(
        private val oldItems: List<Step>,
        private val newItems: List<Step>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size
        override fun getNewListSize(): Int = newItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return oldItem.steps == newItem.steps
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }
    class DirectionViewHolder(
        private val binding: ItemDirectionDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Step) {
            binding.directionDetail = item
            binding.duration = item.duration
            binding.distance = item.distance
        }
        companion object {
            fun from(parent: ViewGroup): DirectionViewHolder {
                return DirectionViewHolder(
                    ItemDirectionDetailBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}
