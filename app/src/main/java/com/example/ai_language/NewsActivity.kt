package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import retrofit2.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Callback
import retrofit2.Response


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

class NewsActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeButton: ImageButton

    lateinit var service: Service
    lateinit var call : Call<List<NewsDTO>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        recyclerView = findViewById(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = NewsAdapter(viewModel)
        recyclerView.adapter = adapter

        homeButton = findViewById(R.id.imageButton)
        homeButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        viewModel.newsList.observe(this, { newsList ->
            adapter.notifyDataSetChanged()
        })

        service = RetrofitClient.getUserRetrofitInterface()
        call = service.getNews()

        call.enqueue(object : Callback<List<NewsDTO>> {
            override fun onResponse(call: Call<List<NewsDTO>>, response: Response<List<NewsDTO>>) {
                if (response.isSuccessful) {
                    val newsList = response.body()
                    newsList?.let {
                        //viewModel.newsList.value = it
                        Log.d("로그", "newsList 전송 성공")
                    }
                } else {
                    Log.e("로그", "newsList 전송 실패")
                }
            }

            override fun onFailure(call: Call<List<NewsDTO>>, t: Throwable) {
                Log.d("로그", "Retrofit 연동 실패")
            }
        })

    }
}