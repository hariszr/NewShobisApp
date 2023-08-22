package com.example.loginandsignupfirebase

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.loginandsignupfirebase.databinding.ActivityNewTraceabilityBinding
import com.example.loginandsignupfirebase.model.DataClassNewAdd
import com.example.loginandsignupfirebase.model.DataClassSummary
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
import java.text.SimpleDateFormat
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

    @RequiresApi(Build.VERSION_CODES.O)
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
        displayDropDownVariety()
        displayDropDownGrade()
        displayDropDownHandling()
        displayDropDownFertilizer()

        binding.sellingPriceEt.setMaskingMoney("Rp. ")
        binding.handlingFeeEt.setMaskingMoney("Rp. ")
        binding.priceFromFarmerEt.setMaskingMoney("Rp. ")
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
        binding.varietyDropDown.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak diperlukan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak diperlukan
            }

            override fun afterTextChanged(s: Editable?) {
                validationVariety()
            }
        })

        binding.gradeDropDown.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak diperlukan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak diperlukan
            }

            override fun afterTextChanged(s: Editable?) {
                validationGrade()
            }
        })

        binding.handlingDropDown.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak diperlukan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak diperlukan
            }

            override fun afterTextChanged(s: Editable?) {
                validationHandling()
            }
        })

        binding.fertilizerDropDown.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak diperlukan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak diperlukan
            }

            override fun afterTextChanged(s: Editable?) {
                validationFertilizer()
            }
        })
    }

    private fun validationVariety() {
        if (binding.varietyDropDown.text.isEmpty()) {
            binding.varietyLayout.error
            binding.varietyDropDown.requestFocus()

            binding.nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Pindahkan layar ke posisi TextView yang menampilkan pesan kesalahan
                    binding.nestedScrollView.scrollTo(0, binding.varietyDropDown.top)

                    // Hapus listener setelah selesai
                    binding.nestedScrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
            return
        } else {
            binding.varietyLayout.error = null
//            binding.varietyDropDown.clearFocus()
        }
    }

    private fun validationGrade() {
        if (binding.gradeDropDown.text.isEmpty()) {
            binding.gradeLayout.error = "Grade cannot be empty"
            binding.gradeDropDown.requestFocus()
            return
        } else {
            binding.gradeLayout.error = null
            binding.gradeDropDown.clearFocus()
        }
    }

    private fun validationHandling() {
        if (binding.handlingDropDown.text.isEmpty()) {
            binding.handlingLayout.error
            binding.handlingDropDown.requestFocus()

            binding.nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Pindahkan layar ke posisi TextView yang menampilkan pesan kesalahan
                    binding.nestedScrollView.scrollTo(0, binding.handlingDropDown.top)

                    // Hapus listener setelah selesai
                    binding.nestedScrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
            return
        } else {
            binding.handlingLayout.error = null
//            binding.varietyDropDown.clearFocus()
        }
    }

    private fun validationFertilizer() {
        if (binding.fertilizerDropDown.text.isEmpty()) {
            binding.fertilizerLayout.error
            binding.fertilizerDropDown.requestFocus()
            return
        } else {
            binding.fertilizerLayout.error = null
//            binding.varietyDropDown.clearFocus()
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

        if (binding.varietyDropDown.text.toString().isEmpty()) {
            binding.varietyLayout.error = "Variety cannot be empty"
            binding.varietyDropDown.requestFocus()
            return
        } else if (binding.varietyDropDown.text.toString().length > 30) {
            binding.varietyDropDown.error = "Maximum 30 character variety"
            binding.varietyDropDown.requestFocus()
            return
        }

        if (binding.weightEt.text.toString().isEmpty()) {
            binding.weightEt.error = "Weight cannot be empty"
            binding.weightEt.requestFocus()
            return
        } else if (binding.weightEt.text.toString().toInt() > 7000) {
            binding.weightEt.error = "Maximum weight is 7000 Kg"
            binding.weightEt.requestFocus()
            return
        }

        if (binding.gradeDropDown.text.isEmpty()) {
            binding.gradeLayout.error = "Grade cannot be empty"
            binding.gradeDropDown.requestFocus()
            return
        } else {
            binding.gradeLayout.error = null
            binding.gradeDropDown.clearFocus()
        }

        if (binding.sellingPriceEt.text.toString().isEmpty()) {
            binding.sellingPriceEt.error = "Selling price cannot be empty"
            binding.sellingPriceEt.requestFocus()
            return
        } else if (binding.sellingPriceEt.text.toString().replace("Rp. ", "").replace(",", "").toIntOrNull()!! <= 3000) {
            binding.sellingPriceEt.error = "Minimum selling price is Rp 3.000"
            binding.sellingPriceEt.requestFocus()
            return
        } else if (binding.sellingPriceEt.text.toString().replace("Rp. ", "").replace(",", "").toIntOrNull()!! > 80000) {
            binding.sellingPriceEt.error = "Maximum selling price is Rp 80.000"
            binding.sellingPriceEt.requestFocus()
            return
        }

        if (binding.handlingDropDown.text.toString().isEmpty()) {
            binding.handlingLayout.error = "Variety cannot be empty"
            binding.handlingDropDown.requestFocus()
            return
        } else if (binding.handlingDropDown.text.toString().length > 60) {
            binding.handlingDropDown.error = "Maximum 60 character handling"
            binding.handlingDropDown.requestFocus()
            return
        }

        if (binding.handlingFeeEt.text.toString().isEmpty()) {
            binding.handlingFeeEt.error = "Cost prices cannot be empty"
            binding.handlingFeeEt.requestFocus()
            return
        } else if (binding.handlingFeeEt.text.toString().replace("Rp. ", "").replace(",", "").toIntOrNull()!! > 3000) {
            binding.handlingFeeEt.error = "Maximum cost price is Rp 3000"
            binding.handlingFeeEt.requestFocus()
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
            binding.farmerEt.error = "Maximum 30 character"
            binding.farmerEt.requestFocus()
            return
        }

        if (binding.harvestTimeEt.text.toString().isEmpty()) {
            binding.harvestTimeEt.error = "Harvest time cannot be empty"
            binding.harvestTimeEt.requestFocus()
            return
        } else if (binding.harvestTimeEt.text.toString().length > 80) {
            binding.harvestTimeEt.error = "Maximum is 80 day"
            binding.harvestTimeEt.requestFocus()
            return
        }

        if (binding.areaEt.text.toString().isEmpty()) {
            binding.areaEt.error = "Area planting cannot be empty"
            binding.areaEt.requestFocus()
            return
        }  else if (binding.areaEt.text.toString().length > 30) {
            binding.areaEt.error = "Maximum 30 character"
            binding.areaEt.requestFocus()
            return
        }

        if (binding.fertilizerDropDown.text.toString().isEmpty()) {
            binding.fertilizerLayout.error = "Fertilizer type cannot be empty"
            binding.fertilizerDropDown.requestFocus()
            return
        } else if (binding.fertilizerDropDown.text.toString().length > 30) {
            binding.fertilizerDropDown.error = "Maximum 30 character"
            binding.fertilizerDropDown.requestFocus()
            return
        }

        if (binding.pesticidesEt.text.toString().isEmpty()) {
            binding.pesticidesEt.error = "Pesticide type cannot be empty"
            binding.pesticidesEt.requestFocus()
            return
        } else if (binding.pesticidesEt.text.toString().length > 30) {
            binding.pesticidesEt.error = "Maximum 30 character"
            binding.pesticidesEt.requestFocus()
            return
        }

        if (binding.priceFromFarmerEt.text.toString().isEmpty()) {
            binding.priceFromFarmerEt.error = "Selling price cannot be empty"
            binding.priceFromFarmerEt.setText("0")
            binding.priceFromFarmerEt.requestFocus()
            return
        }  else if (binding.priceFromFarmerEt.text.toString().replace("Rp. ", "").replace(",", "").toIntOrNull()!! > 25000) {
            binding.priceFromFarmerEt.error = "Maximum selling price is Rp 25.000"
            binding.priceFromFarmerEt.requestFocus()
            return
        }

        if (binding.noteEt.text.toString().trim().isEmpty()) {
            binding.noteEt.setText("-")
        } else if (binding.noteEt.text.toString().length > 150) {
            binding.noteEt.error = "Maximum 150 character"
            binding.noteEt.requestFocus()
            return
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayDropDownVariety() {
        val itemsVariety = arrayListOf("Bima Brebes", "Pikatan", "Pancasona", "Trisula", "Sembrani", "Kuning", "Maja Cipanas", "Kramat-1", "Kramat-2", "Mentes", "Katumi", "TSS Agrihort 1", "Lainnya")
        val adapterVariety = ArrayAdapter(this, R.layout.item_list_dropdown, itemsVariety)
        binding.varietyDropDown.setAdapter(adapterVariety)
        binding.varietyDropDown.setOnItemClickListener { _, _, position, _ ->
            val selectedOption = itemsVariety[position]
            if (selectedOption == "Lainnya") {
                binding.varietyDropDown.isCursorVisible = true
                binding.varietyDropDown.setText("")
                binding.varietyDropDown.requestFocus()

                //munculkan keyboard
                val keyboardShow = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboardShow.showSoftInput(binding.varietyDropDown, InputMethodManager.SHOW_IMPLICIT)
                return@setOnItemClickListener
            } else {
                binding.weightEt.requestFocus()

                //munculkan keyboard
                val keyboardShow = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboardShow.showSoftInput(binding.weightEt, InputMethodManager.SHOW_IMPLICIT)
                return@setOnItemClickListener
            }
        }
    }

    private fun displayDropDownGrade() {
        val itemsGrade = listOf("All Grade", "Super", "A", "AB", "C")
        val adapterGrade = ArrayAdapter(this, R.layout.item_list_dropdown, itemsGrade)
        binding.gradeDropDown.setAdapter(adapterGrade)
        binding.gradeDropDown.setOnItemClickListener { _, _, position, _ ->
            binding.sellingPriceEt.requestFocus()

            //munculkan keyboard
            val keyboardShow = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboardShow.showSoftInput(binding.sellingPriceEt, InputMethodManager.SHOW_IMPLICIT)
            return@setOnItemClickListener
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayDropDownHandling() {
        val itemsHandling = arrayListOf("Tidak Ada", "Sortir", "Grading", "Pengemasan", "Transport", "Sortir", "Sortir & Grading", "Sortir & Pengemaasan", "Sortir & Transport", "Grading & Pengemasan", "Grading & Transport", "Pengemasan & Transport", "Grading & Pengemasan", "Sortir, Grading & Transport", "Sortir, Pengemasan & Transport", "Grading, Pengemasan, & Transport", "Full Service (Sortir, Grading, Pengemasan & Transport)", "Lainnya")
        val adapterHandling = ArrayAdapter(this, R.layout.item_list_dropdown, itemsHandling)
        binding.handlingDropDown.setAdapter(adapterHandling)
        binding.handlingDropDown.setOnItemClickListener { _, _, position, _ ->
            val selectedOption = itemsHandling[position]
            if (selectedOption == "Lainnya") {
                binding.handlingDropDown.isCursorVisible = true
                binding.handlingDropDown.setText("")
                binding.handlingFeeEt.inputType = android.text.InputType.TYPE_CLASS_TEXT
                binding.handlingDropDown.requestFocus()

                //munculkan keyboard
                val keyboardShow = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboardShow.showSoftInput(binding.handlingDropDown, InputMethodManager.SHOW_IMPLICIT)
                return@setOnItemClickListener
            }

            if (selectedOption == "Tidak Ada") {
                binding.handlingDropDown.isCursorVisible = false
                binding.handlingFeeEt.setText("0")
                binding.handlingFeeEt.inputType = android.text.InputType.TYPE_NULL
                binding.handlingFeeEt.isCursorVisible = false
                binding.farmerEt.requestFocus()

                //munculkan keyboard
                val keyboardShow = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboardShow.showSoftInput(binding.farmerEt, InputMethodManager.SHOW_IMPLICIT)
                return@setOnItemClickListener
            } else {
                binding.handlingFeeEt.isCursorVisible = true
                binding.handlingFeeEt.inputType = android.text.InputType.TYPE_CLASS_NUMBER
                binding.handlingFeeEt.requestFocus()

                //munculkan keyboard
                val keyboardShow = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboardShow.showSoftInput(binding.handlingFeeEt, InputMethodManager.SHOW_IMPLICIT)
                return@setOnItemClickListener
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayDropDownFertilizer() {
        val itemsFertilizer = arrayListOf("NPK Mutiara (16:16:16)", "NPK GOLD DGW (16:10:18)", "NPK Phonska(15:15:15)", "SP-36", "KCL", "Lainnya")
        val adapterFertilizer = ArrayAdapter(this, R.layout.item_list_dropdown, itemsFertilizer)
        binding.fertilizerDropDown.setAdapter(adapterFertilizer)
        binding.fertilizerDropDown.setOnItemClickListener { _, _, position, _ ->
            val selectedOption = itemsFertilizer[position]
            if (selectedOption == "Lainnya") {
                binding.fertilizerDropDown.isCursorVisible = true
                binding.fertilizerDropDown.setText("")
                binding.fertilizerDropDown.requestFocus()

                //munculkan keyboard
                val keyboardShow = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboardShow.showSoftInput(binding.fertilizerDropDown, InputMethodManager.SHOW_IMPLICIT)
                return@setOnItemClickListener
            }
        }
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

    @SuppressLint("SimpleDateFormat")
    private fun uploadDataSummary(pid: String) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = Calendar.getInstance().time

        val dateIn = dateFormat.format(currentDate)
        val variety = binding.varietyDropDown.text.toString()
        val dateOut = dateFormat.format(currentDate)
        val weightIn = binding.weightEt.text.toString().toInt()
        val purchasePrice = binding.priceFromFarmerEt.text.toString().replace("Rp. ", "").replace(",", "").toInt()
        val costPrices = binding.handlingFeeEt.text.toString().replace("Rp. ", "").replace(",", "").toInt()
        val weightOut = binding.weightEt.text.toString().toInt()
        val sellingPrice = binding.sellingPriceEt.text.toString().replace("Rp. ", "").replace(",", "").toInt()
        val purchaseCapital = weightIn * purchasePrice
        val capitalCosts = weightIn * costPrices
        val totalCapital = purchaseCapital + capitalCosts
        val totalSell = weightOut * sellingPrice
        val profit = totalSell - totalCapital

        val dateCreate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        val dataClassSummary = DataClassSummary(
            dateIn, variety, pid, dateOut, weightIn, purchasePrice, costPrices, weightOut, sellingPrice, purchaseCapital, capitalCosts, totalCapital, totalSell, profit
        )

        firebaseRef.child(firebaseAuth.uid.toString()).child("summary").child(dateCreate)
            .setValue(dataClassSummary).addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    println("Successfully Created Summary in The Users")
                    Log.i("Data Summary", "Successfully Created Summary in The Users")

                    startActivity(Intent(this, TraceabilityListActivity::class.java))
                    finish()
                }
            }.addOnFailureListener {
                println("Failed Create Summary in The Users")
                Log.i("Data Summary", "Failed Create Summary in The Users")
            }

    }

    fun uploadData(pid: String) {

        val variety = binding.varietyDropDown.text.toString()
        val weight = binding.weightEt.text.toString()
        val grade = binding.gradeDropDown.text.toString()
        val sellingPrice = binding.sellingPriceEt.text.toString()
        val handling = binding.handlingDropDown.text.toString()
        val handlingFee = binding.handlingFeeEt.text.toString()

        val farmer = binding.farmerEt.text.toString()
        val day = binding.harvestTimeEt.text.toString()
        val area = binding.areaEt.text.toString()
        val fertilizer = binding.fertilizerDropDown.text.toString()
        val pesticides = binding.pesticidesEt.text.toString()
        val weightFarmers = binding.weightEt.text.toString()
        val priceFarmers = binding.priceFromFarmerEt.text.toString()

        val dateCreate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        val notes = binding.noteEt.text.toString()

        val sellingPriceUpdate = binding.sellingPriceEt.text.toString()
        val dataDateUpdate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

//        var email = firebaseAuth.currentUser?.email

        firebaseRef.child(firebaseAuth.uid.toString()).child("Profile Users").get()
            .addOnSuccessListener {
                var fullName = it.child("fullName").value.toString()
                var email = it.child("email").value.toString()
                var actor = it.child("levelUser").value.toString()
                var gender = it.child("gender").value.toString()
                var company = it.child("nameCompany").value.toString()
                var address = it.child("address").value.toString()

                if (fullName.isBlank()) fullName = "-"
                if (email.isBlank()) email = "-"
                if (actor.isBlank()) actor = "-"
                if (gender.isBlank()) gender = "-"
                if (company.isBlank()) company = "-"
                if (address.isBlank()) address = "-"

                val dataClassNewAdd = DataClassNewAdd(
                    pid, imageURL, imageURL, imageURLPic, variety, weight, grade, sellingPrice, handling, handlingFee,
                    farmer, day, area, fertilizer, pesticides, weightFarmers, priceFarmers,
                    dateCreate,
                    notes,
                    fullName, actor, email, gender, company, address,
                    sellingPrice, dateCreate
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
                    uploadDataSummary(pid)
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