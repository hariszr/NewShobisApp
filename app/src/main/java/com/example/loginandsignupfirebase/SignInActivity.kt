package com.example.loginandsignupfirebase

import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.loginandsignupfirebase.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var backPressedTime = 0L
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            println("Exit 0")
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finishAffinity()
            } else {
                Toast.makeText(this@SignInActivity, "Press again to exit",
                    Toast.LENGTH_SHORT).show()
            }
            backPressedTime = System.currentTimeMillis()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        Toast.makeText(this, "Sign In Successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // shared preference di smartphone, untuk menyimpan autentikasi pengguna di masing2 smartphone
    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null){
            startActivity(Intent(this, HomeActivity::class.java))
            Toast.makeText(this, "Hi Bang", Toast.LENGTH_SHORT).show()
        }
    }
}