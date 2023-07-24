package com.example.loginandsignupfirebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.loginandsignupfirebase.databinding.FragmentRecentScanBinding
import com.example.loginandsignupfirebase.databinding.FragmentUploadBinding
import com.example.loginandsignupfirebase.model.DataClassNewAdd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecentScanFragment : Fragment() {
    private var binding: FragmentRecentScanBinding? = null
    private lateinit var dataList: ArrayList<DataClassNewAdd>
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


//        showAndClickList()
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
}