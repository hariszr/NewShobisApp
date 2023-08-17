package com.example.loginandsignupfirebase

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.loginandsignupfirebase.adapter.ListAdapterSummary
import com.example.loginandsignupfirebase.databinding.ActivitySummaryBinding
import com.example.loginandsignupfirebase.model.DataClassSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySummaryBinding
    private lateinit var dataList: ArrayList<DataClassSummary>
    private lateinit var adapter: ListAdapterSummary
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    var eventListener: ValueEventListener? = null

    private lateinit var database: FirebaseDatabase

    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(Intent(this@SummaryActivity, HomeActivity::class.java))
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        binding.backProductListIV.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        showSummary()

        val gridLayoutManager =GridLayoutManager(this, 1)
        binding.recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.layout_progress)
        val dialog = builder.create()
        dialog.show()

        dataList = ArrayList()


        adapter = ListAdapterSummary(this, dataList)
        binding.recyclerView.adapter = adapter
        val databaseReferenceChild = databaseReference.child(firebaseAuth.uid.toString()).child("summary")
        dialog.show()

        eventListener = databaseReferenceChild.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClassSummary = itemSnapshot.getValue(DataClassSummary::class.java)
                    if (dataClassSummary != null) {
                        dataList.add(dataClassSummary)
                    }
                }

                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })

    }

    private fun showSummary() {

        database = FirebaseDatabase.getInstance()

        val summaryRef = database.reference.child("users").child(firebaseAuth.uid.toString()).child("summary")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalCapital = 0
                var totalIncome = 0
                var totalProfit = 0

                for (childSnapshot in snapshot.children) {
                    val capital = childSnapshot.child("capital").getValue(Int::class.java) ?: 0
                    totalCapital += capital

                    val income = childSnapshot.child("income").getValue(Int::class.java) ?: 0
                    totalIncome += income

                    val profit = childSnapshot.child("profit").getValue(Int::class.java) ?: 0
                    totalProfit += profit
                }

                // Tampilkan total capital, income, dan profit
                val customFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")) as DecimalFormat
                val symbols = customFormat.decimalFormatSymbols
                symbols.currencySymbol = "Rp. "
                customFormat.decimalFormatSymbols = symbols
                customFormat.maximumFractionDigits = 0  // Mengatur jumlah digit desimal maksimum

                val formattedTotalCapital = customFormat.format(totalCapital)
                val formattedTotalIncome = customFormat.format(totalIncome)
                val formattedTotalProfit = customFormat.format(totalProfit)

                binding.totalCapitalTV.text = formattedTotalCapital
                binding.totalIncomeTV.text = formattedTotalIncome
                binding.totalProfitTV.text = formattedTotalProfit
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value summary.", error.toException())
            }
        }

        summaryRef.addListenerForSingleValueEvent(listener)
    }
}