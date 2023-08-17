package com.example.loginandsignupfirebase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginandsignupfirebase.R
import com.example.loginandsignupfirebase.model.DataClassSummary
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ListAdapterSummary (private val context: Context, private val dataList:List<DataClassSummary>):
    RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_summary, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val list = dataList[position]
        val customFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")) as DecimalFormat
        val symbols = customFormat.decimalFormatSymbols
        symbols.currencySymbol = "Rp. "
        customFormat.decimalFormatSymbols = symbols
        customFormat.maximumFractionDigits = 0 // Menghilangkan angka desimal

        val formattedPurchasePrice = customFormat.format(list.purchasePrice)
        val formattedHandlingFee = customFormat.format(list.handlingFee)
        val formatSellingPrice = customFormat.format(list.sellingPrice)
        val formatPurchaseCapital = customFormat.format(list.purchaseCapital)
        val formatTotalHandlingFee = customFormat.format(list.totalHandlingFee)
        val formatCapital = customFormat.format(list.capital)
        val formatIncome = customFormat.format(list.income)
        val formatProfit = customFormat.format(list.profit)

        holder.itemArrivalDate.text = dataList[position].dateIn
        holder.itemVariety.text = dataList[position].variety
        holder.itemPID.text = dataList[position].pid
        holder.itemOutgoingDate.text = dataList[position].dateOut
        holder.itemInWeight.text = dataList[position].weightIn.toString()
        holder.itemPurchasePrice.text = formattedPurchasePrice
        holder.itemHandlingFee.text = formattedHandlingFee
        holder.itemOutWeight.text = dataList[position].weightOut.toString()
        holder.itemSellingPrice.text = formatSellingPrice
        holder.itemPurchaseCapital.text = formatPurchaseCapital
        holder.itemTotalHandlingFee.text = formatTotalHandlingFee
        holder.itemCapital.text = formatCapital
        holder.itemIncome.text = formatIncome
        holder.itemProfit.text = formatProfit
    }
}

class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
    var itemTable: TableLayout
    var itemArrivalDate: TextView
    var itemVariety: TextView
    var itemPID: TextView
    var itemOutgoingDate: TextView
    var itemInWeight: TextView
    var itemPurchasePrice: TextView
    var itemHandlingFee: TextView
    var itemOutWeight: TextView
    var itemSellingPrice: TextView
    var itemPurchaseCapital: TextView
    var itemTotalHandlingFee: TextView
    var itemCapital: TextView
    var itemIncome: TextView
    var itemProfit: TextView

    init {
        itemTable = itemView.findViewById(R.id.itemSummaryRV)
        itemArrivalDate = itemView.findViewById(R.id.arrivalDateTV)
        itemVariety = itemView.findViewById(R.id.shallotVarietyTV)
        itemPID = itemView.findViewById(R.id.productIDTV)
        itemOutgoingDate = itemView.findViewById(R.id.outgoingDateTV)
        itemInWeight = itemView.findViewById(R.id.incomingWeightTV)
        itemPurchasePrice = itemView.findViewById(R.id.purchasePriceTV)
        itemHandlingFee = itemView.findViewById(R.id.handlingFeeTV)
        itemOutWeight = itemView.findViewById(R.id.outgoingWeightTV)
        itemSellingPrice = itemView.findViewById(R.id.sellingPriceTV)
        itemPurchaseCapital = itemView.findViewById(R.id.purchaseCapitalTV)
        itemTotalHandlingFee = itemView.findViewById(R.id.totalHandlingFeeTV)
        itemCapital = itemView.findViewById(R.id.capitalTV)
        itemIncome = itemView.findViewById(R.id.incomeTV)
        itemProfit = itemView.findViewById(R.id.profitTV)
    }
}