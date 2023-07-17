package com.example.loginandsignupfirebase

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.loginandsignupfirebase.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    var QrCode = ""

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backProductListIV.setOnClickListener {
            startActivity(Intent(this, TraceabilityListActivity::class.java))
            finish()
        }
        progresLoad()
    }

    private fun progresLoad() {
        val builder = AlertDialog.Builder(this@DetailActivity)
        builder.setCancelable(false).setView(R.layout.layout_progress)
        val dialog = builder.create()
        dialog.show()
        showData()
        dialog.dismiss()    }

    private fun showData() {
        val bundle = intent.extras
        if (bundle != null){
            QrCode = bundle.getString("QrCode")!!
            Glide.with(this).load(bundle.getString("QrCode")).into(binding.qrCodeDetailIV)

            binding.productIDTV.text = bundle.getString("PID")
            binding.varietyTV.text = bundle.getString("Variety")
            binding.weightTV.text = bundle.getString("Weight")
            binding.gradeTV.text = bundle.getString("Grade")
            binding.priceTV.text = bundle.getString("Price")

            binding.farmerTV.text = bundle.getString("Farmer")
            binding.dayTV.text = bundle.getString("Day")
            binding.areaTV.text = bundle.getString("Area")
            binding.fertilizerTV.text = bundle.getString("Fertilizer")
            binding.pesticidesTV.text = bundle.getString("Pesticides")

            binding.dateCreateTV.text = bundle.getString("Date")
            binding.notesTV.text = bundle.getString("Note")

        }    }
}