package com.example.loginandsignupfirebase

import android.app.AlertDialog.Builder
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.loginandsignupfirebase.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var builder: Builder
    private lateinit var firebaseref : DatabaseReference

    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        println("INIT")
        builder = Builder(this)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseref = Firebase.database.reference

        fetchUserData()

        binding.changePasswordBtn.setOnClickListener {
            changePasswordTask()
        }

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
                    finishAffinity()
            }
                .show()
        }
    }

    private fun changePasswordTask() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_changepass, null)
        val userEmail = view.findViewById<EditText>(R.id.editBox)
        val auth = firebaseAuth.currentUser
        userEmail.setText(auth?.email)

        builder.setView(view)
        val dialog = builder.create()

        view.findViewById<Button>(R.id.sendBtn).setOnClickListener {
            compareEmail(userEmail)
            dialog.dismiss()
        }

        view.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
    }

    private fun compareEmail(email: EditText) {
        if (email.text.isEmpty()){
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            return
        }
        firebaseAuth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener {task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
//            Toast.makeText(this, "your email is wrong", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserData() {

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
            .setView(R.layout.layout_progress)
        val dialog = builder.create()
        dialog.show()

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val auth = firebaseAuth.currentUser
        binding.emailTV.text = auth?.email

        firebaseref.child("users").child(userID).child("Profile Users").get()
            .addOnSuccessListener {

                dialog.show()
//                val email = it.child("email").value?.toString().orEmpty()
                val fullName = it.child("fullName").value?.toString().orEmpty()
                val imageUrl = it.child("imageUrl").value?.toString().orEmpty()
                val phone = it.child("phone").value?.toString().orEmpty()
                val address = it.child("address").value?.toString().orEmpty()

                if (fullName.isNotBlank()) binding.fullNameTV.text = fullName
                if (imageUrl.isNotBlank()) Glide.with(this).load(imageUrl).into(binding.profileShowSIV)
                if (phone.isNotBlank()) binding.phoneTV.text = phone
                if (address.isNotBlank()) binding.addressTV.text = address

                println("nama :${address}")

                dialog.dismiss()
            }. addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}