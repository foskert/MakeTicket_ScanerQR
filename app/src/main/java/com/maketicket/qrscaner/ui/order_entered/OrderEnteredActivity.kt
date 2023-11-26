package com.maketicket.qrscaner.ui.order_entered

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ActivityOrderEnteredBinding
import com.maketicket.qrscaner.ui.model.OrderEntered
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OrderEnteredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderEnteredBinding
    private lateinit var adpater: OrderEnteredAdapter
    private val dogOrderEntered = mutableListOf<OrderEntered>()
    private lateinit var loading_progessbar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderEnteredBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReturnDetailSilla.setOnClickListener{
            finish()
        }
        loading_progessbar = findViewById(R.id.loading_progessbar)
        if(!QRSanerApplication.preference.getKeyValue().equals("") && !QRSanerApplication.preference.getIdEvent().equals("")) {
            iniRecycleView()
            getOrderEntered()
        }else{
            showResponse("No posee las credenciales necesarias")
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
    private fun iniRecycleView() {
        adpater = OrderEnteredAdapter(dogOrderEntered)
        binding.listOrderEntered.layoutManager= LinearLayoutManager(this)
        binding.listOrderEntered.adapter = adpater
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getOrderEntered() {
        loading_progessbar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getOrderEntered(
                QRSanerApplication.preference.getKeyValue(),
                QRSanerApplication.preference.getIdEvent()
            )
            val response =call.body()
            runOnUiThread {
                loading_progessbar.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success==true){
                            load(response.orderEntered)
                        }else{
                            showResponse("No existe data para el evento  "+QRSanerApplication.preference.getNameEvent())
                        }
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
        orderEntered: ArrayList<OrderEntered>?
    ) {
        if (orderEntered != null) {
            dogOrderEntered.clear()
            dogOrderEntered.addAll(orderEntered)
            Snackbar.make(binding.root , "Total de ordenes ingresadas: ${orderEntered.size}  ", Snackbar.LENGTH_INDEFINITE)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("cerrar.")

                }  ).show()
        }else{
            Snackbar.make(binding.root , "No existe data para el evento  "+QRSanerApplication.preference.getNameEvent(), Snackbar.LENGTH_INDEFINITE)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("cerrar.")

                }  ).show()
        }
        adpater.notifyDataSetChanged()

    }

}