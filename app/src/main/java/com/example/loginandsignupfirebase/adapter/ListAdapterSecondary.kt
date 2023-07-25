package com.example.loginandsignupfirebase

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot

class ListAdapterSecondary(private val context:android.content.Context, private val dataSnapshotList: List<DataSnapshot>): RecyclerView.Adapter<MyViewHolderDetail>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderDetail {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_traceability, parent, false)
        return MyViewHolderDetail(view)
    }

    override fun getItemCount(): Int {
        return dataSnapshotList.size
    }

    override fun onBindViewHolder(holder: MyViewHolderDetail, position: Int) {
//        Glide.with(context).load(dataList[position].dataQrCode).into(holder.itemRecQrCode)

        val childNodePID = dataSnapshotList[position].key

            val intent = Intent(context, DetailUploadActivity::class.java)

            intent.putExtra("PIDNode", childNodePID)

            context.startActivity(intent)
    }

}