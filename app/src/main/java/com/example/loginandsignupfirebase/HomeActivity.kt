package com.example.loginandsignupfirebase

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.loginandsignupfirebase.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef : DatabaseReference
    private lateinit var firebaseRef2 : DatabaseReference
    private lateinit var dialog : AlertDialog
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

        firebaseRef = Firebase.database.reference
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRef2 = FirebaseDatabase.getInstance().getReference("users")

        fetchUserDataHome()

        binding.scanCV.setOnClickListener {
            checkDataUser()
        }

        binding.profileSIV.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.createNewCV.setOnClickListener {
            checkActor()
        }
        binding.shTraceabilityCV.setOnClickListener {
            startActivity(Intent(this, TraceabilityListActivity::class.java))
        }

        binding.summaryCV.setOnClickListener {
            checkActorSumamry()
        }
    }

    private fun checkDataUser() {
        firebaseRef2.child(firebaseAuth.uid.toString()).child("Profile Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val actor = dataSnapshot.child("levelUser").getValue(String::class.java)

                    println("get actor : ${actor.toString()}")

                    startActivity(Intent(this@HomeActivity, ScanQRActivity::class.java))

                } else {
                    dialog = AlertDialog.Builder(this@HomeActivity)
                        .setTitle("User Profile is Empty")
                        .setMessage("Please, complete your user profile first!")
                        .setCancelable(true)
                        .setPositiveButton("Yes") {dialogInterface, it ->
                            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
                        }
                        .setNegativeButton("No") {dialogInterface, it ->
                            dialogInterface.cancel()
                        }
                        .show()
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, error.message, Toast.LENGTH_SHORT).show()
                Log.e("Check User for Scan Feature", "Error when check actor: ${error.message}")
            }
        })
    }

    fun checkActor() {

        firebaseRef2.child(firebaseAuth.uid.toString()).child("Profile Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val actor = dataSnapshot.child("levelUser").getValue(String::class.java)

                    println("get actor : ${actor.toString()}")

                    if (actor == "Pasar Induk" || actor == "Pasar Tradisional" || actor == "Pasar Modern" || actor == "E-Commerce") {
                        dialog = AlertDialog.Builder(this@HomeActivity)
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
                        dialog = AlertDialog.Builder(this@HomeActivity)
                            .setTitle("Consumer and UMKM")
                            .setMessage("You are not allowed to access this")
                            .setCancelable(true)
                            .setPositiveButton("Close") {dialogInterface, it ->
                                dialogInterface.cancel()
                            }
                            .show()
                        return
                    } else {
                        startActivity(Intent(this@HomeActivity, NewTraceabilityActivity::class.java))
                    }
                } else {
                    dialog = AlertDialog.Builder(this@HomeActivity)
                        .setTitle("User Profile is Empty")
                        .setMessage("Please, complete your user profile first!")
                        .setCancelable(true)
                        .setPositiveButton("Yes") {dialogInterface, it ->
                            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
                        }
                        .setNegativeButton("No") {dialogInterface, it ->
                            dialogInterface.cancel()
                        }
                        .show()
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, error.message, Toast.LENGTH_SHORT).show()
                Log.e("Check Actor", "Error when check actor: ${error.message}")
            }
        })
    }

    fun checkActorSumamry() {

        firebaseRef2.child(firebaseAuth.uid.toString()).child("Profile Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val actor = dataSnapshot.child("levelUser").getValue(String::class.java)

                    println("get actor : ${actor.toString()}")

                    if (actor == "Pasar Induk" || actor == "Pasar Tradisional" || actor == "Pasar Modern" || actor == "E-Commerce") {
                        dialog = AlertDialog.Builder(this@HomeActivity)
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
                        dialog = AlertDialog.Builder(this@HomeActivity)
                            .setTitle("Consumer and UMKM")
                            .setMessage("You are not allowed to access this")
                            .setCancelable(true)
                            .setPositiveButton("Close") {dialogInterface, it ->
                                dialogInterface.cancel()
                            }
                            .show()
                        return
                    } else {
                        startActivity(Intent(this@HomeActivity, SummaryActivity::class.java))
                    }
                } else {
                    dialog = AlertDialog.Builder(this@HomeActivity)
                        .setTitle("User Profile is Empty")
                        .setMessage("Please, complete your user profile first!")
                        .setCancelable(true)
                        .setPositiveButton("Yes") {dialogInterface, it ->
                            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
                        }
                        .setNegativeButton("No") {dialogInterface, it ->
                            dialogInterface.cancel()
                        }
                        .show()
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, error.message, Toast.LENGTH_SHORT).show()
                Log.e("Check Actor", "Error when check actor: ${error.message}")
            }
        })
    }

    private fun fetchUserDataHome() {

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
            .setView(R.layout.layout_progress)
        val dialog = builder.create()
        dialog.show()

        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        firebaseRef.child("users").child(userID).child("Profile Users").get()
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