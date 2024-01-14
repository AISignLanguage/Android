package com.example.ai_language

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class NewsItem(
    var title: String,
    var content: String,
    var imageResourceId: Int
)

class NewsAdapter(val context: Context, val newsList: List<NewsItem>) :
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
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        holder.titleTextView.text = newsItem.title
        holder.contentTextView.text = newsItem.content
        holder.imageView.setImageResource(newsItem.imageResourceId)
    }
}

class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val newsList = listOf(
            NewsItem("News Title 1", "Content 1", R.drawable.newitem),
            NewsItem("News Title 2", "Content 2", R.drawable.newitem),
            NewsItem("News Title 3", "Content 3", R.drawable.newitem)
        )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = NewsAdapter(this, newsList)
        recyclerView.adapter = adapter

        val homeButton = findViewById<ImageButton>(R.id.imageButton)
        homeButton.setOnClickListener{
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }


    }
}