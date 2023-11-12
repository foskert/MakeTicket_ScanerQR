package com.maketicket.qrscaner.ui.detail_silla.funtion

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ActivityFuntionBinding
import com.maketicket.qrscaner.ui.response.funciones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FuntionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFuntionBinding
    lateinit var text_name_article: TextView
    private lateinit var imageLoader: ImageView
    private lateinit var adpater_function: FuntionAdapter
    private val dogFuntion = mutableListOf<funciones>()
    private lateinit var loading_progessbar: ProgressBar
    private lateinit var viewDisableLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFuntionBinding.inflate(layoutInflater)
        binding.textFunctionEvent.setText(QRSanerApplication.preference.getNameEvent())
        setContentView(binding.root)
        binding.btnReturnDetailSilla.setOnClickListener {
            finish()
        }
        loading_progessbar = findViewById(R.id.loading_progessbar)
        loading_progessbar.visibility = View.GONE
        imageLoader = findViewById(R.id.loading)
        viewDisableLayout = findViewById(R.id.viewDisableLayout)
        imageLoader.visibility = View.GONE
        if(!QRSanerApplication.preference.getKeyValue().equals("") && !QRSanerApplication.preference.getIdArticle().equals("")) {
            iniRecycleView()
            getFuction()
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
        adpater_function = FuntionAdapter(dogFuntion, this, "", "silla")
        binding.listFunctionSilla.layoutManager= LinearLayoutManager(this)
        binding.listFunctionSilla.adapter = adpater_function
    }
    private fun getFuction() {
        binding.loadingProgessbar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getOrderAdmitted( QRSanerApplication.preference.getIdEvent(), QRSanerApplication.preference.getKeyValue())
            val response =call.body()
            runOnUiThread {
                binding.loadingProgessbar.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success){
                            load(response.funciones)
                            Log.d("LIST_FUNCTION", "respuesta ${response.funciones}")
                        }else{
                            showResponse("No hay datos disponibles para el evento")
                            Log.d("LIST_FUNCTION", "respuesta success false")
                        }
                    }else{
                        showResponse("Error de respuesta")
                        Log.d("LIST_FUNCTION", "respuesta error de respuesta")
                    }
                    // showResponse(dogCode.size.toString())
                }else{
                    showResponse("Error de conexión ")
                    Log.d("LIST_FUNCTION", "respuesta error de conexion")
                }
            }
        }
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun load(
        funtion: ArrayList<funciones>?
    ) {
        dogFuntion.clear()
        /*if (funtion != null) {
            dogFuntion.addAll(funtion)
        }*/
        if (funtion != null) {
            dogFuntion.addAll(funtion)

            Snackbar.make(binding.root , "Total de eventos disponibles: ${dogFuntion.size}  ", Snackbar.LENGTH_LONG)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("Cerrar.")

                }  ).show()
        }else{
            Snackbar.make(binding.root , "No existe data para de evento  ", Snackbar.LENGTH_LONG)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("Cerrar.")

                }  ).show()
        }
        adpater_function.notifyDataSetChanged()
    }
}

