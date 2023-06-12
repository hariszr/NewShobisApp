package com.example.loginandsignupfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.loginandsignupfirebase.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

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
    }
}