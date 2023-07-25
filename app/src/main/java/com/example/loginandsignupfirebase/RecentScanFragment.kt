package com.example.loginandsignupfirebase

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null

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


        showAndClickList()
//        emptyInformation()

        return view
    }

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
        databaseReference = databaseReference?.child(firebaseAuth.uid.toString())?.child("Recent Scan")
        dialog?.show()


        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener{
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