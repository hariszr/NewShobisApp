package com.example.loginandsignupfirebase

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ListAdapter(private val context:android.content.Context, private val dataList:List<DataClass>): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_traceabbility_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        Glide.with(context).load(dataList[position].dataImage).into(holder.recImage)
        holder.itemRecPID.text = dataList[position].pid
        holder.itemRecVariety.text = dataList[position].dataVariety
        holder.itemRecWeight.text = dataList[position].dataWeight

        holder.itemListRecycler.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("Farmer", dataList[holder.adapterPosition].dataFarmer)
            intent.putExtra("Variety", dataList[holder.adapterPosition].dataVariety)
            intent.putExtra("Weight", dataList[holder.adapterPosition].dataWeight)
            context.startActivity(intent)
        }
    }

}

class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var itemListRecycler : CardView
    var itemRecImage : ImageView
    var itemRecPID : TextView
    var itemRecWeight : TextView
    var itemRecVariety : TextView

    init {
        itemListRecycler = itemView.findViewById(R.id.itemListRecyclerCV)
        itemRecImage = itemView.findViewById(R.id.itemRecImageSIV)
        itemRecPID = itemView.findViewById(R.id.itemRecPIDTV)
        itemRecWeight = itemView.findViewById(R.id.itemRecWeightTV)
        itemRecVariety = itemView.findViewById(R.id.itemRecVarietyTV)
    }
}