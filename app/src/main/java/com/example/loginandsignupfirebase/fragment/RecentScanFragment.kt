package com.example.loginandsignupfirebase.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginandsignupfirebase.ListAdapterRecent
import com.example.loginandsignupfirebase.ProfileActivity
import com.example.loginandsignupfirebase.R
import com.example.loginandsignupfirebase.ScanQRActivity
import com.example.loginandsignupfirebase.databinding.FragmentRecentScanBinding
import com.example.loginandsignupfirebase.model.DataClassNewAdd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RecentScanFragment : Fragment() {
    private var binding: FragmentRecentScanBinding? = null
    private lateinit var dataList: ArrayList<DataClassNewAdd>
    private lateinit var adapter: ListAdapterRecent
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    var eventListener: ValueEventListener? = null
    private lateinit var dialog : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecentScanBinding.inflate(inflater, container, false)
        val view = binding?.root
        // Di sini Anda dapat mengakses komponen dalam layout menggunakan ViewBinding
        // Misalnya: binding.textView.text = "Hello World!"

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

//        val scanResult = arguments?.getString("scanResult")
//        if (!scanResult.isNullOrEmpty()) {
//            // Lakukan apa yang perlu Anda lakukan dengan data scanResult di sini
//            // Misalnya, tampilkan data dalam RecyclerView
//            // ...
//
//            // Buka detailed activity berdasarkan data scanResult
//            openDetailedActivity(scanResult)
//        }

        showAndClickList()
//        emptyInformation()

        binding!!.scanFAB.setOnClickListener {
            checkDataUser()
        }


        return view
    }

    private fun checkDataUser() {
        databaseReference.child(firebaseAuth.uid.toString()).child("Profile Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val actor = dataSnapshot.child("levelUser").getValue(String::class.java)

                    println("get actor : ${actor.toString()}")

                    startActivity(Intent(requireContext(), ScanQRActivity::class.java))

                } else {
                    dialog = AlertDialog.Builder(requireContext())
                        .setTitle("User Profile is Empty")
                        .setMessage("Please, complete your user profile first!")
                        .setCancelable(true)
                        .setPositiveButton("Yes") {dialogInterface, it ->
                            startActivity(Intent(requireContext(), ProfileActivity::class.java))
                        }
                        .setNegativeButton("No") {dialogInterface, it ->
                            dialogInterface.cancel()
                        }
                        .show()
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Check User for Scan Feature : ${error.message}")
                Log.e("Check User for Scan Feature", "Error when check actor: ${error.message}")
            }
        })
    }

//    private fun openDetailedActivity(scanResult: String) {
//        val intent = Intent(requireContext(), DetailRecentActivity::class.java)
//        intent.putExtra("scanResult", scanResult)
//        startActivity(intent)
//    }

    private fun emptyInformation() {
        if (dataList.isEmpty()) {
            binding?.emptyTextView?.visibility = View.VISIBLE
            binding?.uploadDataIV?.visibility = View.VISIBLE
        } else {
            binding?.emptyTextView?.visibility = View.GONE
            binding?.uploadDataIV?.visibility = View.GONE
        }
    }

    private fun showAndClickList() {
        val gridLayoutManager= GridLayoutManager(context, 1)
        binding?.listTraceRecyclerView?.layoutManager = gridLayoutManager

        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setCancelable(false)
            ?.setView(R.layout.layout_progress)
        val dialog = builder?.create()
        dialog?.show()

        //  val pid = databaseReference!!.get().

        dataList = ArrayList()
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = false
        binding?.listTraceRecyclerView?.layoutManager = linearLayoutManager

        adapter = ListAdapterRecent(this, dataList)
        binding?.listTraceRecyclerView?.adapter = adapter
         val databaseReferenceChild = databaseReference?.child(firebaseAuth.uid.toString())?.child("Recent Scan")
        dialog?.show()


        eventListener = databaseReferenceChild!!.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children){
                    val dataClassNewAdd = itemSnapshot.getValue(DataClassNewAdd::class.java)
                    if (dataClassNewAdd != null) {
                        dataClassNewAdd.key = itemSnapshot.key
                    }
                    if (dataClassNewAdd != null){
                        dataList.add(dataClassNewAdd)
                    }
                }
                dataList.sortByDescending { it.dataDateCreate }
                binding?.listTraceRecyclerView?.setHasFixedSize(true)
                adapter.notifyDataSetChanged()
                dialog?.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog?.dismiss()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Jangan lupa untuk menghapus referensi ViewBinding saat fragmen dihancurkan
        // dengan mengosongkan variabel lateinit
        binding = null
    }
}

