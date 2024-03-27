package com.example.ai_language.ui.dictionary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.ai_language.R
import com.example.ai_language.ui.dictionary.viewmodel.DicPic

class DicAdapter(val itemList: ArrayList<DicPic>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<DicAdapter.DicViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(dicPic: DicPic)
    }

    inner class DicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dic_img = itemView.findViewById<ImageView>(R.id.dic_item)
        val dic_ex = itemView.findViewById<TextView>(R.id.dic_text)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(itemList[position])
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DicViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_dic_item, parent, false)
        return DicViewHolder(view)
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    override fun onBindViewHolder(holder: DicViewHolder, position: Int) {
        val dicPic = itemList[position]
        val dicUri = dicPic.dicpic
        //holder.dic_img.setImageURI(dicUri)
        val radiusInPixels = dpToPx(holder.itemView.context, 26)
        holder.dic_ex.text = dicPic.ex
        Glide.with(holder.itemView)
            .load(dicUri)
            .transform(RoundedCorners(radiusInPixels))
            .centerCrop()
            .into(holder.dic_img)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
