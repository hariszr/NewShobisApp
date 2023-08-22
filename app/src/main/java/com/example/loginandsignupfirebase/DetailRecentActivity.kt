package com.example.loginandsignupfirebase

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.loginandsignupfirebase.databinding.ActivityDetailRecentBinding
import com.example.loginandsignupfirebase.model.DataClassAdd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class DetailRecentActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailRecentBinding
    private lateinit var datalist : ArrayList<DataClassAdd>
    private lateinit var adapter: ListAdapterDetail
    private lateinit var firebaseAuth : FirebaseAuth
    var databaseReference: DatabaseReference? = null
    private lateinit var databaseReference2: DatabaseReference
    var eventListener: ValueEventListener? = null
    private lateinit var dialog : AlertDialog
    var QrCode = ""
    var imageUrl = ""
    var PIDNode: String? = ""
    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRecentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.backProductListIV.setOnClickListener {
//            startActivity(Intent(this, TraceabilityListActivity::class.java))
            finish()
        }
        PIDNode = intent.extras?.getString("PIDNode")
        progressLoad()

        val gridLayoutManager = GridLayoutManager(this@DetailRecentActivity, 1)
        binding.listDetailRecyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this@DetailRecentActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.layout_progress)
        val dialog = builder.create()
        dialog.show()


        datalist = ArrayList()
        adapter = ListAdapterDetail(datalist)
        binding.listDetailRecyclerView.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.uid.toString()).child("Recent Scan").child(PIDNode.toString()) /** ini yang buat error, masih di setting "pid" referensi child nya, harusnya recent itu yang di child "Recent Scan" **/
        databaseReference2 = FirebaseDatabase.getInstance().getReference("users")
        dialog.show()

        eventListener = databaseReference!!.child("Secondary Data").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                datalist.clear()
                for (itemSnapshot in snapshot.children) {
                    println(PIDNode.toString())
                    println(itemSnapshot.toString())
                    val dataClass = itemSnapshot.getValue(DataClassAdd::class.java)
                    if (dataClass != null) {
                        datalist.add(dataClass)
                    }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }

        })

        binding.addDataBtn.setOnClickListener {
            checkConsumer()
        }

        checkActorToDownload()

        binding.downloadBtn.setOnClickListener {
            getDataQrCodeUpdate()
        }

