package com.example.ai_language

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class DicPic(var dicpic: Uri, var ex: String)

class DicAdapter(val itemList: ArrayList<DicPic>) :
    RecyclerView.Adapter<DicAdapter.DicViewHolder>() {

    inner class DicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dic_img = itemView.findViewById<ImageView>(R.id.dic_item)
        val dic_ex = itemView.findViewById<TextView>(R.id.ex_text)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DicViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_dic_item, parent, false)
        return DicViewHolder(view)
    }

    override fun onBindViewHolder(holder: DicViewHolder, position: Int) {
        val dicPic = itemList[position]
        val dicUri = dicPic.dicpic
        holder.dic_img.setImageURI(dicUri)
        holder.dic_ex.text = dicPic.ex
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}

class DictionaryPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary_page)

        val rv_dic = findViewById<RecyclerView>(R.id.recyclerGridView)
        val itemList = ArrayList<DicPic>()

        // 여러 Drawable 리소스 ID를 가져와 Uri로 변환하여 itemList에 추가
        val drawableResId1 = R.drawable.recycleritem // Drawable 리소스 ID 1
        val drawableUri1 = drawableResourceIdToUri(this, drawableResId1)
        itemList.add(DicPic(drawableUri1, "설명 1")) // 설명 텍스트 1

        val drawableResId2 = R.drawable.recycleritem // Drawable 리소스 ID 2
        val drawableUri2 = drawableResourceIdToUri(this, drawableResId2)
        itemList.add(DicPic(drawableUri2, "설명 2")) // 설명 텍스트 2

        // GridLayoutManager를 사용하여 2열 그리드로 설정
        val layoutManager = GridLayoutManager(this, 2)
        rv_dic.layoutManager = layoutManager

        // RecyclerView에 어댑터 설정
        val adapter = DicAdapter(itemList)
        rv_dic.adapter = adapter
    }

    // Drawable 리소스 ID를 Uri로 변환하는 함수
    private fun drawableResourceIdToUri(context: Context, drawableResId: Int): Uri {
        val res = context.resources
        val uriString = "android.resource://${res.getResourcePackageName(drawableResId)}/${res.getResourceTypeName(drawableResId)}/${res.getResourceEntryName(drawableResId)}"
        return Uri.parse(uriString)
    }
}
