package com.example.ai_language

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsAdapter(private val viewModel: NewsViewModel) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.newsTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.content)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_news_item, parent, false)
        return NewsViewHolder(view);
    }

    override fun getItemCount(): Int {
        return viewModel.newsList.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = viewModel.newsList.value?.get(position)
        newsItem?.let {
            holder.titleTextView.text = it.title
            holder.contentTextView.text = it.content
            Glide.with(holder.itemView)
                .load(it.imageResourceId)
                .into(holder.imageView)
        }
    }
}