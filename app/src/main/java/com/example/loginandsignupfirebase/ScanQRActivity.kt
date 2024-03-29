package com.example.loginandsignupfirebase

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.loginandsignupfirebase.databinding.ActivityScanQractivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val CAMERA_REQUEST_CODE = 101

class ScanQRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanQractivityBinding
    private lateinit var codeScanner : CodeScanner
    private lateinit var firebaseAuth : FirebaseAuth

    private lateinit var firebaseRefServer: DatabaseReference
    private lateinit var firebaseRef : DatabaseReference

    private var count = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRefServer = FirebaseDatabase.getInstance().getReference("pid server")
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")

        setupPermissions()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startScanning() {
        val scannerView : CodeScannerView = binding.scannerCSV
        codeScanner = CodeScanner(this, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    if (it != null) {
                        Toast.makeText(this@ScanQRActivity,
                            "Scan result: ${it.text}", Toast.LENGTH_SHORT).show()
                        println("Hasil langsung: ${it.text}")
                        Log.i("Scan Result", "Scan Result : ${it.text}")
                        val getPID = it.text
                        initCopyAndShowDataToRecent(getPID)
//                        val addTraceabilityActivity = AddTraceabilityActivity()
//                        addTraceabilityActivity.initCopy()
                        val intent = Intent(this@ScanQRActivity, TraceabilityListActivity::class.java)
//                        intent.putExtra("scanResult", getPID)
                        startActivity(intent)
//                        startActivity(Intent(this@ScanQRActivity, TraceabilityListActivity::class.java))
                        finish()
                    }
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Toast.makeText(this@ScanQRActivity,
                        "Camera initialization error: ${it.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }

            scannerView.setOnClickListener {
                codeScanner.startPreview()
            }
        }
    }

    private fun initCopyAndShowDataToRecent(pid: String) {

        val sourceRef = firebaseRefServer.child(pid)
        val destinationRef = firebaseRef.child(firebaseAuth.uid.toString()).child("Recent Scan").child(pid)

        copyData(sourceRef, destinationRef) {
            // Panggil callback setelah semua data berhasil disalin
            println("Successfully copied data to server")
        }
    }

    private fun copyData(sourceRef: DatabaseReference, destinationRef: DatabaseReference, callback: () -> Unit) {
        sourceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                destinationRef.setValue(dataSnapshot.value) { databaseError, _ ->
                    if (databaseError == null) {
                        for (childSnapshot in dataSnapshot.children) {
                            val sourceChildRef = childSnapshot.ref
                            val destinationChildRef = destinationRef.child(childSnapshot.key!!)
                            copyData(sourceChildRef, destinationChildRef) {
                                // Panggil callback setelah semua data berhasil disalin
                                println("Successfully copied data to users")
                                callback.invoke()
                            }
                        }
                    } else {
                        println("Failed to copy data: ${databaseError.message}")
                        callback.invoke()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Failed to read data: ${databaseError.message}")
                callback.invoke()
            }
        })
    }

    private fun readAndSaveDataToFirebase(getPID: String) {
//        firebaseref.child(firebaseAuth.uid.toString()).child("pid").child(getPID)
//            .get().addOnSuccessListener {
//                println("Hasil: ${getPID}")
//                if (getPID.isNotEmpty()) {
//
//                    val imageURL = it.child("dataQrCode").value.toString()
//                    val variety = it.child("dataVariety").value.toString()
//                    val weight = it.child("dataWeight").value.toString()
//                    val grade = it.child("dataGrade").value.toString()
//                    val price = it.child("dataPrice").value.toString()
//
//
//                    val farmer = it.child("dataFarmer").value.toString()
//                    val day = it.child("dataDay").value.toString()
//                    val area = it.child("dataPlantingArea").value.toString()
//                    val fertilizer = it.child("dataFertilizer").value.toString()
//                    val pesticides = it.child("dataPesticides").value.toString()
//
//
//                    val dateCreate = it.child("dataDate").value.toString()
//
//                    val notes = it.child("dataNotes").value.toString()
//
//                    println("imageURL : $imageURL")
//
//                    Toast.makeText(this@ScanQRActivity, "Data Read Successfully", Toast.LENGTH_SHORT).show()
//
//                    val dataClassNewAdd = DataClassNewAdd(
//                        getPID, imageURL, variety, weight, grade, price,
//                        farmer, day, area, fertilizer, pesticides, dateCreate, notes
//                    )
//
//                    count+=1
//                    println("${count} 1 URL unduhan gambar: $imageURL")
//                    firebaseref.child(firebaseAuth.uid.toString()).child("pid").child(getPID)
//                        .setValue(dataClassNewAdd).addOnCompleteListener { task ->
//                            if (task.isSuccessful){
//                                Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show()
//                                Toast.makeText(this@ScanQRActivity, "get $imageURL successfully from firebase", Toast.LENGTH_SHORT).show()
//                                count+=1
//                                println("${count} 2 URL unduhan gambar: $imageURL")
//                                startActivity(Intent(this, TraceabilityListActivity::class.java))
//                                finish()
//                            }
//                        }.addOnFailureListener { e ->
//                            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
//                        }
//                }
//                Log.e("Firebase", "Got Value ${it.key}")
////                Toast.makeText(this@ScanQRActivity, "User doesn't exist", Toast.LENGTH_SHORT).show()
//
//            }.addOnFailureListener {
//                Toast.makeText(this@ScanQRActivity, "Failed", Toast.LENGTH_SHORT).show()
//            }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner?.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized) {
            codeScanner?.releaseResources()
        }
        super.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.CAMERA)

        if (permission == PackageManager.PERMISSION_DENIED) {
            makeRequest()
        } else {
            startScanning()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf
            (android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
                if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@ScanQRActivity,
                        "Camara Permission Granted",
                        Toast.LENGTH_LONG).show()
                    startScanning()
                } else {
                    Toast.makeText(this@ScanQRActivity,
                        "You need to camera permission to be able to use this app!",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }
}