package com.example.loginandsignupfirebase

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.loginandsignupfirebase.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var builder: AlertDialog.Builder
    private lateinit var firebaseAuth: FirebaseAuth
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    override fun onBackPressed() {
        if (backPressedTime + 4000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()
        } else {
            Toast.makeText(this, "Press back again to Exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}