package com.example.loginandsignupfirebase

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.loginandsignupfirebase.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var builder: AlertDialog.Builder
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseref : DatabaseReference
    private var backPressedTime = 0L
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            println("Exit 0")
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finishAffinity()
            } else {
                Toast.makeText(this@HomeActivity, "Press again to exit",
                    Toast.LENGTH_SHORT).show()
            }
            backPressedTime = System.currentTimeMillis()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        firebaseref = Firebase.database.reference

        fetchUserDataHome()

        binding.scanCV.setOnClickListener {
            startActivity(Intent(this, ScanQRActivity::class.java))
        }

        binding.profileSIV.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.createNewCV.setOnClickListener {
            startActivity(Intent(this, AddTraceabilityActivity::class.java))
        }
        binding.shTraceabilityCV.setOnClickListener {
            startActivity(Intent(this, TraceabilityListActivity::class.java))
        }
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun fetchUserDataHome() {

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
            .setView(R.layout.layout_progress)
        val dialog = builder.create()
        dialog.show()

        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        firebaseref.child("users").child(userID).child("Profile Users").get()
            .addOnSuccessListener {
                dialog.show()

                val fullName = it.child("fullName").value?.toString().orEmpty()
                val imageUrl = it.child("imageUrl").value?.toString().orEmpty()

                if (fullName.isNotBlank()) binding.fullNameHomeTV.text = fullName
                if (imageUrl.isNotBlank()) Glide.with(this).load(imageUrl).into(binding.profileSIV)

                dialog.dismiss()

            }. addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

}