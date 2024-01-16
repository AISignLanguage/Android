package com.example.ai_language

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class DicPic(var dicpic: Uri, var ex: String)
data class Tagdata(var tag: String)



class TagAdapter(val itemList: ArrayList<Tagdata>) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tag_txt = itemView.findViewById<TextView>(R.id.tag)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_tag_item, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val td = itemList[position]
        holder.tag_txt.text = td.tag
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}




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


        val home_btn = findViewById<ImageButton>(R.id.home_btn_dic)
        home_btn.setOnClickListener{
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }


        val rv_dic = findViewById<RecyclerView>(R.id.recyclerGridView)
        val itemList = ArrayList<DicPic>()


        val rv_tag = findViewById<RecyclerView>(R.id.recyclerTag)
        val tagList = ArrayList<Tagdata>()


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


        tagList.add(Tagdata("#운동"))
        tagList.add(Tagdata("#인사"))


        val layoutManager2 = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_tag.layoutManager = layoutManager2

        val adapter2 = TagAdapter(tagList)
        rv_tag.adapter = adapter2

    }

    // Drawable 리소스 ID를 Uri로 변환하는 함수
    private fun drawableResourceIdToUri(context: Context, drawableResId: Int): Uri {
        val res = context.resources
        val uriString = "android.resource://${res.getResourcePackageName(drawableResId)}/${res.getResourceTypeName(drawableResId)}/${res.getResourceEntryName(drawableResId)}"
        return Uri.parse(uriString)
    }
}
