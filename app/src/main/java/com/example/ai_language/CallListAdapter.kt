package com.example.ai_language

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView

class CallListAdapter (val itemList: ArrayList<CallListItem>) :
    RecyclerView.Adapter<CallListAdapter.CallListViewHolder>() {

    //클릭 이벤트
    var itemClickListener : OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.call_item, parent, false)
        return CallListViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallListViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.phoneNumber.text = itemList[position].callNumber
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class CallListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val phoneNumber = itemView.findViewById<TextView>(R.id.phoneNumber)

        val callBtn = itemView.findViewById<ImageButton>(R.id.callBtn)
        init {
            callBtn.setOnClickListener{
                itemClickListener?.onItemClick(adapterPosition)
                //페이지 이동
                //val intent = Intent(itemView.context, FaqPage::class.java)
                //itemView.context.startActivity(intent)
            }
        }
    }
}