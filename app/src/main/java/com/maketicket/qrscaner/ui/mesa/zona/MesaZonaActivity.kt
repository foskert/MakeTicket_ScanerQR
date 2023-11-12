package com.maketicket.qrscaner.ui.mesa.zona

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.databinding.ActivityDetailSillaBinding
import com.maketicket.qrscaner.databinding.ActivityMesaZonaBinding
import com.maketicket.qrscaner.ui.detail_silla.DetailSillaAdapter
import com.maketicket.qrscaner.ui.model.MesaZona
import com.maketicket.qrscaner.ui.model.MySQLiteHelper
import com.maketicket.qrscaner.ui.model.Silla
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MesaZonaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMesaZonaBinding
    lateinit var text_name_article: TextView
    private lateinit var adpater_mesa_zona: MesaZonaAdapter
    private val dogMesaZona = mutableListOf<MesaZona>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesaZonaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReturnDetailSilla.setOnClickListener {
            finish()
        }
        if(!QRSanerApplication.preference.getKeyValue().equals("") && !QRSanerApplication.preference.getIdArticle().equals("")) {
            iniRecycleViewMesaZona()
            getMesaZona()
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
    private fun iniRecycleViewMesaZona() {
        adpater_mesa_zona = MesaZonaAdapter(dogMesaZona, this)
        binding.listMesaZonas.layoutManager= LinearLayoutManager(this)
        binding.listMesaZonas.adapter = adpater_mesa_zona
    }
    private fun getMesaZona(){
        binding.loadingProgessbar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{

            val call = getRestEngine().create(OrderEntryService:: class.java).getMesaZonas(QRSanerApplication.preference.getKeyValue(), QRSanerApplication.preference.getIdEvent())
            val response =call.body()
            runOnUiThread {
                binding.loadingProgessbar.visibility = View.GONE

                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("LIST_MESA_ZONA", "respuesta $response")

                        loadMesaZona(response)
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
    private fun loadMesaZona(
        mesa_zona: ArrayList<MesaZona>?
    ) {
        dogMesaZona.clear()
        if (mesa_zona != null) {
            dogMesaZona.addAll(mesa_zona)
        }
        adpater_mesa_zona.notifyDataSetChanged()

    }
}