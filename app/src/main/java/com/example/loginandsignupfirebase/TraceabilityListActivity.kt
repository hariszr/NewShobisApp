package com.example.loginandsignupfirebase

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.loginandsignupfirebase.adapter.FragmentAdapter
import com.example.loginandsignupfirebase.databinding.ActivityTraceabilityListBinding
import com.example.loginandsignupfirebase.model.DataClassNewAdd
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TraceabilityListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTraceabilityListBinding
//    private lateinit var dataList: ArrayList<DataClassNewAdd>
//    private lateinit var adapter: ListAdapter
//    private lateinit var firebaseAuth : FirebaseAuth
//    private lateinit var firebaseDatabase: FirebaseDatabase
//    var databaseReference: DatabaseReference? = null
//    var eventListener: ValueEventListener? = null

    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(Intent(this@TraceabilityListActivity, HomeActivity::class.java))
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraceabilityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        var viewPager = findViewById(R.id.viewPager) as ViewPager
        var tabLayout = findViewById(R.id.tabLayout) as TabLayout

        val fragmentAdapter = FragmentAdapter(supportFragmentManager)
        fragmentAdapter.addFragment(RecentScanFragment(), "Recent Scan")
        fragmentAdapter.addFragment(UploadFragment(), "Your Upload")

        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)

//        firebaseAuth = FirebaseAuth.getInstance()
//        firebaseDatabase = FirebaseDatabase.getInstance()
//        databaseReference = FirebaseDatabase.getInstance().getReference("users")

//        binding.addFAB.setOnClickListener {
//            startActivity(Intent(this, NewTraceabilityActivity::class.java))
//            finish()
//        }

        binding.backProductListIV.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
//        showAndClickList()
    }

//    private fun showAndClickList() {
//        val gridLayoutManager= GridLayoutManager(this, 1)
//        binding.listTraceRecyclerView.layoutManager = gridLayoutManager
//
//        val builder = AlertDialog.Builder(this)
//        builder.setCancelable(false)
//            .setView(R.layout.layout_progress)
//        val dialog = builder.create()
//        dialog.show()
//
//        //  val pid = databaseReference!!.get().
//
//        dataList = ArrayList()
//        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        linearLayoutManager.reverseLayout = false
//        linearLayoutManager.stackFromEnd = false
//        binding.listTraceRecyclerView.layoutManager = linearLayoutManager
//
//        adapter = ListAdapter(this, dataList)
//        binding.listTraceRecyclerView.adapter = adapter
//        databaseReference = databaseReference?.child(firebaseAuth.uid.toString())?.child("pid")
//        dialog.show()
//
//
//        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener{
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onDataChange(snapshot: DataSnapshot) {
//                dataList.clear()
//                for (itemSnapshot in snapshot.children){
//                    val dataClassNewAdd = itemSnapshot.getValue(DataClassNewAdd::class.java)
//                    if (dataClassNewAdd != null) {
//                        dataClassNewAdd.key = itemSnapshot.key
//                    }
//                    if (dataClassNewAdd != null){
//                        dataList.add(dataClassNewAdd)
//                    }
//                }
//                dataList.sortByDescending { it.dataDateCreate }
//                binding.listTraceRecyclerView.setHasFixedSize(true)
//                adapter.notifyDataSetChanged()
//                dialog.dismiss()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                dialog.dismiss()
//            }
//        })    }
}