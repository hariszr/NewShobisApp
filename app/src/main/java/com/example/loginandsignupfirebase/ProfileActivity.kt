package com.example.loginandsignupfirebase

import android.app.AlertDialog.Builder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.loginandsignupfirebase.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var builder: Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        builder = Builder(this)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.backProfileIV.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.editProfileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))
        }

        binding.signOutBtn.setOnClickListener {
            builder.setTitle("Sign Out!")
                .setMessage("Are you sure to Sign Out?")
                .setCancelable(false)
                .setNegativeButton("No") {dialogInterface, it ->
                    dialogInterface.cancel()
            }
                .setPositiveButton("Yes") {dialogInterface, it ->
                    firebaseAuth.signOut()
                    startActivity(Intent(this, SignInActivity::class.java))
                    Toast.makeText(this, "Sign Out Successfully", Toast.LENGTH_SHORT).show()
                    finish()
            }
                    .show()
        }
    }
}