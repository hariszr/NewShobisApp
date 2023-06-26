package com.example.loginandsignupfirebase

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.loginandsignupfirebase.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var builder: AlertDialog.Builder
    private lateinit var firebaseAuth: FirebaseAuth
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

        binding.scanCV.setOnClickListener {
            startActivity(Intent(this, ScanQRActivity::class.java))
        }

        binding.profileSIV.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.createNewCV.setOnClickListener {
            startActivity(Intent(this, NewTraceabilityActivity::class.java))
        }
        binding.shTraceabilityCV.setOnClickListener {
            startActivity(Intent(this, TraceabilityListActivity::class.java))
        }
        firebaseAuth = FirebaseAuth.getInstance()
    }

}