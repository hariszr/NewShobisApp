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
import com.example.loginandsignupfirebase.ListAdapterUpload
import com.example.loginandsignupfirebase.NewTraceabilityActivity
import com.example.loginandsignupfirebase.ProfileActivity
import com.example.loginandsignupfirebase.R
import com.example.loginandsignupfirebase.databinding.FragmentUploadBinding
import com.example.loginandsignupfirebase.model.DataClassNewAdd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UploadFragment : Fragment() {

    private var binding: FragmentUploadBinding? = null
    private lateinit var dataList: ArrayList<DataClassNewAdd>
    private lateinit var adapter: ListAdapterUpload
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    var eventListener: ValueEventListener? = null
    private lateinit var dialog : AlertDialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        val view = binding?.root
        // Di sini Anda dapat mengakses komponen dalam layout menggunakan ViewBinding
        // Misalnya: binding.textView.text = "Hello World!"

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")


        showAndClickList()

        binding!!.addFAB.setOnClickListener {
            checkActor()
        }

        return view

    }

    private fun checkActor() {

        databaseReference.child(firebaseAuth.uid.toString()).child("Profile Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val actor = dataSnapshot.child("levelUser").getValue(String::class.java)

                    println("get actor : ${actor.toString()}")

                    if (actor == "Pasar Induk" || actor == "Pasar Tradisional" || actor == "Pasar Modern" || actor == "E-Commerce") {
                        dialog = AlertDialog.Builder(requireContext())
                            .setTitle("Market Level Actor")
                            .setMessage("You are not allowed to access this")
                            .setCancelable(true)
                            .setPositiveButton("Close") {dialogInterface, it ->
                                dialogInterface.cancel()
                            }
                            .show()
                        return
                    }

                    else if (actor == "Konsumen" || actor == "UMKM") {
                        dialog = AlertDialog.Builder(requireContext())
                            .setTitle("Consumer and UMKM")
                            .setMessage("You are not allowed to access this")
                            .setCancelable(true)
                            .setPositiveButton("Close") {dialogInterface, it ->
                                dialogInterface.cancel()
                            }
                            .show()
                        return
                    } else {
                        startActivity(Intent(requireContext(), NewTraceabilityActivity::class.java))
                    }
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
                println("Check Actor : ${error.message}")
                Log.e("Check Actor", "Error when check actor: ${error.message}")
            }
        })
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

        adapter = ListAdapterUpload(this, dataList)
        binding?.listTraceRecyclerView?.adapter = adapter
        val databaseReferenceChild = databaseReference.child(firebaseAuth.uid.toString()).child("pid")
        dialog?.show()


        eventListener = databaseReferenceChild.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                // Buat list baru untuk menampung data baru
                val newDataList = ArrayList<DataClassNewAdd>()

                // Ambil anak-anak dalam urutan terbalik
                val reversedChildren = snapshot.children.toList().reversed()

                for (itemSnapshot in reversedChildren){
                    val dataClassNewAdd = itemSnapshot.getValue(DataClassNewAdd::class.java)
                    if (dataClassNewAdd != null) {
                        dataClassNewAdd.key = itemSnapshot.key
                        // Tambahkan data baru ke newDataList
                        newDataList.add(dataClassNewAdd)
                    }
                }
                // Sorting newDataList
                dataList.sortByDescending { it.dataDateCreate }
                dataList.clear()
                // Ganti dataList dengan newDataList yang sudah diurutkan
                dataList.addAll(newDataList)
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