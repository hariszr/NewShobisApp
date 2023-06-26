package com.example.loginandsignupfirebase

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.loginandsignupfirebase.databinding.ActivityScanQractivityBinding

private const val CAMERA_REQUEST_CODE = 101

class ScanQRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanQractivityBinding
    private lateinit var codeScanner : CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPermissions()

    }

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