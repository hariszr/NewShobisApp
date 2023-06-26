package com.example.loginandsignupfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.loginandsignupfirebase.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    var QrCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backProductListIV.setOnClickListener {
            startActivity(Intent(this, TraceabilityListActivity::class.java))
            finish()
        }

        val bundle = intent.extras
        if (bundle != null){
            binding.farmerDetail.text = bundle.getString("Farmer")
            binding.varietyDetail.text = bundle.getString("Variety")
            binding.weightDetail.text = bundle.getString("Weight")
//            QrCode = bundle.getString("QrCode")!!
//            Glide.with(this).load(bundle.getString("QrCode")).into(binding.qrCodeDetail)
        }
    }
}