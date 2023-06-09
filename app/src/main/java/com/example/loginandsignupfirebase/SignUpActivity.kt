package com.example.loginandsignupfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginandsignupfirebase.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            signInPage()
        }
        binding.button.setOnClickListener {
            createNewUser()
        }
    }
    private fun createNewUser() {
        val email = binding.emailEt.text.toString()
        val pass = binding.passET.text.toString()
        val confirmPass = binding.confirmPassEt.text.toString()

        if (email.isNotEmpty()) {
            binding.emailLayout.error = null
            if (pass.isNotEmpty()) {
                binding.passwordLayout.error = null
                if (confirmPass.isNotEmpty()) {
                    binding.confirmPasswordLayout.error = null
                    if (pass == confirmPass) {

                        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val intent = Intent(this, SignInActivity::class.java)
                                    startActivity(intent)
                                    Toast.makeText(
                                        this,
                                        "Sign Up Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        it.exception.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.confirmPasswordLayout.error = "Confirm password is required"
                }
            } else {
                binding.passwordLayout.error = "Password is required"
//                    Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.emailLayout.error = "Email is required"
        }    }

    private fun signInPage() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}