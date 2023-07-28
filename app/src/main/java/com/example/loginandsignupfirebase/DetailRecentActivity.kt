package com.example.loginandsignupfirebase

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.loginandsignupfirebase.databinding.ActivityDetailRecentBinding
import com.example.loginandsignupfirebase.model.DataClassAdd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*
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
    var eventListener: ValueEventListener? = null
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
            val intent = Intent(this@DetailRecentActivity, AddTraceabilityActivity::class.java)
            intent.putExtra("sendPID", PIDNode)
            startActivity(intent)
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
