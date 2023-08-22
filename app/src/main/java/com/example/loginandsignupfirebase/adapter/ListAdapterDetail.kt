package com.example.loginandsignupfirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginandsignupfirebase.model.DataClassAdd

class ListAdapterDetail(private val dataList: List<DataClassAdd>): RecyclerView.Adapter<MyViewHolderDetail>() {
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

        holder.itemOutgoingDate.text = dataList[position].dataOutgoingDate
        holder.itemOutgoingWeight.text = dataList[position].dataOutgoingWeight
        holder.itemGradeSecondary.text = dataList[position].dataGrade
        holder.itemHandlingSecondary.text = dataList[position].dataHandling
        holder.itemHandlingFeeSecondary.text = dataList[position].dataHandlingFee
        holder.itemLossProduct.text = dataList[position].dataWeightLoss
        holder.itemSellingPriceSecondary.text = dataList[position].dataSellingPrice
        holder.itemDistribution.text = dataList[position].dataDistribution

        holder.itemNotes.text = dataList[position].dataNotes

        holder.itemCreator.text = dataList[position].dataNameCreator
        holder.itemActor.text = dataList[position].dataActor
        holder.itemEmail.text = dataList[position].dataEmail
        holder.itemCompany.text = dataList[position].dataCompany
        holder.itemAddress.text = dataList[position].dataAddress

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

    var itemOutgoingDate : TextView
    var itemOutgoingWeight : TextView
    var itemGradeSecondary : TextView
    var itemHandlingSecondary : TextView
    var itemHandlingFeeSecondary : TextView
    var itemLossProduct : TextView
    var itemSellingPriceSecondary : TextView
    var itemDistribution : TextView

    var itemNotes : TextView

    var itemCreator : TextView
    var itemActor : TextView
    var itemEmail : TextView
    var itemCompany : TextView
    var itemAddress : TextView

    init {
        itemLL = itemView.findViewById(R.id.itemDetailRecyclerViewLL)
        itemPID = itemView.findViewById(R.id.productIDSecondaryTV)
        itemCreateDate = itemView.findViewById(R.id.dateCreateSecondaryTV)
        itemArrivedDate = itemView.findViewById(R.id.arrivedDateTV)
        itemIncomingWeight = itemView.findViewById(R.id.incomingWeightSecondaryTV)

        itemOutgoingDate = itemView.findViewById(R.id.outgoingDateSecondaryTV)
        itemOutgoingWeight = itemView.findViewById(R.id.outgoingWeightSecondaryTV)
        itemGradeSecondary = itemView.findViewById(R.id.gradeSecodaryTV)
        itemHandlingSecondary = itemView.findViewById(R.id.handlingSecondaryTV)
        itemHandlingFeeSecondary = itemView.findViewById(R.id.handlingFeeSecondaryTV)
        itemLossProduct = itemView.findViewById(R.id.weightLossTV)
        itemSellingPriceSecondary = itemView.findViewById(R.id.sellingPriceSecondaryTV)
        itemDistribution = itemView.findViewById(R.id.distributionTV)

        itemNotes = itemView.findViewById(R.id.notesTV)

        itemCreator = itemView.findViewById(R.id.creatorTV)
        itemActor = itemView.findViewById(R.id.actorTV)
        itemEmail = itemView.findViewById(R.id.emailTV)
        itemCompany = itemView.findViewById(R.id.companyTV)
        itemAddress = itemView.findViewById(R.id.addressTV)
    }
}