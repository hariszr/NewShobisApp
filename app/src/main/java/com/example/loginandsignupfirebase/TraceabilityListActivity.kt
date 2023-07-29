package com.example.loginandsignupfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.viewpager.widget.ViewPager
import com.example.loginandsignupfirebase.adapter.FragmentAdapter
import com.example.loginandsignupfirebase.databinding.ActivityTraceabilityListBinding
import com.example.loginandsignupfirebase.fragment.RecentScanFragment
import com.example.loginandsignupfirebase.fragment.UploadFragment
import com.google.android.material.tabs.TabLayout

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

//        if (savedInstanceState == null) {
//            // Jika savedInstanceState == null, berarti ini adalah pembukaan pertama kali
//            // Lakukan transaksi fragment untuk menampilkan Fragment Upload
//            val fragmentUpload = UploadFragment()
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_Upload, fragmentUpload)
//                .commit()
//        }

//        val scanResult = intent.getStringExtra("scanResult")
//        if (!scanResult.isNullOrEmpty()) {
//            // Buka recentScan Fragment dengan data scanResult
//            val fragment = RecentScanFragment()
//            val bundle = Bundle()
//            bundle.putString("scanResult", scanResult)
//            fragment.arguments = bundle
//
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_Container, fragment, "recentScanFragmentTag")
//                .commit()
//        }

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

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
//        })
//        }
}