package com.example.loginandsignupfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.example.loginandsignupfirebase.databinding.ActivityHomeBinding
import com.example.loginandsignupfirebase.databinding.ActivityNewTraceabilityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.values
import java.text.DateFormat
import java.util.Calendar

class NewTraceabilityActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNewTraceabilityBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firebaseref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTraceabilityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseref = FirebaseDatabase.getInstance().getReference("Product")

        binding.closeBtn.setOnClickListener {
            finish()
        }
        binding.createTraceabilityBtn.setOnClickListener {
            saveData()
        }
    }

    private fun saveData(){

        val pid = firebaseref.push().key!!

        val farmer = binding.farmerEt.text.toString()
        val variety = binding.varietytEt.text.toString()
        val weight = binding.weightEt.text.toString()

        val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        binding.farmerEt.addTextChangedListener {
            if (it!!.count() > 0)
                binding.farmerLayout.error = null
        }

        binding.varietytEt.addTextChangedListener {
            if (it!!.count() > 0)
                binding.varietyLayout.error = null
        }

        binding.weightEt.addTextChangedListener {
            if (it!!.count() > 0)
                binding.weightLayout.error = null
        }

        if (farmer.isEmpty())
            binding.farmerLayout.error = "Please enter Name"
        if (variety.isEmpty())
            binding.varietyLayout.error = "Please enter Variety"
        if (weight.isEmpty())
            binding.weightLayout.error = "Please enter Weight"

        if (farmer.isNotEmpty() && variety.isNotEmpty() && weight.isNotEmpty()) {

            val builder = AlertDialog.Builder(this@NewTraceabilityActivity)
            builder.setCancelable(false)
                .setView(R.layout.layout_progress)
            val dialog = builder.create()
            dialog.show()

            val dataClass = DataClass(pid, farmer, variety, weight, currentDate)

            FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.uid.toString()).child(pid)
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
}