package com.example.loginandsignupfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.loginandsignupfirebase.adapter.ListAdapterSummary
import com.example.loginandsignupfirebase.databinding.ActivitySummaryBinding
import com.example.loginandsignupfirebase.model.DataClassSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySummaryBinding
    private lateinit var dataList: ArrayList<DataClassSummary>
    private lateinit var adapter: ListAdapterSummary
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    var eventListener: ValueEventListener? = null

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
}