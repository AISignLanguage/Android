package com.example.ai_language

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class InviteListAdapter (val itemList: ArrayList<InviteListItem>) :
    RecyclerView.Adapter<InviteListAdapter.InviteListViewHolder>() {

    //클릭 이벤트
    var itemClickListener : OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.invite_item, parent, false)
        return InviteListViewHolder(view)
    }

    override fun onBindViewHolder(holder: InviteListViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.phoneNumber.text = itemList[position].callNumber
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class InviteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val phoneNumber = itemView.findViewById<TextView>(R.id.phoneNumber)

        val inviteBtn = itemView.findViewById<ImageButton>(R.id.inviteBtn)
        init {
            inviteBtn.setOnClickListener{
                itemClickListener?.onItemClick(adapterPosition)
                //val intent = Intent(itemView.context, FaqPage::class.java)
                //itemView.context.startActivity(intent)
            }
        }
    }
}
