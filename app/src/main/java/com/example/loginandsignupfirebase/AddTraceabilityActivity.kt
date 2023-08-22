package com.example.loginandsignupfirebase

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.loginandsignupfirebase.databinding.ActivityAddTraceabilityBinding
import com.example.loginandsignupfirebase.model.DataClassAdd
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
import kotlin.collections.HashMap

class AddTraceabilityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTraceabilityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRefServer: DatabaseReference
    private lateinit var firebaseRef: DatabaseReference
    private var imageURL: String? = null
    private var count = 0
    val database = FirebaseDatabase.getInstance()
    private val myref = database.reference

    private var oldPID: String? = ""

    private var loadingDialog: AlertDialog? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTraceabilityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRefServer = FirebaseDatabase.getInstance().getReference("pid server")
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")

        oldPID = intent.extras?.getString("sendPID")

        displayDropDownGrade()
        displayDropDownHandling()
        displayDropDownDistribution()

        binding.purchasePriceEt.setMaskingMoney("Rp. ")
        binding.handlingFeeEt.setMaskingMoney("Rp. ")
        binding.sellingPriceEt.setMaskingMoney("Rp. ")

        firebaseRefServer.child(oldPID.toString()).get().addOnSuccessListener{
                if (oldPID.toString().isNotEmpty()) {
                    val dateBefore = it.child("dataDateCreate").value.toString()
                    val dataSellingPriceUpdate = it.child("dataSellingPriceUpdate").value.toString().replace("Rp. ", "").replace(",", "")

                    binding.purchasePriceEt.setText(dataSellingPriceUpdate)
                    binding.purchasePriceEt.inputType = android.text.InputType.TYPE_NULL
                    //untuk pengecekan
//                    val dateString = "Jul 25, 2023 16:25:19"

                    println("data yg diambil tanggal : $dateBefore")
                    println("data yg diambil sellingPriceUpdate : $dataSellingPriceUpdate")

                    // Konversi string menjadi objek Date
                    val dateFormat = SimpleDateFormat("MMM d, yyyy HH:mm:ss", Locale.US)
                    val date = dateFormat.parse(dateBefore)

                    println("data yg diambil tanggal date : $date")

                    // Mengubah objek Date menjadi millis
                    val myDesiredDateInMillis = date?.time

                    // Tanggal saat ini
                    val currentDate = System.currentTimeMillis()

                    // Menambahkan 1 hari dalam millis (86400000 millis dalam sehari)
//                    val oneDayInMillis = 86400000L
//                    val nextDayInMillis = myDesiredDateInMillis!! + oneDayInMillis

                    val myCalendar = Calendar.getInstance()
                    val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        myCalendar.set(Calendar.YEAR, year)
                        myCalendar.set(Calendar.MONTH, month)
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        updateLabel(myCalendar)
                    }

                    binding.arriveDateEt.setOnClickListener {
                        val dPicker = DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH))
                        dPicker.datePicker.minDate = myDesiredDateInMillis!!
                        dPicker.datePicker.maxDate = currentDate
                        dPicker.show()
                    }
                }
            }

        val myCalendar2 = Calendar.getInstance()
        val datePicker2 = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar2.set(Calendar.YEAR, year)
            myCalendar2.set(Calendar.MONTH, month)
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateLabel2(myCalendar2)
        }

        binding.outgoingDateEt.setOnClickListener {
            val dPicker = DatePickerDialog(this, datePicker2, myCalendar2.get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH), myCalendar2.get(Calendar.DAY_OF_MONTH))
            dPicker.datePicker.minDate = System.currentTimeMillis()
            dPicker.show()
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.addDataTraceabilityBtn.setOnClickListener {
            validationInputData()
//            saveData()
        }

        binding.arriveDateEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak diperlukan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak diperlukan
            }

            override fun afterTextChanged(s: Editable?) {
                validationInputDateArrive()
            }
        })

        binding.outgoingDateEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak diperlukan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak diperlukan
            }

            override fun afterTextChanged(s: Editable?) {
                validationInputDateOutgoing()
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

        binding.distributionDropDown.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak diperlukan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak diperlukan
            }

            override fun afterTextChanged(s: Editable?) {
                validationDistribution()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun DialogProgressStart() {
        val builder = AlertDialog.Builder(this@AddTraceabilityActivity)
        builder.setCancelable(false)
            .setView(R.layout.layout_progress)
        loadingDialog = builder.create()
        loadingDialog?.show()
        initCopy()
    }


    private fun validationInputDateArrive() {
        if (binding.arriveDateEt.text.toString().isEmpty()) {
            binding.arriveDateLayout.error = "Product arrive date cannot be empty"
            binding.arriveDateEt.requestFocus()
            return
        } else {
            binding.arriveDateLayout.error = null
        }
    }
    private fun validationInputDateOutgoing() {
        if (binding.outgoingDateEt.text.toString().isEmpty()) {
            binding.outgoingDateLayout.error = "Product outgoing date cannot be empty"
            binding.outgoingDateEt.requestFocus()
            return
        } else {
            binding.outgoingDateLayout.error = null
            binding.outgoingDateEt.clearFocus()
            binding.weightLossEt.clearFocus()
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

    private fun validationDistribution() {
        if (binding.distributionDropDown.text.isEmpty()) {
            binding.distributionLayout.error = "Distribution cannot be empty"
            binding.distributionDropDown.requestFocus()
            return
        } else {
            binding.distributionLayout.error = null
            binding.distributionDropDown.clearFocus()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validationInputData() {
        //product condition arrived
        if (binding.arriveDateEt.text.toString().isEmpty()) {
            binding.arriveDateLayout.error = "Product arrive date cannot be empty"
            binding.arriveDateEt.requestFocus()
            return
        } else {
            binding.arriveDateLayout.error = null
        }

        if (binding.incomingWeightEt.text.toString().isEmpty()) {
            binding.incomingWeightEt.error = "Product weight upon arrival cannot be empty"
            binding.incomingWeightEt.requestFocus()
            return
        } else if (binding.incomingWeightEt.text.toString().toInt() > 7000) {
            binding.incomingWeightEt.error = "Maximum weight is 7000 Kg"
            binding.incomingWeightEt.requestFocus()
            return
        } else if (binding.incomingWeightEt.text.toString().length > 4) { /** ini tidak berguna sebenrnya, karena sudah dibawah 6000 dan tidak akan lebih dari 4 digit, isoke buat belajar **/
            binding.incomingWeightEt.error = "Maximum 4 digit"
            binding.incomingWeightEt.requestFocus()
            return
        } else {
            binding.incomingWeightEt.error = null
        }

        if (binding.purchasePriceEt.text.toString().isEmpty()) {
            binding.purchasePriceEt.error = "Price cannot be empty"
            binding.purchasePriceEt.requestFocus()
            return
        }
        else if (binding.purchasePriceEt.text.toString().replace("Rp. ", "").replace(",", "").toIntOrNull()!! < 5000) {
            binding.purchasePriceEt.error = "Minimum price is Rp 5.000"
            binding.purchasePriceEt.requestFocus()
            return
        } else if (binding.purchasePriceEt.text.toString().replace("Rp. ", "").replace(",", "").toIntOrNull()!! > 80000) {
            binding.purchasePriceEt.error = "Maximum price is Rp 80.000"
            binding.purchasePriceEt.requestFocus()
            return
        }
        else {
            binding.purchasePriceEt.error = null
        }

        //update information product
        if (binding.outgoingDateEt.text.toString().isEmpty()) {
            binding.outgoingDateLayout.error = "Product outgoing date cannot be empty"
            binding.outgoingDateEt.requestFocus()
            return
        } else {
            binding.outgoingDateLayout.error = null
            binding.outgoingDateEt.clearFocus()
            binding.weightLossEt.clearFocus()
        }

        if (binding.outgoingWeightEt.text.toString().isEmpty()) {
            binding.outgoingWeightEt.error = "Outgoing weight cannot be empty"
            binding.outgoingWeightEt.requestFocus()
            return
        } else if (binding.outgoingWeightEt.text.toString().toInt() > 7000) {
            binding.outgoingWeightEt.error = "Maximum weight is 7000 Kg"
            binding.outgoingWeightEt.requestFocus()
            return
        } else if (binding.outgoingWeightEt.text.toString().length > 4) { /** ini tidak berguna sebenrnya, karena sudah dibawah 6000 dan tidak akan lebih dari 4 digit, isoke buat belajar **/
            binding.outgoingWeightEt.error = "Maximum 4 digit"
            binding.outgoingWeightEt.requestFocus()
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


        if (binding.weightLossEt.text.toString().isEmpty()) {
            binding.weightLossEt.error = "Weight loss cannot be empty"
            binding.weightLossEt.requestFocus()
            return
        }
        else if (binding.weightLossEt.text.toString().toInt() > 750) {
            binding.weightLossEt.error = "Maximum weight loss is 750 Kg"
            binding.weightLossEt.requestFocus()
            return
        } else if (binding.weightLossEt.text.toString().length > 3) { /** ini tidak berguna sebenrnya, karena sudah dibawah 6000 dan tidak akan lebih dari 4 digit, isoke buat belajar **/
            binding.weightLossEt.error = "Maximum 3 digit"
            binding.weightLossEt.requestFocus()
            return
        }

        if (binding.sellingPriceEt.text.toString().isEmpty()) {
            binding.sellingPriceEt.error = "Price cannot be empty"
            binding.sellingPriceEt.requestFocus()
            return
        } else if (binding.sellingPriceEt.text.toString().replace("Rp. ", "").replace(",", "").toIntOrNull()!! < 1000) {
            binding.sellingPriceEt.error = "Minimum price is Rp 1.000"
            binding.sellingPriceEt.requestFocus()
            return
        } else if (binding.sellingPriceEt.text.toString().replace("Rp. ", "").replace(",", "").toIntOrNull()!! > 80000) {
            binding.sellingPriceEt.error = "Maximum price is Rp 80.000"
            binding.sellingPriceEt.requestFocus()
            return
        } else {
            binding.sellingPriceEt.error = null
        }

        if (binding.noteEt.text.toString().trim().isEmpty()) {
            binding.noteEt.setText("-")
            binding.noteEt.clearFocus()
        }

        if (binding.distributionDropDown.text.toString().isEmpty()) {
            binding.distributionLayout.error = "Distribution cannot be empty"
            binding.distributionDropDown.requestFocus()
            return
        }


        DialogProgressStart()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveData() {
//        val storageReference = FirebaseStorage.getInstance().reference.child("Product QR Code")
//                .child(uri!!.lastPathSegment!!)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeProductID(): String {
        val newProductID = firebaseRef.push()
        val pid = newProductID.key!!

        val qrCodeBitMap = generateQrCode(pid)
        saveQrCodeToStorage(pid, qrCodeBitMap)

        return pid
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun initCopy() {

        val newPID = makeProductID()
        println("NewPID : $newPID")
//        val oldPID = "-N_yGBMC_nwnvhktwjxB"

        val sourceRef = firebaseRefServer.child(oldPID.toString())
        val destinationRefServer = firebaseRefServer.child(newPID)
        val destinationRef = firebaseRef.child(firebaseAuth.uid.toString()).child("pid").child(newPID)

        copyDataServer(sourceRef, destinationRefServer) {
            // Panggil callback setelah semua data berhasil disalin
            println("Successfully copied data to server")
        }
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
                            copyData(sourceChildRef, destinationChildRef) /** mungkin ini bisa diganti jadi "copyData" jika ada something yang error **/ {
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

    fun copyDataServer(sourceRef: DatabaseReference, destinationRefServer: DatabaseReference, callback: () -> Unit) {
        sourceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                destinationRefServer.setValue(dataSnapshot.value) { databaseError, _ ->
                    if (databaseError == null) {
                        for (childSnapshot in dataSnapshot.children) {
                            val sourceChildRef = childSnapshot.ref
                            val destinationChildRef = destinationRefServer.child(childSnapshot.key!!)
                            copyDataServer(sourceChildRef, destinationChildRef) {
                                // Panggil callback setelah semua data berhasil disalin
                                println("Successfully copied data to server")
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
    @SuppressLint("SimpleDateFormat")
    private fun uploadDataSummary(pid: String) {

        firebaseRefServer.child(oldPID.toString()).get().addOnSuccessListener{
            if (oldPID.toString().isNotEmpty()){
                val variety = it.child("dataVariety").value.toString()

                println("Successfully get dataVariety in OLDPID in server PID")
                Log.i("Get data variety", "Successfully get dataVariety in OLDPID in server PID")

                val dateIn = binding.arriveDateEt.text.toString()
                //variety
                val dateOut = binding.outgoingDateEt.text.toString()
                val weightIn = binding.incomingWeightEt.text.toString().toInt()
                val purchasePrice = binding.purchasePriceEt.text.toString().replace("Rp. ", "").replace(",", "").toInt()
                val costPrices = binding.handlingFeeEt.text.toString().replace("Rp. ", "").replace(",", "").toInt()
                val weightOut = binding.outgoingWeightEt.text.toString().toInt()
                val sellingPrice = binding.sellingPriceEt.text.toString().replace("Rp. ", "").replace(",", "").toInt()


                val dateCreate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

                val weightLoss = binding.weightLossEt.text.toString().toInt()
                if (binding.distributionDropDown.text.toString() == "Sebagian Produk") {

                    val resultDistribution = weightIn + weightOut - weightIn + weightLoss

                    val purchaseCapital = resultDistribution * purchasePrice
                    val capitalCosts = resultDistribution * costPrices
                    val totalCapital = purchaseCapital + capitalCosts
                    val totalSell = weightOut * sellingPrice
                    val profit = totalSell - totalCapital

                    val dataClassSummary = DataClassSummary(
                        dateIn, variety, pid, dateOut, resultDistribution, purchasePrice, costPrices, weightOut, sellingPrice, purchaseCapital, capitalCosts, totalCapital, totalSell, profit
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
                } else {
                    val resultDistribution = weightIn

                    val purchaseCapital = resultDistribution * purchasePrice
                    val capitalCosts = resultDistribution * costPrices
                    val totalCapital = purchaseCapital + capitalCosts
                    val totalSell = weightOut * sellingPrice
                    val profit = totalSell - totalCapital

                    val dataClassSummary = DataClassSummary(
                        dateIn, variety, pid, dateOut, resultDistribution, purchasePrice, costPrices, weightOut, sellingPrice, purchaseCapital, capitalCosts, totalCapital, totalSell, profit
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

            }
        }.addOnFailureListener {
            println("Failed get dataVariety in OLDPID in server PID")
            Log.i("Get data variety", "Failed get dataVariety in OLDPID in server PID")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadData(pid: String) {

        val arriveDate = binding.arriveDateEt.text.toString()
        val incomingWeight = binding.incomingWeightEt.text.toString()
        val purchasePrice = binding.purchasePriceEt.text.toString()

        val outgoingDate = binding.outgoingDateEt.text.toString()
        val outgoingWeight = binding.outgoingWeightEt.text.toString()
        val grade = binding.gradeDropDown.text.toString()
        val handling = binding.handlingDropDown.text.toString()
        val handlingFee = binding.handlingFeeEt.text.toString()
        val weightLoss = binding.weightLossEt.text.toString()
        val sellingPrice = binding.sellingPriceEt.text.toString()
        val distribution = binding.distributionDropDown.text.toString()

        val dateCreate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        val notes = binding.noteEt.text.toString()

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

                val dataClassAdd = DataClassAdd(
                    pid, imageURL, arriveDate, incomingWeight, purchasePrice,
                    outgoingDate, outgoingWeight, grade, handling, handlingFee, weightLoss, sellingPrice, distribution,
                    dateCreate,
                    notes,
                    fullName, actor, email, gender, company, address
                )

                count += 1
                println("${count} 1 URL unduhan gambar: $imageURL")
                firebaseRef.child(firebaseAuth.uid.toString()).child("pid").child(pid).child("Secondary Data").child(dateCreate).setValue(dataClassAdd)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            firebaseRefServer.child(pid).child("Secondary Data").child(dateCreate).setValue(dataClassAdd).addOnCompleteListener { t ->
                                if (t.isSuccessful) {
                                    updateQrCode(pid)

                                    val childUpdates = HashMap<String, Any>()

                                    // Simpan nama di path yang sesuai
                                    childUpdates["$pid/dataDateUpdate"] = outgoingDate
                                    childUpdates["$pid/dataSellingPriceUpdate"] = sellingPrice

                                    firebaseRefServer.updateChildren(childUpdates)
                                        .addOnSuccessListener{
                                            // Berhasil menyimpan imageURL ke Firebase
                                            Log.i("imageUrl", "Success upload image to storage and firebase realtime server")

                                            // Simpan juga ke dalam user
                                            firebaseRef.child(firebaseAuth.uid.toString()).child("pid").updateChildren(childUpdates)
                                                .addOnCompleteListener {
                                                    Log.i("imageUrl", "Success upload image to storage and firebase realtime user")

                                                }.addOnFailureListener { exception ->
                                                    Log.e("imageUrl", "Something wrong with save QrCode Update to firebase realtime user: ${exception.message}")
                                                }

                                        }.addOnFailureListener { exception ->
                                            Log.e("imageUrl", "Something wrong with save QrCode Update to firebase realtime server: ${exception.message}")
                                        }


                                    println("Data Secondary Successfully Updated to PID Server")
                                    Log.i( "Server Updated","Data Secondary Successfully Updated to PID Server")
                                }
                            }.addOnFailureListener { e ->
                                Toast.makeText(this@AddTraceabilityActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                                println("Data Secondary Failed Update to PID Server")
                                Log.e( "Server Updated","Data Secondary Failed Update to PID Server")
                            }

                            Toast.makeText(this@AddTraceabilityActivity, "Created", Toast.LENGTH_SHORT).show()
//                            Toast.makeText(this@AddTraceabilityActivity, "get $imageURL successfully from firebase", Toast.LENGTH_SHORT).show()
                            count += 1
                            println("${count} 2 URL unduhan gambar: $imageURL")

                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(this@AddTraceabilityActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener {
                println("Error get profile data : ${it.message}")
                Log.e("Get Profile Data", "${it.message}")
            }


//        val startDate = LocalDateTime.of(2023, Month.JANUARY, 1, 0, 0, 0) // Tanggal dan waktu awal
//        val endDate = LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59, 59) // Tanggal dan waktu akhir
//
//        fun generateNewPath(oldPath: String): String {
//            val pathComponents = oldPath.split(" ")
//            val tanggal = pathComponents[1]
//            val jam = pathComponents[2]
//            val newTanggal = "new_" + tanggal
//            val newJam = "new_" + jam
//            return "data/$newTanggal $newJam"
//        }
//
//        var currentDate = startDate
//
//        while (currentDate <= endDate) {
//            val currentTanggal = currentDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
//            val currentJam = currentDate.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//            val oldPath = "data/$currentTanggal $currentJam"
//            val newPath = generateNewPath(oldPath)
//
//            val oldRef = firebaseref.child(oldUserID).child(oldPath)
//            val newRef = firebaseref.child(newUserID).child(newPath)
//
//            oldRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    val data = dataSnapshot.getValue(DataClassAdd::class.java)
//
//                    newRef.setValue(data).addOnSuccessListener {
//                        Log.d("Firebase", "Data copied successfully from $oldPath to $newPath")
//                    }.addOnFailureListener { exception ->
//                        Log.e("Firebase", "Failed to copy data from $oldPath to $newPath: ${exception.message}")
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Log.e("Firebase", "Failed to read data from $oldPath: ${databaseError.message}")
//                }
//            })
//
//            currentDate = currentDate.plusSeconds(1) // Atur interval sesuai kebutuhan
//
//        }

//        val oldUserRef = firebaseref.child(oldUserID)
//        oldUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val newAddData = snapshot.getValue(DataClassNewAdd::class.java)
//                val newUserRef = firebaseref.child(newUserID)

//                newUserRef.setValue(newAddData).addOnSuccessListener {
//                    Log.d("Firebase", "Data copied successfully from $oldUserID to $newUserID")

        /////////////////
//                }
//                    .addOnFailureListener { exception ->
//                        Log.e(
//                            "Firebase",
//                            "Failed to copy data from $oldUserID to $newUserID: ${exception.message}"
//                        )
//                    }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("Firebase", "Failed to read data from $oldUserID: ${error.message}")
//            }
//
//        })
    }
    private fun updateQrCode(pid: String) {

        val childUpdates = HashMap<String, Any>()
        val dataQrCodeUpdate = DataClassAdd(imageURL)

        // Simpan nama di path yang sesuai
        childUpdates["$pid/dataQrCodeUpdate"] = imageURL.toString()

        firebaseRefServer.updateChildren(childUpdates)
            .addOnSuccessListener{
                // Berhasil menyimpan imageURL ke Firebase
                Log.i("imageUrl", "Success upload image to storage and firebase realtime server")

                // Simpan juga ke dalam user
                firebaseRef.child(firebaseAuth.uid.toString()).child("pid").updateChildren(childUpdates)
                    .addOnCompleteListener {
                        Log.i("imageUrl", "Success upload image to storage and firebase realtime user")

                    }.addOnFailureListener { exception ->
                        Log.e("imageUrl", "Something wrong with save QrCode Update to firebase realtime user: ${exception.message}")
                    }

            }.addOnFailureListener { exception ->
                Log.e("imageUrl", "Something wrong with save QrCode Update to firebase realtime server: ${exception.message}")
            }
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

    @RequiresApi(Build.VERSION_CODES.O)
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
//                    updateQrCode(pid)
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


    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val simpleDateFormat = SimpleDateFormat(myFormat, Locale.UK)
        binding.arriveDateEt.setText(simpleDateFormat.format(myCalendar.time))
    }

    private fun updateLabel2(myCalendar2: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val simpleDateFormat = SimpleDateFormat(myFormat, Locale.UK)
        binding.outgoingDateEt.setText(simpleDateFormat.format(myCalendar2.time))
    }

    private fun displayDropDownGrade() {
        val itemsGrade = listOf("Sama dengan sebelumnya", "All Grade", "Super", "A", "AB", "C")
        val adapterGrade = ArrayAdapter(this, R.layout.item_list_dropdown, itemsGrade)
        binding.gradeDropDown.setAdapter(adapterGrade)
    }

    private fun displayDropDownDistribution() {
        val itemsDistribution = listOf("Seluruh Produk", "Sebagian Produk")
        val adapterDistribution = ArrayAdapter(this, R.layout.item_list_dropdown, itemsDistribution)
        binding.distributionDropDown.setAdapter(adapterDistribution)
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
                binding.weightLossEt.requestFocus()

                //munculkan keyboard
                val keyboardShow = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboardShow.showSoftInput(binding.weightLossEt, InputMethodManager.SHOW_IMPLICIT)
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
}