package com.example.ai_language.ui.news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_language.R
import com.example.ai_language.ui.news.adapter.NewsAdapter
import com.example.ai_language.ui.news.viewmodel.NewsViewModel


class TabFragment3 : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_tab3, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)

        recyclerView = rootView.findViewById(R.id.recyclerViewtab3)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NewsAdapter(viewModel.numberList.value ?: emptyList())
        recyclerView.adapter = adapter

        viewModel.numberList.observe(viewLifecycleOwner) { newsList ->
            adapter.updateData(newsList)
        }

        return rootView
    }
}