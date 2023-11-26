package com.maketicket.qrscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.maketicket.qrscaner.databinding.ActivityReportIcomesBinding
import com.maketicket.qrscaner.databinding.ActivityReportVentasBinding
import com.maketicket.qrscaner.ui.model.ReportVentas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class ReportVentasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportVentasBinding
    private lateinit var  n_aforo: TextView
    private lateinit var  date_aforo: TextView
    private lateinit var  n_ordenes: TextView
    private lateinit var  date_ordenes: TextView
    private lateinit var  n_bloqueo: TextView
    private lateinit var  date_bloqueo: TextView
    private lateinit var  n_dia: TextView
    private lateinit var  date_dia: TextView
    private lateinit var  n_ayer: TextView
    private lateinit var  date_ayer: TextView
    private lateinit var  n_cortesia: TextView
    private lateinit var  date_cortesia: TextView
    private lateinit var  n_descuento: TextView
    private lateinit var  date_descuento: TextView
    private lateinit var  n_disponible: TextView
    private lateinit var  date_disponible: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportVentasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        n_aforo = findViewById(R.id.n_aforo)
        date_aforo = findViewById(R.id.date_text_aforo)
        n_ordenes = findViewById(R.id.n_ordenes)
        date_ordenes = findViewById(R.id.date_text_ordenes)
        n_bloqueo = findViewById(R.id.n_bloqueo)
        date_bloqueo = findViewById(R.id.date_text_bloqueo)
        n_dia = findViewById(R.id.n_dia)
        date_dia = findViewById(R.id.date_text_dia)
        n_ayer = findViewById(R.id.n_ayer)
        date_ayer = findViewById(R.id.date_text_ayer)
        n_cortesia = findViewById(R.id.n_cortesia)
        date_cortesia = findViewById(R.id.date_text_cortesia)
        n_descuento = findViewById(R.id.n_descuento)
        date_descuento = findViewById(R.id.date_text_descuento)
        n_disponible = findViewById(R.id.n_disponible)
        date_disponible = findViewById(R.id.date_text_disponible)

        binding.btnMenuReturn.setOnClickListener{
            finish()
        }
        loadReportVentas()
    }
    private fun loadReportVentas() {
        binding.loadingProgessbar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).reportVentas(QRSanerApplication.preference.getKeyValue(), QRSanerApplication.preference.getIdEvent())
            val response =call.body()
            runOnUiThread {
                binding.loadingProgessbar.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success==true){
                            response.data?.let { iniReportVentas(it) }
                        }else{
                            showResponse(response.status.toString())
                        }

                    }else{
                        showResponse("Error respuesta nula ")
                    }
                }else{
                    showResponse("Error de conexión ")
                    //finish()
                    //onBackPressed()
                }
            }
        }

    }

    private fun iniReportVentas(report: ReportVentas) {
        val formatter = SimpleDateFormat("d/m/y hh:mm a")
        val time = formatter.format(Date())
        var acum = 0
        n_aforo.setText(report.aforo.toString())
        date_aforo.setText(time.toString())
        n_ordenes.setText(report.ordenes.toString())
        acum += report.ordenes
        date_ordenes.setText(time.toString())
        if(report.bloqueo != null){
            n_bloqueo.setText(report.bloqueo.toString())
            acum += report.bloqueo
        }
        date_bloqueo.setText(time.toString())
        if(report.ordenesDia != null){
            n_dia.setText(report.ordenesDia.toString())
            acum += report.ordenesDia
        }
        date_dia.setText(time.toString())
        if(report.ordenesAyer != null){
            n_ayer.setText(report.ordenesAyer.toString())
            acum += report.ordenesAyer
        }
        date_ayer.setText(time.toString())
        if(report.ordenesCortesia != null){
            n_cortesia.setText(report.ordenesCortesia.toString())
            acum += report.ordenesCortesia
        }
        date_cortesia.setText(time.toString())
        if(report.ordenesDescuento != null){
            n_descuento.setText(report.ordenesDescuento.toString())
            acum += report.ordenesDescuento
        }
        date_descuento.setText(time.toString())
        val disponible = (report.aforo!!.toInt() -  acum)
        n_disponible.setText(disponible.toString())
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

}