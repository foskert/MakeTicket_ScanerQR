package com.maketicket.qrscaner.ui.order_dni

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ActivityDetailSillaBinding
import com.maketicket.qrscaner.databinding.ActivityOrderDniBinding
import com.maketicket.qrscaner.ui.detail_silla.DetailSillaAdapter
import com.maketicket.qrscaner.ui.model.OrderDNI
import com.maketicket.qrscaner.ui.model.Silla
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OrderDniActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDniBinding
    private lateinit var adpater_order_dni: OrderDniAdapter
    private val dogOrderDni = mutableListOf<OrderDNI>()

    private lateinit var imageLoader: ImageView
    private lateinit var loading_progessbar: ProgressBar
    private lateinit var viewDisableLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDniBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReturnDetailSilla.setOnClickListener{
            finish()
        }
        binding.textDescrptionOrderDni.setText("Debe ingresar un número de documento")
        loading_progessbar = findViewById(R.id.loading_progessbar)
        loading_progessbar.visibility = View.GONE
        imageLoader = findViewById(R.id.loading)
        viewDisableLayout = findViewById(R.id.viewDisableLayout)
        imageLoader.visibility = View.GONE
        binding.btnSearch.setOnClickListener {
            if(!binding.editDni.text.toString().equals("")) {
                imageLoader.visibility = View.VISIBLE
                loading_progessbar.visibility = View.VISIBLE
                getOrderDni(binding.editDni.text.toString())
            }
        }
        if(!QRSanerApplication.preference.getKeyValue().equals("") && !QRSanerApplication.preference.getIdArticle().equals("")) {
            iniRecycleView()
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
    private fun iniRecycleView() {
        adpater_order_dni = OrderDniAdapter(dogOrderDni, this, loading_progessbar, viewDisableLayout)
        binding.listOrderDni.layoutManager= LinearLayoutManager(this)
        binding.listOrderDni.adapter = adpater_order_dni
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getOrderDni(dni: String) {
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getOrderDNI(QRSanerApplication.preference.getKeyValue(), QRSanerApplication.preference.getIdEvent(), dni)
            val response =call.body()
            runOnUiThread {
                imageLoader.visibility = View.GONE
                loading_progessbar.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("LIST_ORDER_DNI", "respuesta $response")

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
        iten: ArrayList<OrderDNI>?
    ) {
        dogOrderDni.clear()
        if (iten != null) {
            dogOrderDni.addAll(iten)
        }
        if(iten?.size==0){
            binding.textDescrptionOrderDni.setText("El Documento seleccionado no posee ordenes de compra para el evento")
        }
        adpater_order_dni.notifyDataSetChanged()

    }
}