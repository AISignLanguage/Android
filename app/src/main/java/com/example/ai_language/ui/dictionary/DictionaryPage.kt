package com.example.ai_language.ui.dictionary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.ai_language.BuildConfig
import com.example.ai_language.ui.home.Home
import com.example.ai_language.R
import com.example.ai_language.Util.RetrofitClient
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.ActivityDictionaryPageBinding
import com.example.ai_language.ui.dictionary.adapter.DicAdapter
import com.example.ai_language.ui.dictionary.adapter.TagAdapter
import com.example.ai_language.ui.dictionary.data.Tagdata
import com.example.ai_language.ui.dictionary.viewmodel.DictionaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DictionaryPage :
    BaseActivity<ActivityDictionaryPageBinding>(R.layout.activity_dictionary_page) {
    private val dicViewModel by viewModels<DictionaryViewModel>()
    private val dicAdapter = DicAdapter()


    override fun setLayout() {
        startServer()
        onClickedByNavi()
        setRecyclerView()

    }

    private fun setRecyclerView() {
        setGridRecyclerView()
        setTagRecyclerView()
    }

    private fun startServer() {
        dicViewModel.getDictionaryByOpenApi(
            BuildConfig.Dictionary_Api_key,
            "100",
            "1"
        )
    }

    private fun setTagRecyclerView() {
        with(binding) {
            val layoutManager2 =
                LinearLayoutManager(this@DictionaryPage, LinearLayoutManager.HORIZONTAL, false)
            recyclerTag.layoutManager = layoutManager2

            val adapter2 = dicViewModel.tag_data.value?.let { TagAdapter(it) }
            recyclerTag.adapter = adapter2
            dicViewModel.tag_data.observe(this@DictionaryPage) {
                adapter2?.notifyDataSetChanged()
            }
            if (dicViewModel.tag_data.value?.isEmpty() == true) {
                dicViewModel.tagAddData(Tagdata("전체"))
            }
        }
    }

    private fun setGridRecyclerView() {
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_layout_margin)
        binding.recyclerGridView.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                spacingInPixels,
                true
            )
        )
        // GridLayoutManager를 사용하여 2열 그리드로 설정
        val layoutManager = GridLayoutManager(this@DictionaryPage, 2)
        binding.recyclerGridView.layoutManager = layoutManager
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                dicViewModel.dicList.collectLatest {
                    if (it.response?.body?.items?.item?.isEmpty() == true) {
                        binding.blankInfo.visibility = View.VISIBLE
                    } else {
                        binding.blankInfo.visibility = View.GONE
                        dicAdapter.update(it)
                        binding.recyclerGridView.adapter = dicAdapter
                    }
                }
            }
        }
    }

    private fun onClickedByNavi() {
//        binding.linearLayout3.setNavigationOnClickListener {
//            val navController = findNavController()
//            navController.navigate(R.id.action_navigation_dictionary_to_navigation_home)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

}
