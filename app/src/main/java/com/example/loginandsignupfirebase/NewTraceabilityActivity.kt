package com.example.loginandsignupfirebase

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.loginandsignupfirebase.databinding.ActivityNewTraceabilityBinding
import com.example.loginandsignupfirebase.model.DataClassNewAdd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.*

class NewTraceabilityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewTraceabilityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseref: DatabaseReference
    private lateinit var firebaserefServer: DatabaseReference
    private var imageURL: String? = null
    var uri: Uri? = null
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTraceabilityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseref = FirebaseDatabase.getInstance().getReference("users")
        firebaserefServer = FirebaseDatabase.getInstance().getReference("pid server")

//        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
//            ActivityResultContracts.StartActivityForResult()){ result ->
//            if (result.resultCode == RESULT_OK){
//                val data = result.data
//                uri = data!!.data
//                binding.qrCodeIV.setImageURI(uri)
//            } else {
//                Toast.makeText(this, "Can't Show The QR Code", Toast.LENGTH_SHORT).show()
//            }
//        }
//        binding.generateTraceabilityBtn.setOnClickListener {
//            val pid = firebaseref.push().key!!
//            val encoder = QRGEncoder(pid, null, QRGContents.Type.TEXT, 0)
//            binding.qrCodeIV.setImageBitmap(encoder.bitmap)
//        }

        displayDropDownGrade()
        binding.priceEt.setMaskingMoney("Rp. ")
//        val editText = findViewById<EditText>(R.id.priceEt)
//        editText.addCurrencyTextWatcher()
        binding.closeBtn.setOnClickListener {
            finish()
        }
//        binding.qrCodeIV.setOnClickListener {
//            val photoPicker = Intent(Intent.ACTION_PICK)
//            photoPicker.type = "image/*"
//            activityResultLauncher.launch(photoPicker)
//        }
        binding.createTraceabilityBtn.setOnClickListener {
            saveData()
        }
    }

    fun EditText.setMaskingMoney(currencyText: String) {
        this.addTextChangedListener(object : MyTextWatcher {
            val editTextWeakReference: WeakReference<EditText> =
                WeakReference<EditText>(this@setMaskingMoney)

            override fun afterTextChanged(editable: Editable?) {
                val editText = editTextWeakReference.get() ?: return
                val s = editable.toString()
                editText.removeTextChangedListener(this)
                val cleanString = s.replace("[Rp,. ]".toRegex(), "")
                val newval = currencyText + cleanString.monetize()

                editText.setText(newval)
                editText.setSelection(newval.length)
                editText.addTextChangedListener(this)
            }
        })
    }

    interface MyTextWatcher : TextWatcher {
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }

    fun String.monetize(): String = if (this.isEmpty()) "0"
    else DecimalFormat("#,###").format(this.replace("[^\\d]".toRegex(), "").toLong())

