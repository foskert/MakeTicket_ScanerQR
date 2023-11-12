package com.maketicket.qrscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.maketicket.qrscaner.databinding.ActivityReportDetailOrderBinding
import com.maketicket.qrscaner.databinding.ActivityReportVentasBinding
import com.maketicket.qrscaner.ui.model.ReportDetailOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReportDetailORderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportDetailOrderBinding
    private lateinit var  n_orders: TextView
    private lateinit var  n_open: TextView
    private lateinit var  n_verification: TextView
    private lateinit var  n_payment: TextView
    private lateinit var  n_print: TextView
    private lateinit var  n_no_print: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        n_orders = findViewById(R.id.n_orders)
        n_open = findViewById(R.id.n_open)
        n_verification = findViewById(R.id.n_verification)
        n_payment = findViewById(R.id.n_payment)
        n_print = findViewById(R.id.n_print)
        n_no_print = findViewById(R.id.n_no_print)
        binding.btnMenuReturn.setOnClickListener{
            finish()
        }
        loadReportDeetailOrders()
    }

    private fun loadReportDeetailOrders() {
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).reportDetailOrder(QRSanerApplication.preference.getKeyValue(), QRSanerApplication.preference.getIdEvent())
            val response =call.body()
            runOnUiThread {
                if(call.isSuccessful) {
                    if (response != null) {
                        iniReportDetailOrder(response)
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

    private fun iniReportDetailOrder(report: ReportDetailOrder) {
        if(report.ordenes != null){
            n_orders.setText(report.ordenes.toString())
        }
        if(report.abiertas != null){
            n_open.setText(report.abiertas.toString())
        }
        if(report.verificacion != null){
            n_verification.setText(report.verificacion.toString())
        }
        if(report.pagado != null){
            n_payment.setText(report.pagado.toString())
        }
        if(report.impreso != null){
            n_print.setText(report.impreso.toString())
        }
        if(report.noimpreso != null){
            n_no_print.setText(report.noimpreso.toString())
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