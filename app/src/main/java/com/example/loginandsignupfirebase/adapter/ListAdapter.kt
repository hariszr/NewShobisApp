package com.example.loginandsignupfirebase

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginandsignupfirebase.model.DataClassNewAdd
import com.google.firebase.database.DataSnapshot

class ListAdapter(private val context:android.content.Context, private val dataList:List<DataClassNewAdd>): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_traceability, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        Glide.with(context).load(dataList[position].dataQrCode).into(holder.itemRecQrCode)
        holder.itemRecPID.text = dataList[position].key
        holder.itemRecVariety.text = dataList[position].dataVariety
        holder.itemRecWeight.text = dataList[position].dataWeight

        holder.itemRec.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)

            val childNodePID = dataList[position].key
            intent.putExtra("PIDNode", childNodePID)

            intent.putExtra("PID", dataList[holder.adapterPosition].pid)
            intent.putExtra("Variety", dataList[holder.adapterPosition].dataVariety)
            intent.putExtra("Weight", dataList[holder.adapterPosition].dataWeight)
            intent.putExtra("Grade", dataList[holder.adapterPosition].dataGrade)
            intent.putExtra("Price", dataList[holder.adapterPosition].dataPrice)

            intent.putExtra("Farmer", dataList[holder.adapterPosition].dataFarmer)
            intent.putExtra("Day", dataList[holder.adapterPosition].dataDay)
            intent.putExtra("Area", dataList[holder.adapterPosition].dataPlantingArea)
            intent.putExtra("Fertilizer", dataList[holder.adapterPosition].dataFertilizer)
            intent.putExtra("Pesticides", dataList[holder.adapterPosition].dataPesticides)

            intent.putExtra("Date", dataList[holder.adapterPosition].dataDateCreate)

            intent.putExtra("Note", dataList[holder.adapterPosition].dataNotes)
            intent.putExtra("QrCode", dataList[holder.adapterPosition].dataQrCode)

            context.startActivity(intent)
        }
    }

}

class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var itemRec : CardView
    var itemRecQrCode : ImageView
    var itemRecPID : TextView
    var itemRecWeight : TextView
    var itemRecVariety : TextView

    init {
        itemRec = itemView.findViewById(R.id.itemTraceRecyclerView)
        itemRecQrCode = itemView.findViewById(R.id.itemRecQrCodeSIV)
        itemRecPID = itemView.findViewById(R.id.itemRecPIDTV)
        itemRecWeight = itemView.findViewById(R.id.itemRecWeightTV)
        itemRecVariety = itemView.findViewById(R.id.itemRecVarietyTV)
    }
}