package com.example.loginandsignupfirebase

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.loginandsignupfirebase.databinding.ActivityAddTraceabilityBinding
import com.example.loginandsignupfirebase.model.DataClassAdd
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
        binding.priceEt.setMaskingMoney("Rp. ")

        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateLabel(myCalendar)
        }

        val myCalendar2 = Calendar.getInstance()
        val datePicker2 = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar2.set(Calendar.YEAR, year)
            myCalendar2.set(Calendar.MONTH, month)
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateLabel2(myCalendar2)
        }

        binding.arriveDateEt.setOnClickListener {
            val dPicker = DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH))
            dPicker.datePicker.maxDate = System.currentTimeMillis()
            dPicker.show()
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
            val builder = AlertDialog.Builder(this@AddTraceabilityActivity)
            builder.setCancelable(false).setView(R.layout.layout_progress)
            val dialog = builder.create()
            dialog.show()
//            validationInputData()
            initCopy()
//            saveData()
            dialog.dismiss()
        }
    }

    private fun validationInputData() {
        TODO("Not yet implemented")
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadData(pid: String) {

        val arriveDate = binding.arriveDateEt.text.toString()
        val incomingWeight = binding.incomingWeightEt.text.toString()
        val grade = binding.gradeDropDown.text.toString()
        val price = binding.priceEt.text.toString()

        val outgoingWeight = binding.outgoingWeightEt.text.toString()
        val weightLoss = binding.weightLossEt.text.toString()
        val outgoingDate = binding.outgoingDateEt.text.toString()

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
                    pid, imageURL, arriveDate, incomingWeight, grade, price,
                    outgoingWeight, weightLoss, outgoingDate, dateCreate,
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
                                    println("Data Secondary Successfully Updated to PID Server")
                                    Log.i( "Server Updated","Data Secondary Successfully Updated to PID Server")
                                }
                            }.addOnFailureListener { e ->
                                Toast.makeText(this@AddTraceabilityActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                                println("Data Secondary Failed Update to PID Server")
                                Log.e( "Server Updated","Data Secondary Failed Update to PID Server")
                            }

                            Toast.makeText(this@AddTraceabilityActivity, "Created", Toast.LENGTH_SHORT).show()
                            Toast.makeText(this@AddTraceabilityActivity, "get $imageURL successfully from firebase", Toast.LENGTH_SHORT).show()
                            count += 1
                            println("${count} 2 URL unduhan gambar: $imageURL")
                            startActivity(Intent(this@AddTraceabilityActivity, TraceabilityListActivity::class.java))
                            finish()
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
                // Berhasil menyimpan nama ke Firebase
                Log.i(
                    "imageUrl",
                    "Success upload image to storage and firebase realtime server"
                )
                // Simpan juga ke dalam user
                firebaseRef.child(firebaseAuth.uid.toString()).child("pid").updateChildren(childUpdates)
                    .addOnCompleteListener {
                        Log.i(
                            "imageUrl",
                            "Success upload image to storage and firebase realtime user")
                    }.addOnFailureListener { exception ->
                        Log.e(
                            "imageUrl",
                            "Something wrong with save QrCode Update to firebase realtime user: ${exception.message}")
                    }
            }.addOnFailureListener { exception ->
                Log.e(
                    "imageUrl",
                    "Something wrong with save QrCode Update to firebase realtime server: ${exception.message}")
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
        val itemsGrade = listOf("Same with Previous", "All Grade", "Super", "A", "AB", "C")
        val adapterGrade = ArrayAdapter(this, R.layout.item_list_dropdown, itemsGrade)
        binding.gradeDropDown.setAdapter(adapterGrade)
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