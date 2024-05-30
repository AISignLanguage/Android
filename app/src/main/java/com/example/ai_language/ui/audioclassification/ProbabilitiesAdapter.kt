package com.example.ai_language.ui.audioclassification

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_language.R
import com.example.ai_language.databinding.ItemProbabilityBinding
import org.tensorflow.lite.support.label.Category

internal class ProbabilitiesAdapter : RecyclerView.Adapter<ProbabilitiesAdapter.ViewHolder>() {
    var categoryList: List<Category> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemProbabilityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category.label, category.score, category.index)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class ViewHolder(private val binding: ItemProbabilityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var primaryProgressColorList: IntArray
        private var backgroundProgressColorList: IntArray

        // 리소스에서 색상 배열 가져와서 초기화
        init {
            primaryProgressColorList =
                binding.root.resources.getIntArray((R.array.colors_progress_primary))
            backgroundProgressColorList =
                binding.root.resources.getIntArray((R.array.colors_progress_background))
        }

        fun bind(label: String, score: Float, index: Int) {
            with(binding) {
                labelTextView.text = label

                progressBar.progressBackgroundTintList =
                    ColorStateList.valueOf(
                        backgroundProgressColorList[index % backgroundProgressColorList.size])

                progressBar.progressTintList =
                    ColorStateList.valueOf(
                        primaryProgressColorList[index % primaryProgressColorList.size])

                // 점수 퍼센트로 변환
                val newValue = (score * 100).toInt()
                progressBar.progress = newValue
            }
        }
    }
}