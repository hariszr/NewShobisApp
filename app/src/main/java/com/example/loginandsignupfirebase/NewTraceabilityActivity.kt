package com.example.loginandsignupfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.loginandsignupfirebase.databinding.ActivityHomeBinding
import com.example.loginandsignupfirebase.databinding.ActivityNewTraceabilityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.Calendar

class NewTraceabilityActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNewTraceabilityBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTraceabilityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        binding.closeBtn.setOnClickListener {
            finish()
        }
        binding.createTraceabilityBtn.setOnClickListener {
            saveData()
        }
    }

    private fun saveData(){
        val builder = AlertDialog.Builder(this@NewTraceabilityActivity)
        builder.setCancelable(false)
            .setView(R.layout.layout_progress)
        val dialog = builder.create()
        dialog.show()

        val farmer = binding.farmerEt.text.toString()
        val variety = binding.varietytEt.text.toString()
        val weight = binding.weightEt.text.toString()
        val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        val dataClass = DataClass(farmer, variety, weight, currentDate)

        FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.uid.toString()).child("New Traceability")
            .setValue(dataClass).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }
}