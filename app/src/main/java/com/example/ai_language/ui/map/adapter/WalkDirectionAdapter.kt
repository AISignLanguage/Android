package com.example.ai_language.ui.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_language.databinding.ItemWalkingDetailBinding
import com.example.ai_language.domain.model.response.Feature
import com.example.ai_language.domain.model.response.Properties

class WalkDirectionAdapter : RecyclerView.Adapter<WalkDirectionAdapter.WalkDirectionViewHolder>() {
    private val items = mutableListOf<Feature>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkDirectionViewHolder {
        return WalkDirectionViewHolder.from(parent)
    }
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: WalkDirectionViewHolder, position: Int) {
        holder.bind(items[position])
    }
    fun update(newItems: List<Feature>) {
        val diffUtil = PostListDiffUtil(items, newItems)
        val result = DiffUtil.calculateDiff(diffUtil)
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
    class PostListDiffUtil(
        private val oldItems: List<Feature>,
        private val newItems: List<Feature>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size
        override fun getNewListSize(): Int = newItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }
    class WalkDirectionViewHolder(
        private val binding: ItemWalkingDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Feature) {
            binding.walkingDetail = item.properties
            binding.tvDistanceStep.text = item.properties.distance
            binding.tvDurationStep.text = item.properties.time
        }
        companion object {
            fun from(parent: ViewGroup): WalkDirectionViewHolder {
                return WalkDirectionViewHolder(
                    ItemWalkingDetailBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}