//        databaseReference!!.addChildEventListener(object : ChildEventListener {
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                // Mendapatkan data dari setiap node anak
//                val data = snapshot.getValue(DataClassAdd::class.java)
//                if (data != null) {
//                    datalist.add(data)
//                }
//                adapter.notifyDataSetChanged()
//                dialog.dismiss()
//                // Lakukan sesuatu dengan data yang diperoleh
//                // ...
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                // Dilakukan ketika ada perubahan pada data node anak
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//                // Dilakukan ketika ada node anak yang dihapus
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                // Dilakukan ketika posisi node anak berubah
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Penanganan kesalahan
//                Log.d("Firebase", "Error: ${error.message}")
//                dialog.dismiss()
//            }
//        })

    }

    private fun checkConsumer() {
        databaseReference2.child(firebaseAuth.uid.toString()).child("Profile Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val actor = dataSnapshot.child("levelUser").getValue(String::class.java)

                    println("get actor : ${actor.toString()}")
                    if (actor == "Konsumen" || actor == "UMKM") {
//                        dialog = AlertDialog.Builder(this@DetailRecentActivity)
//                            .setTitle("Consumer and UMKM")
//                            .setMessage("You are not allowed to access this")
//                            .setCancelable(true)
//                            .setPositiveButton("Close") {dialogInterface, it ->
//                                dialogInterface.cancel()
//                            }
//                            .show()
                        binding.addDataBtn.visibility = View.GONE
                        return
                    } else {
                        val intent = Intent(this@DetailRecentActivity, AddTraceabilityActivity::class.java)
                        intent.putExtra("sendPID", PIDNode)
                        startActivity(intent)
                    }
                } else {
                    dialog = AlertDialog.Builder(this@DetailRecentActivity)
                        .setTitle("User Profile is Empty")
                        .setMessage("Please, complete your user profile first!")
                        .setCancelable(true)
                        .setPositiveButton("Yes") {dialogInterface, it ->
                            startActivity(Intent(this@DetailRecentActivity, ProfileActivity::class.java))
                        }
                        .setNegativeButton("No") {dialogInterface, it ->
                            dialogInterface.cancel()
                        }
                        .show()
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Check Actor : ${error.message}")
                Log.e("Check Actor", "Error when check actor: ${error.message}")
            }
        })
    }

    private fun checkActorToDownload() {
        databaseReference2.child(firebaseAuth.uid.toString()).child("Profile Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val actor = dataSnapshot.child("levelUser").getValue(String::class.java)

                    println("get actor : ${actor.toString()}")
                    if (actor == "Konsumen" || actor == "UMKM") {
                        binding.downloadBtn.visibility = View.VISIBLE
                        binding.addDataBtn.visibility = View.GONE
                        return
                    } else if (actor == "Pasar Induk" || actor == "Pasar Tradisional" || actor == "Pasar Modern" ||actor == "E-Commerce"  ) {
                        binding.downloadBtn.visibility = View.VISIBLE
                        binding.addDataBtn.visibility = View.VISIBLE
                        return
                    } else {
                        binding.downloadBtn.visibility = View.GONE
                        return
                    }
                } else {
                    dialog = AlertDialog.Builder(this@DetailRecentActivity)
                        .setTitle("User Profile is Empty")
                        .setMessage("Please, complete your user profile first!")
                        .setCancelable(true)
                        .setPositiveButton("Yes") {dialogInterface, it ->
                            startActivity(Intent(this@DetailRecentActivity, ProfileActivity::class.java))
                        }
                        .setNegativeButton("No") {dialogInterface, it ->
                            dialogInterface.cancel()
                        }
                        .show()
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Check Actor : ${error.message}")
                Log.e("Check Actor", "Error when check actor: ${error.message}")
            }
        })
    }

    private fun getDataQrCodeUpdate() {
        databaseReference!!.child("dataQrCodeUpdate").addListenerForSingleValueEvent(object : ValueEventListener {

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onDataChange(snapshot: DataSnapshot) {
                // Mendapatkan nilai data dari snapshot
                val value = snapshot.getValue(String::class.java)
                downloadImageAndSaveAsPdf(value)
                // Lakukan sesuatu dengan nilai data, misalnya tampilkan di logcat
                Log.d("download Qr Code", "Get Value Url: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Tangani kesalahan jika ada
                Log.e("download Qr Code", "Error: ${error.message}")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun downloadImageAndSaveAsPdf(linkUrl : String?) {

        // CoroutineScope untuk melaksanakan tugas di latar belakang
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Mengunduh gambar dari URL menggunakan Glide
                val bitmap = withContext(Dispatchers.IO) {
                    Glide.with(applicationContext)
                        .asBitmap()
                        .load(linkUrl)
                        .submit()
                        .get()
                }

                // Lokasi penyimpanan file PDF di direktori "Downloads" di penyimpanan eksternal
                val timeStamp = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(Date())
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val pdfFile = File(downloadDir,"ShobisApp_QRCode_$timeStamp.pdf")

                // Membuat dokumen PDF
                val document = Document(PageSize.A4)
                val pdfWriter = withContext(Dispatchers.IO) {
                    PdfWriter.getInstance(document, FileOutputStream(pdfFile))
                }
                document.open()

                // Mengonversi gambar menjadi Image dalam dokumen PDF
                val image = Image.getInstance(bitmapToByteArray(bitmap))
                image.scaleToFit(PageSize.A4)
                image.alignment = Element.ALIGN_CENTER
                document.add(image)

                // Tutup dokumen PDF
                document.close()

                // Pindah ke thread UI untuk menampilkan pesan selesai jika perlu
                CoroutineScope(Dispatchers.Main).launch {
                    // Tampilkan pesan bahwa proses telah selesai
                    // Misalnya, Anda dapat menampilkan notifikasi atau pesan Toast di sini
                    Log.d("Convert", "Convert Qr Code to PDF successfully ${pdfFile.absolutePath}")
                    Toast.makeText(this@DetailRecentActivity, "Berhasil disimpan, lihat ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                // Tangani kesalahan jika ada
                e.printStackTrace()
            }
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }


    private fun progressLoad() {
        val builder = AlertDialog.Builder(this@DetailRecentActivity)
        builder.setCancelable(false).setView(R.layout.layout_progress)
        val dialog = builder.create()
        dialog.show()
        showData()
        dialog.dismiss()
    }

    private fun showData() {
        val bundle = intent.extras
        if (bundle != null){
            QrCode = bundle.getString("QrCodeUpdate")!!
            Glide.with(this).load(bundle.getString("QrCodeUpdate")).into(binding.qrCodeDetailIV)

            imageUrl = bundle.getString("PictProduct")!!
            Glide.with(this).load(bundle.getString("PictProduct")).into(binding.pictDetailIV)

            binding.productIDTV.text = bundle.getString("PID")
            binding.varietyTV.text = bundle.getString("Variety")
            binding.weightTV.text = bundle.getString("Weight")
            binding.gradeTV.text = bundle.getString("Grade")
            binding.priceTV.text = bundle.getString("Price")

            binding.farmerTV.text = bundle.getString("Farmer")
            binding.dayTV.text = bundle.getString("Day")
            binding.areaTV.text = bundle.getString("Area")
            binding.fertilizerTV.text = bundle.getString("Fertilizer")
            binding.pesticidesTV.text = bundle.getString("Pesticides")

            binding.dateCreateTV.text = bundle.getString("Date")
            binding.notesTV.text = bundle.getString("Note")

            binding.creatorTV.text = bundle.getString("Creator")
            binding.actorTV.text = bundle.getString("Actor")
            binding.emailTV.text = bundle.getString("Email")
            binding.companyTV.text = bundle.getString("Company")
            binding.addressTV.text = bundle.getString("Address")

        }
    }
}
