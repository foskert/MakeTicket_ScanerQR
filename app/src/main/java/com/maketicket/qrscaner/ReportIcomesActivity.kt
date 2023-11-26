 package com.maketicket.qrscaner

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.maketicket.qrscaner.databinding.ActivityReportIcomesBinding
import com.maketicket.qrscaner.ui.model.ReportIcomes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

 class ReportIcomesActivity : AppCompatActivity() {
     private lateinit var binding: ActivityReportIcomesBinding
     private lateinit var  n_recaudados: TextView
     private lateinit var  n_recaudados_dia: TextView
     private lateinit var  date_recaudados: TextView
     private lateinit var  date_recaudados_dia: TextView



     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportIcomesBinding.inflate(layoutInflater)
        setContentView(binding.root)

         n_recaudados = findViewById(R.id.n_recaudos)
         n_recaudados_dia = findViewById(R.id.n_recaudos_dia)
         date_recaudados = findViewById(R.id.date_text_recaudos)
         date_recaudados_dia = findViewById(R.id.date_text_recaudos_dia)
         binding.btnMenuReturn.setOnClickListener{
             finish()
         }
        loadReportIcomet()

     }
     @SuppressLint("NotifyDataSetChanged")
     private fun loadReportIcomet() {
         binding.loadingProgessbar.visibility = View.VISIBLE
         CoroutineScope(Dispatchers.IO).launch{
             val call = getRestEngine().create(OrderEntryService:: class.java).reportIcomes(QRSanerApplication.preference.getKeyValue(), QRSanerApplication.preference.getIdEvent())
             val response =call.body()
             runOnUiThread {
                 binding.loadingProgessbar.visibility = View.GONE
                 if(call.isSuccessful) {
                     if (response != null) {
                         if(response.success==true) {
                             iniReportIcomet(response.data!!)
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
     private fun iniReportIcomet(report: ReportIcomes) {
         val formatter = SimpleDateFormat("d/m/y hh:mm a")
         val time = formatter.format(Date())
         date_recaudados.setText(time.toString())
         date_recaudados_dia.setText(time.toString())
         n_recaudados.setText(report.recaudado)
         if(report.recaudadodia != null){
             n_recaudados_dia.setText(report.recaudadodia)

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
 }