package com.maketicket.qrscaner.ui.order_entry

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ActivityMesaSillaBinding
import com.maketicket.qrscaner.databinding.ActivityOrderEntryBinding
import com.maketicket.qrscaner.ui.mesa.silla.MesaSillaAdapter
import com.maketicket.qrscaner.ui.model.MesaSilla
import com.maketicket.qrscaner.ui.model.OrderEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OrderEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderEntryBinding
    private lateinit var adpater_order_entry: OrderEntryAdapter
    private val dogOrderEntry = mutableListOf<OrderEntry>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReturnDetailSilla.setOnClickListener {
            finish()
        }
        if(!QRSanerApplication.preference.getKeyValue().equals("") && !QRSanerApplication.preference.getIdArticle().equals("")) {
            iniRecycleViewMesaSilla()
            getOrderEntry()
        }else{
            showResponse("No posee credenciales")
        }

    }
    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.makeidsystems.com/makeid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun showResponse(body: String?) {
        Toast.makeText(this, "$body", Toast.LENGTH_LONG).show()
    }
    private fun iniRecycleViewMesaSilla() {
        adpater_order_entry = OrderEntryAdapter(dogOrderEntry, this)
        binding.listOrderEntry.layoutManager= LinearLayoutManager(this)
        binding.listOrderEntry.adapter = adpater_order_entry
    }
    private fun getOrderEntry(){
        binding.loadingProgessbar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getOrderEntry(
                QRSanerApplication.preference.getKeyValue(), QRSanerApplication.preference.getIdEvent())
            val response =call.body()
            runOnUiThread {
                binding.loadingProgessbar.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("LIST_MESA_TABLE", "respuesta $response")
                        load(response)
                    }else{
                        showResponse("Error de respuesta  ")
                    }
                }else{
                    showResponse("Error de conexión ")
                    //finish()
                    //onBackPressed()
                }
            }
        }
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun load(
        iten: ArrayList<OrderEntry>?
    ) {
        if (iten != null) {
            dogOrderEntry.clear()
            dogOrderEntry.addAll(iten)
            Snackbar.make(binding.root , "Total de ordenes por ingresar: ${dogOrderEntry.size}  ", Snackbar.LENGTH_LONG)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("Cerrar.")

                }  ).show()
        }else{
            Snackbar.make(binding.root , "No existe data para de evento  ", Snackbar.LENGTH_LONG)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("Cerrar.")

                }  ).show()
        }
        adpater_order_entry.notifyDataSetChanged()
    }
}