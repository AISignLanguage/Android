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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.ai_language.ui.home.Home
import com.example.ai_language.R
import com.example.ai_language.Util.RetrofitClient
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.ActivityDictionaryPageBinding
import com.example.ai_language.domain.model.response.ResponseBodys
import com.example.ai_language.ui.dictionary.adapter.DicAdapter
import com.example.ai_language.ui.dictionary.adapter.TagAdapter
import com.example.ai_language.ui.dictionary.viewmodel.DicPic
import com.example.ai_language.ui.dictionary.viewmodel.DictionaryViewModel
import com.example.ai_language.ui.dictionary.viewmodel.Tagdata
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DictionaryPage : BaseFragment<ActivityDictionaryPageBinding>(R.layout.activity_dictionary_page), DicAdapter.OnItemClickListener {
    private lateinit var dicViewModel: DictionaryViewModel


    override fun setLayout() {
        with(binding) {
            startServer()
            dicViewModel = ViewModelProvider(requireActivity())[DictionaryViewModel::class.java]

            homeBtnDic.setOnClickListener {
                val intent = Intent(requireContext(), Home::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_layout_margin)
            recyclerGridView.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))


            // 여러 Drawable 리소스 ID를 가져와 Uri로 변환하여 itemList에 추가
            val drawableResId1 = R.drawable.recycleritem // Drawable 리소스 ID 1
            val drawableUri1 = drawableResourceIdToUri(requireContext(), drawableResId1)
            dicViewModel.dicAddData(DicPic(drawableUri1, "설명 1"))

            val drawableResId2 = R.drawable.recycleritem // Drawable 리소스 ID 2
            val drawableUri2 = drawableResourceIdToUri(requireContext(), drawableResId2)
            dicViewModel.dicAddData(DicPic(drawableUri2, "설명 2"))


            // GridLayoutManager를 사용하여 2열 그리드로 설정
            val layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerGridView.layoutManager = layoutManager


            // RecyclerView에 어댑터 설정
            val adapter = dicViewModel.dic_data.value?.let { DicAdapter(it, this@DictionaryPage) }
            recyclerGridView.adapter = adapter

            dicViewModel.tagAddData(Tagdata("전체"))


            val layoutManager2 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerTag.layoutManager = layoutManager2

            val adapter2 = dicViewModel.tag_data.value?.let { TagAdapter(it) }
            recyclerTag.adapter = adapter2




            dicViewModel.dic_data.observe(requireActivity(), Observer { newData ->
                adapter?.notifyDataSetChanged()
            })

            dicViewModel.tag_data.observe(requireActivity(), Observer { newData ->
                adapter2?.notifyDataSetChanged()
            })

        }

    }

    private fun startServer() {
        val service = RetrofitClient.getUserRetrofitInterface2()
        val call = service.fetchData(
            "73de7874-baa6-4268-8909-f5eb6d3decb6",
            "100",
            "1"
        )
        call.enqueue(object : Callback<ResponseBodys> {

            override fun onResponse(call: Call<ResponseBodys>, response: Response<ResponseBodys>) {
                if (response.isSuccessful) {

                    dicViewModel.dic_data.value?.clear()
                    val items = response.body()?.response?.body?.items?.item
                    items?.let {
                        for (item in items) {
                            val title = item.title
                            val ex = item.subDescription
                            val referenceIdentifier = item.referenceIdentifier
                            val uri = Uri.parse(referenceIdentifier)

                            // DicPic 객체를 생성하여 리스트에 추가
                            val dicPic = DicPic(uri, title)
                            dicViewModel.dicAddData(dicPic)
                        }
                    }
                } else {
                    Log.d("에러:", "응답 실패")
                }
            }

            override fun onFailure(call: Call<ResponseBodys>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }


    override fun onItemClick(dicPic: DicPic) {

    }

    private fun drawableResourceIdToUri(context: Context, drawableResId: Int): Uri {
        val res = context.resources
        val uriString = "android.resource://${res.getResourcePackageName(drawableResId)}/${
            res.getResourceTypeName(drawableResId)
        }/${res.getResourceEntryName(drawableResId)}"
        return Uri.parse(uriString)
    }

}
