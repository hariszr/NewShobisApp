package com.example.loginandsignupfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.loginandsignupfirebase.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    var imageURL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        if (bundle != null){
            binding.farmerDetail.text = bundle.getString("Farmer")
            binding.varietyDetail.text = bundle.getString("Variety")
            binding.weightDetail.text = bundle.getString("Weight")
//            imageURL = bundle.getString("image")!!
//            Glide.with(this).load(bundle.getString("image")).into(binding.imageDetail)
        }
    }
}