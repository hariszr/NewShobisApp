package com.example.loginandsignupfirebase

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.loginandsignupfirebase.databinding.ActivityProfileEditBinding
import com.example.loginandsignupfirebase.model.UserModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage : FirebaseStorage
    private var selectedImg : Uri? = null
    private var imageUrl : Uri? = null
    private lateinit var dialog : AlertDialog.Builder
    private lateinit var databaseReference: DatabaseReference

    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.fullNameProfileET.text!!.isEmpty()) {
                dialog.setTitle("Profile is empty!")
                    .setMessage("Please, user profile can't be empty ")
                    .setCancelable(false)
                    .setNegativeButton("Yes") {dialogInterface, it ->
                        dialogInterface.cancel()
                    }
                    .show()
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        dialog = AlertDialog.Builder(this)
            .setMessage("Updating Profile...")
            .setCancelable(false)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        val auth = firebaseAuth.currentUser

        binding.fullNameProfileET.setText(auth?.displayName)
        displayDropDownGender()
        displayEditProfile()

        binding.editFAB.setOnClickListener {
//            val intent = Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            startActivityForResult(intent,1)
            alertPhotoProfileEdit()
            }

        saveBtn(imageUrl.toString())

        binding.backBtn.setOnClickListener {
            checkCloseBtn()
        }
    }

    private fun displayDropDownGender() {
        val itemsGender = listOf("Male", "Female")
        val adapterGender = ArrayAdapter(this, R.layout.list_item_dropdown, itemsGender)
        binding.dropDownGender.setAdapter(adapterGender)

        val itemsLevel = listOf("Land Trader", "Collectors", "Wholesalers", "Traditional Central Market", "Traditional Market", "Modern Market", "E-Commerce", "Consumers")
        val adapterLevel = ArrayAdapter(this, R.layout.list_item_dropdown, itemsLevel)
        binding.dropDownLevelUser.setAdapter(adapterLevel)
    }

    private fun alertPhotoProfileEdit() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change photo profile")
        builder.setMessage("Edit or delete photo profile")
        builder.setCancelable(true)

        builder.setPositiveButton("edit") {_, _ ->
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        builder.setNegativeButton("delete") {_, _ ->
            deletePhotoProfile()
        }
            .show()
    }

    private fun deletePhotoProfile() {

        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        databaseReference.child(userID).child("Profile Users").child("imageUrl").removeValue()
        binding.profileSIV.setImageDrawable(null)
        binding.profileSIV.setImageURI(null)

        checkCloseBtn()
//
//        if (binding.profileSIV.toString().isEmpty()) {
//            val drawableResId = binding.profileSIV.drawable.constantState
//
//            binding.profileSIV.setOnClickListener {
//                drawableResId?.let { resId ->
//                    binding.profileSIV.setImageResource(resId.hashCode())
//                }
//            }
//        }

//        val defaultDrawable = binding.profileSIV.drawable
//        binding.profileSIV.setImageDrawable(defaultDrawable)

    }

    private fun checkCloseBtn() {
        if (binding.fullNameProfileET.text!!.isEmpty()) {
            dialog.setTitle("Profile is empty!")
                .setMessage("Please, user profile can't be empty ")
                .setCancelable(true)
                .setNegativeButton("Yes") {dialogInterface, it ->
                    dialogInterface.cancel()
                }
                .show()
        } else {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    private fun displayEditProfile() {
        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        val itemsGender = listOf("Male", "Female")
        val adapterGender = ArrayAdapter(this, R.layout.list_item_dropdown, itemsGender)

        databaseReference.child(userID).child("Profile Users").get()
            .addOnSuccessListener {

                val levelUser = it.child("levelUser").value?.toString().orEmpty()
                val fullName = it.child("fullName").value?.toString().orEmpty()
                val gender = it.child("gender").value?.toString().orEmpty()
                val imageUrl = it.child("imageUrl").value?.toString().orEmpty()
                val phone = it.child("phone").value?.toString().orEmpty()
                val address = it.child("address").value?.toString().orEmpty()

                if (levelUser.isNotBlank()) binding.dropDownLevelUser.setText(levelUser)
                if (fullName.isNotBlank()) binding.fullNameProfileET.setText(fullName)
                if (gender.isNotBlank()) {
                    binding.dropDownGender.setText(gender)
                    binding.dropDownGender.setAdapter(adapterGender)
                }
                if (imageUrl.isNotBlank()) {
                    Glide.with(this).load(imageUrl).into(binding.profileSIV)
                    saveBtn(imageUrl)
                }
                if (phone.isNotBlank()) binding.phoneProfileET.setText(phone)
                if (address.isNotBlank()) binding.addressProfileET.setText(address)

            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveBtn(imageUrl: String) {
        binding.saveProfileBtn.setOnClickListener {

            if (binding.dropDownLevelUser.text!!.isEmpty()) {
                binding.dropDownLevelUser.error = "Please enter your name"

            } else if (binding.fullNameProfileET.text!!.isEmpty()){
                binding.fullNameProfileET.error = "Please enter your name"

            } else if (binding.dropDownGender.text!!.isEmpty()) {
                binding.dropDownGender.error = "Please enter your phone"

            } else if (binding.phoneProfileET.text!!.isEmpty()) {
                binding.phoneProfileET.error = "Please enter your phone"

            } else if (binding.addressProfileET.text!!.isEmpty()) {
                binding.phoneProfileET.error = "Please enter your address"

            }
//            else if (selectedImg == null || imageUrl == null) {
//                Toast.makeText(this, "Please Selected your photo", Toast.LENGTH_SHORT).show()
//            }
            uploadData(imageUrl)
//                updateProfile()
        }
    }
    private fun updateProfile() {
//        val email = binding.emailProfileET.text.toString()
        val fullName = binding.fullNameProfileET.text.toString()
        val phone = binding.phoneProfileET.text.toString()
        val address = binding.addressProfileET.text.toString()

        val editMap = mapOf(
//            "email" to email,
            "fullName" to fullName,
            "phone" to phone,
            "address" to address
        )

        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        databaseReference.child(userID).child("Profile Users").updateChildren(editMap)
        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    private fun uploadData(imageUrl: String) {
        if (selectedImg != null) {
            val reference = storage.reference.child("Profile").child(Date().time.toString())
            reference.putFile(selectedImg!!).addOnCompleteListener{
                if (it.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener { task ->
                        uploadInfo(task.toString())
                    }
                }
            }
        } else {
            uploadInfo(imageUrl)
        }

    }

    private fun uploadInfo(imageUrl: String) {

        val user = UserModel(
            firebaseAuth.uid.toString(),
            firebaseAuth.currentUser?.email.toString(),
            binding.fullNameProfileET.text.toString(),
            binding.dropDownLevelUser.text.toString(),
            binding.dropDownGender.text.toString(),
            binding.phoneProfileET.text.toString(),
            binding.addressProfileET.text.toString(),
            imageUrl)


        database.reference.child("users")
            .child(firebaseAuth.uid.toString())
            .child("Profile Users")
            .setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {

            if (data.data != null) {
                selectedImg = data.data!!
                binding.profileSIV.setImageURI(selectedImg)

//                Toast.makeText(this, "Upload image profile success", Toast.LENGTH_SHORT).show()
            }
        }
    }
}