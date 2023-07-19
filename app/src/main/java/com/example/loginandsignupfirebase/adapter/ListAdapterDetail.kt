package com.example.loginandsignupfirebase

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginandsignupfirebase.model.DataClassAdd
import com.example.loginandsignupfirebase.model.DataClassNewAdd

class ListAdapterDetail(private val context:android.content.Context, private val dataList:List<DataClassAdd>): RecyclerView.Adapter<MyViewHolderDetail>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderDetail {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_detail, parent, false)
        return MyViewHolderDetail(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolderDetail, position: Int) {
//        Glide.with(context).load(dataList[position].dataQrCode).into(holder.itemRecQrCode)
        holder.itemPID.text = dataList[position].pid
        holder.itemCreateDate.text = dataList[position].dataDateCreate
        holder.itemArrivedDate.text = dataList[position].dataArriveDate
        holder.itemIncomingWeight.text = dataList[position].dataIncomingWeight
        holder.itemGradeSecondary.text = dataList[position].dataGrade
        holder.itemPriceSecondary.text = dataList[position].dataPrice
        holder.itemOutgoingWeight.text = dataList[position].dataOutgoingWeight
        holder.itemLossProduct.text = dataList[position].dataWeightLoss
        holder.itemOutgoingDate.text = dataList[position].dataOutgoingDate

//        holder.itemLL.setOnClickListener {
//            val intent = Intent(context, DetailActivity::class.java)
//            intent.putExtra("PID", dataList[holder.adapterPosition].pid)
//            intent.putExtra("Variety", dataList[holder.adapterPosition].dataVariety)
//            intent.putExtra("Weight", dataList[holder.adapterPosition].dataWeight)
//            intent.putExtra("Grade", dataList[holder.adapterPosition].dataGrade)
//            intent.putExtra("Price", dataList[holder.adapterPosition].dataPrice)
//
//            intent.putExtra("Farmer", dataList[holder.adapterPosition].dataFarmer)
//            intent.putExtra("Day", dataList[holder.adapterPosition].dataDay)
//            intent.putExtra("Area", dataList[holder.adapterPosition].dataPlantingArea)
//            intent.putExtra("Fertilizer", dataList[holder.adapterPosition].dataFertilizer)
//            intent.putExtra("Pesticides", dataList[holder.adapterPosition].dataPesticides)
//
//            intent.putExtra("Date", dataList[holder.adapterPosition].dataDateCreate)
//
//            intent.putExtra("Note", dataList[holder.adapterPosition].dataNotes)
//            intent.putExtra("QrCode", dataList[holder.adapterPosition].dataQrCode)
//            context.startActivity(intent)
//        }
    }

}

class MyViewHolderDetail(itemView: View): RecyclerView.ViewHolder(itemView){
    var itemLL : LinearLayout
    var itemPID : TextView
    var itemCreateDate : TextView
    var itemArrivedDate : TextView
    var itemIncomingWeight : TextView
    var itemGradeSecondary : TextView
    var itemPriceSecondary : TextView
    var itemOutgoingWeight : TextView
    var itemLossProduct : TextView
    var itemOutgoingDate : TextView

    init {
        itemLL = itemView.findViewById(R.id.itemDetailRecyclerViewLL)
        itemPID = itemView.findViewById(R.id.productIDSecondaryTV)
        itemCreateDate = itemView.findViewById(R.id.dateCreateSecondaryTV)
        itemArrivedDate = itemView.findViewById(R.id.arrivedDateTV)
        itemIncomingWeight = itemView.findViewById(R.id.incomingWeightTV)
        itemGradeSecondary = itemView.findViewById(R.id.gradeSecodaryTV)
        itemPriceSecondary = itemView.findViewById(R.id.priceSecondaryTV)
        itemOutgoingWeight = itemView.findViewById(R.id.outgoingWeightTV)
        itemLossProduct = itemView.findViewById(R.id.weightLossTV)
        itemOutgoingDate= itemView.findViewById(R.id.outgoingDateTV)
    }
}