package com.example.ai_language

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

class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // 아이템 위치
        val column = position % spanCount // 아이템 열

        if (includeEdge) {
            outRect.left = spacing * (spanCount - column) / spanCount // 왼쪽 여백
            outRect.right = spacing * (column + 1) / spanCount // 오른쪽 여백

            if (position < spanCount) { // 첫 번째 행의 상단 여백
                outRect.top = spacing
            }
            outRect.bottom = spacing // 아이템 하단 여백
        } else {
            outRect.left = spacing * column / spanCount // 왼쪽 여백
            outRect.right = spacing - (column + 1) * spacing / spanCount // 오른쪽 여백
            if (position >= spanCount) {
                outRect.top = spacing // 아이템 상단 여백
            }
        }
    }
}



class TagAdapter(val itemList: ArrayList<Tagdata>) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tag_txt = itemView.findViewById<Button>(R.id.tag)
        private var isButtonPressed = false
        init {
            tag_txt.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    isButtonPressed = !isButtonPressed // 상태 토글
                    setButtonState(isButtonPressed) // 버튼 상태 업데이트

                }
            }
        }
        private fun setButtonState(isPressed: Boolean) {
            isButtonPressed = isPressed
            if (isButtonPressed) {
                // 첫 번째 상태: 배경 #9999FF, 글씨 흰색
                tag_txt.setBackgroundResource(R.drawable.tag)
                tag_txt.setTextColor(Color.WHITE)
            } else {
                // 두 번째 상태: 배경 투명 & 테두리 #9999FF, 글씨 #9999FF
                tag_txt.setBackgroundResource(R.drawable.nottag)
                tag_txt.setTextColor(Color.parseColor("#9999FF"))
            }
            tag_txt.invalidate() // 뷰를 강제로 다시 그리도록 함
        }
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




class DicAdapter(val itemList: ArrayList<DicPic>,private val listener:OnItemClickListener) :
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

class DictionaryPage : AppCompatActivity(),DicAdapter.OnItemClickListener {
    private lateinit var dicViewModel: DictionaryViewModel


    private lateinit var rv_dic : RecyclerView
    private lateinit var rv_tag : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary_page)


        dicViewModel = ViewModelProvider(this)[DictionaryViewModel::class.java]

        val home_btn = findViewById<ImageButton>(R.id.home_btn_dic)
        home_btn.setOnClickListener{
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }
        rv_dic = findViewById<RecyclerView>(R.id.recyclerGridView)
        rv_tag = findViewById<RecyclerView>(R.id.recyclerTag)
        val spacingInPixels =resources.getDimensionPixelSize(R.dimen.grid_layout_margin)
        rv_dic.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))



        // 여러 Drawable 리소스 ID를 가져와 Uri로 변환하여 itemList에 추가
        val drawableResId1 = R.drawable.recycleritem // Drawable 리소스 ID 1
        val drawableUri1 = drawableResourceIdToUri(this, drawableResId1)
        dicViewModel.dicAddData(DicPic(drawableUri1, "설명 1"))

        val drawableResId2 = R.drawable.recycleritem // Drawable 리소스 ID 2
        val drawableUri2 = drawableResourceIdToUri(this, drawableResId2)
        dicViewModel.dicAddData(DicPic(drawableUri2, "설명 2"))


        // GridLayoutManager를 사용하여 2열 그리드로 설정
        val layoutManager = GridLayoutManager(this, 2)
        rv_dic.layoutManager = layoutManager


        // RecyclerView에 어댑터 설정
        val adapter = dicViewModel.dic_data.value?.let { DicAdapter(it, this) }
        rv_dic.adapter = adapter

        dicViewModel.tagAddData(Tagdata("전체"))


        val layoutManager2 = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_tag.layoutManager = layoutManager2

        val adapter2 = dicViewModel.tag_data.value?.let { TagAdapter(it) }
        rv_tag.adapter = adapter2




        dicViewModel.dic_data.observe(this, Observer { newData ->
            adapter?.notifyDataSetChanged()
        })

        dicViewModel.tag_data.observe(this, Observer { newData ->
            adapter2?.notifyDataSetChanged()
        })


    }

    override fun onItemClick(dicPic: DicPic) {

    }
    private fun drawableResourceIdToUri(context: Context, drawableResId: Int): Uri {
        val res = context.resources
        val uriString = "android.resource://${res.getResourcePackageName(drawableResId)}/${res.getResourceTypeName(drawableResId)}/${res.getResourceEntryName(drawableResId)}"
        return Uri.parse(uriString)
    }

}
