package com.example.loginandsignupfirebase

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.loginandsignupfirebase.model.DataClassNewAdd
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ListAdapter(private val context:android.content.Context, private val dataList:List<DataClassNewAdd>): RecyclerView.Adapter<MyViewHolder>() {
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_traceability, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = dataList[position]
        Glide.with(context).load(dataList[position].dataPicProduct).into(holder.itemRecPictProduct)
        holder.itemRecPID.text = item.key
        holder.itemRecVariety.text = item.dataVariety
//        holder.itemRecWeight.text = item.dataWeight

        holder.itemRec.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)

            val childNodePID = dataList[position].key
            intent.putExtra("PIDNode", childNodePID)

            intent.putExtra("QrCodeUpdate", dataList[holder.adapterPosition].dataQrCodeUpdate)
            intent.putExtra("PictProduct", dataList[holder.adapterPosition].dataPicProduct)

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

            context.startActivity(intent)

            MaterialAlertDialogBuilder(holder.itemView.context)


        }
        holder.itemRec.setOnLongClickListener {
        MaterialAlertDialogBuilder(holder.itemView.context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") {_,_ ->
                firebaseAuth = FirebaseAuth.getInstance()
                val firebaseRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.uid.toString())?.child("pid")
                firebaseRef?.child(item.key.toString())?.removeValue()
            }
            .setNegativeButton("No") {_,_ ->

            }
            .show()

            return@setOnLongClickListener true
        }
    }
}

class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var itemRec : CardView
    var itemRecPictProduct : ImageView
    var itemRecPID : TextView
//    var itemRecWeight : TextView
    var itemRecVariety : TextView

    init {
        itemRec = itemView.findViewById(R.id.itemTraceRecyclerView)
        itemRecPictProduct = itemView.findViewById(R.id.itemRecPictProductSIV)
        itemRecPID = itemView.findViewById(R.id.itemRecPIDTV)
//        itemRecWeight = itemView.findViewById(R.id.itemRecWeightTV)
        itemRecVariety = itemView.findViewById(R.id.itemRecVarietyTV)
    }
}