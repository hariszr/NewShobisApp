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
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseRefServer: DatabaseReference
    private var imageURL: String? = null
    private var imageURLPic: String? = null
    var uri: Uri? = null
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTraceabilityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")
        firebaseRefServer = FirebaseDatabase.getInstance().getReference("pid server")

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                val data = result.data
                uri = data!!.data
                binding.shallotImageIV.setImageURI(uri)
                binding.errorPictShallotET.visibility = View.GONE
            } else {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
                binding.errorPictShallotET.requestFocus()
            }
        }
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
        binding.shallotImageIV.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }
        binding.createTraceabilityBtn.setOnClickListener {
            validateInputData()
        }
    }

    private fun validateInputData() {

        if (uri == null) {
            Toast.makeText(this, "Product image cannot be empty", Toast.LENGTH_SHORT).show()
            binding.errorPictShallotET.visibility = View.VISIBLE
            binding.errorPictShallotET.requestFocus()
            binding.nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Pindahkan layar ke posisi TextView yang menampilkan pesan kesalahan
                    binding.nestedScrollView.scrollTo(0, binding.errorPictShallotET.top)

                    // Hapus listener setelah selesai
                    binding.nestedScrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
            return
        } else {
            binding.errorPictShallotET.visibility = View.GONE
        }

        if (binding.varietyEt.text.toString().isEmpty()) {
            binding.varietyEt.error = "Variety cannot be empty"
            binding.varietyEt.requestFocus()
            return
        } else if (binding.varietyEt.text.toString().length > 30) {
            binding.varietyEt.error = "Maximum 30 character variety"
            binding.farmerEt.requestFocus()
            return
        }

        if (binding.weightEt.text.toString().isEmpty()) {
            binding.weightEt.error = "Weight cannot be empty"
            binding.weightEt.requestFocus()
            return
        } else if (binding.weightEt.text.toString().toInt() > 30000) {
            binding.weightEt.error = "Maximum weight is 30000 gram"
            binding.weightEt.requestFocus()
            return
        }

        if (binding.gradeDropDown.text.isEmpty()) {
            binding.gradeDropDown.error = "Grade cannot be empty"
            binding.gradeDropDown.requestFocus()
            return
        } else {
            binding.gradeDropDown.error = null
            binding.gradeDropDown.clearFocus()
        }

        if (binding.priceEt.text.toString().isEmpty()) {
            binding.priceEt.error = "Price cannot be empty"
            binding.priceEt.requestFocus()
            return
        } else if (binding.priceEt.text.toString().replace("Rp. ", "").replace(",", "").toIntOrNull()!! < 1000) {
            binding.priceEt.error = "Maximum price is Rp 1.000"
            binding.priceEt.requestFocus()
            return
        } else if (binding.priceEt.text.toString().replace("Rp. ", "").replace(",", "").toIntOrNull()!! > 50000) {
            binding.priceEt.error = "Maximum price is Rp 50.000"
            binding.priceEt.requestFocus()
            return
        }

        if (binding.farmerEt.text.toString().isEmpty()) {
            binding.farmerEt.error = "Farmer name cannot be empty"
            binding.farmerEt.requestFocus()
            return
        } else if (binding.farmerEt.text.toString().trim().any {it.isDigit()}) {
            binding.farmerEt.error = "Contain number is not allowed"
            binding.farmerEt.requestFocus()
            return
        } else if (binding.farmerEt.text.toString().length > 30) {
            binding.farmerEt.error = "Maximum 30 character name"
            binding.farmerEt.requestFocus()
            return
        }

        if (binding.harvestTimeEt.text.toString().isEmpty()) {
            binding.harvestTimeEt.error = "Harvest time cannot be empty"
            binding.harvestTimeEt.requestFocus()
            return
        } else if (binding.harvestTimeEt.text.toString().length > 150) {
            binding.harvestTimeEt.error = "Maximum is 150 day"
            binding.harvestTimeEt.requestFocus()
            return
        }

        if (binding.areaEt.text.toString().isEmpty()) {
            binding.areaEt.error = "Area planting cannot be empty"
            binding.areaEt.requestFocus()
            return
        }

        if (binding.fertilizerEt.text.toString().isEmpty()) {
            binding.fertilizerEt.error = "Fertilizer type cannot be empty"
            binding.fertilizerEt.requestFocus()
            return
        }

        if (binding.pesticidesEt.text.toString().isEmpty()) {
            binding.pesticidesEt.error = "Pesticide type cannot be empty"
            binding.pesticidesEt.requestFocus()
            return
        }

        if (binding.noteEt.text.toString().trim().isEmpty()) {
            binding.noteEt.setText("-")
        }
        saveData()

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


//        if (imageURLPic == null) {
//            Toast.makeText(this, "Product image cannot be empty", Toast.LENGTH_SHORT).show()
//            return
//        }

        val storageReference = FirebaseStorage.getInstance().reference.child("Product QR Code")
                .child(uri!!.lastPathSegment!!)

        val builder = AlertDialog.Builder(this@NewTraceabilityActivity)
        builder.setCancelable(false)
            .setView(R.layout.layout_progress)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isComplete);
                    val urlImage = uriTask.result
                imageURLPic = urlImage.toString()

                uploadData()

                dialog.dismiss()
            }.addOnFailureListener{
                dialog.dismiss()
            }
    }

    private fun uploadData() {

        val newProductID = firebaseRef.push()
        val pid = newProductID.key!!

//        binding.farmerEt.addTextChangedListener {
//            if (it!!.count() > 0)
//                binding.farmerLayout.error = null
//        }
//
//        binding.varietyEt.addTextChangedListener {
//            if (it!!.count() > 0)
//                binding.varietyLayout.error = null
//        }
//
//        binding.weightEt.addTextChangedListener {
//            if (it!!.count() > 0)
//                binding.weightLayout.error = null
//        }

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

        var email = firebaseAuth.currentUser?.email

        firebaseRef.child(firebaseAuth.uid.toString()).child("Profile Users").get()
            .addOnSuccessListener {
                var fullName = it.child("fullName").value.toString()
                var actor = it.child("levelUser").value.toString()
                var gender = it.child("gender").value.toString()
                var address = it.child("address").value.toString()

                if (fullName.isBlank()) fullName = "-"
                if (email!!.isBlank()) email = "-"
                if (actor.isBlank()) actor = "-"
                if (gender.isBlank()) gender = "-"
                if (address.isBlank()) address = "-"

                val dataClassNewAdd = DataClassNewAdd(
                    pid, imageURL, imageURL, imageURLPic, variety, weight, grade, price,
                    farmer, day, area, fertilizer, pesticides, dateCreate, notes,
                    fullName, actor, email, gender, address
                )


                count += 1
                println("${count} 1 URL unduhan gambar: $imageURL")
                firebaseRef.child(firebaseAuth.uid.toString()).child("pid").child(pid)
                    .setValue(dataClassNewAdd).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseRefServer.child(pid).setValue(dataClassNewAdd).addOnCompleteListener { task ->
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
            }.addOnFailureListener {
                println("Error get profile data : ${it.message}")
                Log.e("Get Profile Data", "${it.message}")
            }


//        when {
//            imageURLPic == "" -> {
//                Toast.makeText(this, "Product image cannot be empty", Toast.LENGTH_SHORT).show()
//            }
//            variety == "" -> {
//                binding.varietyEt.error = "Variety cannot be empty"
//            }
//            weight == "" -> {
//                binding.weightEt.error = "Weight cannot be empty"
//            }
//            weight.toInt() > 30000 -> {
//                binding.varietyEt.error = "Variety cannot be empty"
//            }
//
//        }
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