//    fun EditText.addCurrencyTextWatcher() {
//        this.addTextChangedListener(object : TextWatcher {
//            private var current = ""
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString() != current) {
//                    this@addCurrencyTextWatcher.removeTextChangedListener(this)
//
//                    val cleanString = s.toString().replace("[Rp,.\\s]".toRegex(), "")
//
//                    val parsed = cleanString.toDouble()
//                    val formatted = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(parsed)
//
//                    current = formatted
//                    this@addCurrencyTextWatcher.setText(formatted)
//                    this@addCurrencyTextWatcher.setSelection(formatted.length)
//                    this@addCurrencyTextWatcher.addTextChangedListener(this)
//                }
//            }
//        })
//    }

    private fun displayDropDownGrade() {
        val itemsGrade = listOf("All Grade", "Super", "A", "AB", "C")
        val adapterGrade = ArrayAdapter(this, R.layout.item_list_dropdown, itemsGrade)
        binding.gradeDropDown.setAdapter(adapterGrade)

    }

    @SuppressLint("SuspiciousIndentation")
    private fun saveData() {
//        val storageReference = FirebaseStorage.getInstance().reference.child("Product QR Code")
//                .child(uri!!.lastPathSegment!!)

        val builder = AlertDialog.Builder(this@NewTraceabilityActivity)
        builder.setCancelable(false)
            .setView(R.layout.layout_progress)
        val dialog = builder.create()
        dialog.show()

        uploadData()
        dialog.dismiss()

//        storageReference.putFile(uri!!)
        //       .addOnSuccessListener { taskSnapshot ->
//            val uriTask = taskSnapshot.storage.downloadUrl
//            while (!uriTask.isComplete);
//            val urlImage = uriTask.result
//            imageURL = urlImage.toString()
//
//            dialog.dismiss()
//        }.addOnFailureListener{
//
//        }
    }

    private fun uploadData() {

        val newProductID = firebaseref.push()
        val pid = newProductID.key!!

        val qrCodeBitMap = generateQrCode(pid)
        saveQrCodeToStorage(pid, qrCodeBitMap)
    }

    fun uploadData(pid: String) {

        val variety = binding.varietyEt.text.toString()
        val weight = binding.weightEt.text.toString()
        val grade = binding.gradeDropDown.text.toString()
        val price = binding.priceEt.text.toString()

        val farmer = binding.farmerEt.text.toString()
        val day = binding.harvestTimeEt.text.toString()
        val area = binding.areaEt.text.toString()
        val fertilizer = binding.fertilizerEt.text.toString()
        val pesticides = binding.pesticidesEt.text.toString()

        val dateCreate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        val notes = binding.noteEt.text.toString()

        val dataClassNewAdd = DataClassNewAdd(
            pid, imageURL, variety, weight, grade, price,
            farmer, day, area, fertilizer, pesticides, dateCreate, notes
        )

        count += 1
        println("${count} 1 URL unduhan gambar: $imageURL")
        firebaseref.child(firebaseAuth.uid.toString()).child("pid").child(pid)
            .setValue(dataClassNewAdd).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaserefServer.child(pid).setValue(dataClassNewAdd).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Created to Server", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed create to server", Toast.LENGTH_SHORT).show()
                    }
//                    Toast.makeText(this,"Created", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(this@NewTraceabilityActivity, "get $imageURL successfully from firebase", Toast.LENGTH_SHORT).show()

                    count += 1
                    println("${count} 2 URL unduhan gambar: $imageURL")
                    startActivity(Intent(this, TraceabilityListActivity::class.java))
                    finish()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }


    private fun saveQrCodeToStorage(pid: String, qrCodeBitmap: Bitmap) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // Membuat referensi storage untuk menyimpan gambar kode QR
        val qrCodeRef: StorageReference = storageRef.child("qr_codes/$pid.jpg")

        // Mengonversi bitmap menjadi byte array
        val baos = ByteArrayOutputStream()
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()

//        firebaseDatabase = FirebaseDatabase.getInstance()
//        firebaseref = firebaseDatabase.getReference("users")
//        firebaseref.child(pid).setValue(qrCodeBitmap)

        qrCodeRef.putBytes(data)
            .addOnSuccessListener { taskSnapshot ->
                // Upload berhasil
                // Anda dapat mendapatkan URL unduhan gambar dengan taskSnapshot.metadata?.reference?.downloadUrl
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    this.imageURL =
                        uri.toString() // Menyimpan URL unduhan gambar ke variabel imageURL
                    count += 1
                    println("${count} 3 URL unduhan gambar: $imageURL")
                    Log.i(
                        "QrCode",
                        "Success upload photo to storage firebase"
                    )
                    uploadData(pid)
//                uploadData(imageURL)
                }
                    ?.addOnFailureListener { exception ->
                        // Upload gagal
                        // Lakukan tindakan yang diperlukan jika terjadi kesalahan
                        Log.e(
                            "QrCode",
                            "something wrong with save URL qrCode to firebase: ${exception.message}"
                        )
                    }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "QrCode", "something wrong with save qrCode to firebase: ${exception.message}"
                )
            }
        count += 1
        println("${count} 4 URL unduhan gambar: $imageURL")
    }

    private fun generateQrCode(pid: String): Bitmap {
        val multiFormatWriter = MultiFormatWriter()
        try {
            // Membuat bit matrix dari ID produk
            val bitMatrix: BitMatrix =
                multiFormatWriter.encode(pid, BarcodeFormat.QR_CODE, 200, 200)
            // Mengonversi bit matrix menjadi bitmap
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565).apply {
            setPixel(0, 0, Color.BLACK)
        }
    }
}