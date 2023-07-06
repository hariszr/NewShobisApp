package com.example.loginandsignupfirebase

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
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
                Toast.makeText(
                    this@SignInActivity, "Press again to exit",
                    Toast.LENGTH_SHORT
                ).show()
            }
            backPressedTime = System.currentTimeMillis()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.forgotPassTV.setOnClickListener {
            changePasswordTask()
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty()) {
                binding.emailLayout.error = null
                if(pass.isNotEmpty()) {
                    binding.passwordLayout.error = null
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
                    binding.passwordLayout.error = "Password can't be empty"
//                    if (pass.isNotEmpty()) binding.passwordLayout.error = null
                }

            } else {
                binding.emailLayout.error = "Email can't be empty"
//                if (email.isNotEmpty()) binding.emailLayout.error = null
//                Toast.makeText(this, "Empty Fields Are not Allowed !", Toast.LENGTH_SHORT).show()
            }
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

// shared preference di smartphone, untuk menyimpan autentikasi pengguna di masing2 smartphone
override fun onStart() {
    super.onStart()

    if (firebaseAuth.currentUser != null) {
        startActivity(Intent(this, HomeActivity::class.java))
//        Toast.makeText(this, "Hi Bang", Toast.LENGTH_SHORT).show()
    }
}
}