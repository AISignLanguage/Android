package com.example.ai_language

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TabFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_tab, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)

        recyclerView = rootView.findViewById(R.id.recyclerViewtab1)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NewsAdapter(viewModel.consonantList.value ?: emptyList())
        recyclerView.adapter = adapter

        viewModel.consonantList.observe(viewLifecycleOwner, { newsList ->
            adapter.updateData(newsList)
        })

        return rootView
    }
}