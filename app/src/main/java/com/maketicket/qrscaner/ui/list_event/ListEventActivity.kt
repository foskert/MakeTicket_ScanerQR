package com.maketicket.qrscaner.ui.list_event

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.databinding.ActivityListEventBinding
import com.maketicket.qrscaner.ui.model.EventDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListEventBinding
    private lateinit var adpater: ListEventAdapter
    private val dogList = mutableListOf<EventDetail>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityListEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReturnDetailSilla.setOnClickListener {
            finish()
        }
        val bundle   = intent.extras
        if(!QRSanerApplication.preference.getKeyValue().equals("") ) {
            iniRecycleView()
            getList()
        }else{
            showResponse("No posee credenciales")
        }
    }
    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.maketicket.com.ve/api/makeidsystems/external/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun showResponse(body: String?) {
        Toast.makeText(this, "$body", Toast.LENGTH_LONG).show()
    }
    private fun iniRecycleView() {
        adpater = ListEventAdapter(dogList, this)
        binding.listEvent.layoutManager= LinearLayoutManager(this)
        binding.listEvent.adapter = adpater
    }
    private fun getList(){
        binding.loadingProgessbar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java)
                .getEventList("makeidsystems", "63b4b3eba51e48.20916573")
            val response =call.body()
            runOnUiThread {
                binding.loadingProgessbar.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("LIST_EVENT_DATA", response.data.size.toString())
                        load(response.data)
                    }else{
                        showResponse("Error de respuesta  ")
                    }
                }else{
                    showResponse("Error de conexión ")
                }
            }
        }
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun load(
        list: ArrayList<EventDetail>?
    ) {
        if (list != null) {
            dogList.clear()
            dogList.addAll(list)

            Snackbar.make(binding.root , "Total de eventos disponibles: ${dogList.size}  ", Snackbar.LENGTH_LONG)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("Cerrar.")

                }  ).show()
        }else{
            Snackbar.make(binding.root , "No existe data para de evento  ", Snackbar.LENGTH_LONG)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("Cerrar.")

                }  ).show()
        }
        adpater.notifyDataSetChanged()
    }
}