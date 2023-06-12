package com.example.loginandsignupfirebase

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var dialog : AlertDialog.Builder
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = AlertDialog.Builder(this)
            .setMessage("Updating Profile...")
            .setCancelable(false)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        val auth = firebaseAuth.currentUser

        binding.emailProfileET.setText(auth?.email)
        binding.nameProfileET.setText(auth?.displayName)

        binding.editFAB.setOnClickListener {
//            val intent = Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            startActivityForResult(intent,1)

            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        binding.saveProfileBtn.setOnClickListener {

//            if (binding.emailProfileET.text!!.isEmpty()){
//                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
//                binding.emailProfileET.requestFocus()
//            } else if (binding.nameProfileET.text!!.isEmpty()){
//                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
//                binding.nameProfileET.requestFocus()
//            } else if (binding.ageProfileET.text!!.isEmpty()){
//                Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show()
//                binding.ageProfileET.requestFocus()
            if (selectedImg == null){
                Toast.makeText(this, "Please insert your photo", Toast.LENGTH_SHORT).show()
            } else
                uploadData()
        }

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun uploadData() {
        val reference = storage.reference.child("Profile").child(Date().time.toString())
        reference.putFile(selectedImg!!).addOnCompleteListener{
            if (it.isSuccessful) {
                reference.downloadUrl.addOnSuccessListener { task ->
                    uploadInfo(task.toString())
                }
            }
        }
    }

    private fun uploadInfo(imgUrl: String) {
        val user = UserModel(
            firebaseAuth.uid.toString(),
            firebaseAuth.currentUser?.email.toString(),
            binding.nameProfileET.text.toString(),
            binding.ageProfileET.text.toString(),
            imgUrl)


        database.reference.child("users")
            .child(firebaseAuth.uid.toString())
            .setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this, ProfileEditActivity::class.java))
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {

            if (data.data != null) {
                selectedImg = data.data!!
                binding.profileSIV.setImageURI(selectedImg)
                Toast.makeText(this, "Upload image profile success", Toast.LENGTH_SHORT).show()
            }
        }
    }
}