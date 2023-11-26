package com.maketicket.qrscaner

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.maketicket.qrscaner.databinding.ActivityOrderDetailBinding
import com.maketicket.qrscaner.ui.model.MySQLiteHelper
import com.maketicket.qrscaner.ui.model.Partaker
import com.maketicket.qrscaner.ui.order_detail.OrderDetailArticleAdapter
import com.maketicket.qrscaner.ui.order_detail.OrderDetailCodeQRAdapter
import com.maketicket.qrscaner.ui.order_detail.OrderDetailLocationAdapter
import com.maketicket.qrscaner.ui.response.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OrderDetailActivity : AppCompatActivity() {
    //private var compartir:Boolean = false
    private lateinit var info: String
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var imagen_toolbar: ImageView
    private lateinit var imagen_toolbar_return: ImageView
    private lateinit var adpater_code_qr: OrderDetailCodeQRAdapter
    private val dogCodeQR = mutableListOf<PurchaseOrderTicket>()
    private lateinit var adpater_article: OrderDetailArticleAdapter
    private lateinit var adpater_location: OrderDetailLocationAdapter
    private val dogArticle = mutableListOf<Articles>()
    private val dogLocation = mutableListOf<Location>()
    private lateinit var DBHelper:MySQLiteHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //compartir = false
        info = ""
        imagen_toolbar = findViewById(R.id.img_toolbar)
        imagen_toolbar_return = findViewById(R.id.img_toolbar_return)
        val send: Boolean = intent.getBooleanExtra("SEND", false)
        val code: String = intent.getStringExtra("CODE").toString()
        val info: String = intent.getStringExtra("ORDEN").toString()
        if (send){
            binding!!.checkAll.visibility = View.VISIBLE
            binding!!.sendCode.visibility = View.VISIBLE
        }
        //val chair: String = intent.getStringExtra("CHAIR").toString()
        binding.detailTextInfo.setText(info)


        //showResponse("el code enviado $code")
        returnView()
        iniRecycleViewCodeQR()
        iniRecycleViewArticle()
        compartir()
        /*if(!chair.equals("")){
            getCodeChair(chair)
        }else{*/
            getCodeValue(code)
        //}
        binding!!.checkAll.setOnClickListener {
            checkAllList(binding!!.checkAll.isChecked)
        }
        binding!!.sendCode.setOnClickListener{
            AlertDialog.Builder(this).apply {
                setTitle(R.string.alert)
                setMessage("¿Está seguro de realizar la operación?, se enviarán los códigos seleccionados para su verificación esta operación no se puede revertir")
                setNegativeButton("No", null)
                setPositiveButton("Si") { _: DialogInterface, _: Int ->
                    sendCodes()
                }
            }.show()
        }

    }

    private fun sendCodes() {
        var i=0
        dogCodeQR.forEach {
            if(it.check && !it.status.equals("Fue al evento")) {
                evalueCode(it.code.toString(), i)
                i++
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun evalueCode(code: String, i: Int) {
        var funciones:String=""
        var i=0
        DBHelper= MySQLiteHelper(this)
        DBHelper.ListFunction().forEach {
            if(i==0) {
                funciones += "${it.id_function}"
            }else{
                funciones += ",${it.id_function}"
            }
            i++
        }
        CoroutineScope(Dispatchers.IO).launch{
            val id_article:String
            if(QRSanerApplication.preference.getIdArticle()>0){
                id_article = QRSanerApplication.preference.getIdArticle().toString()
            }else{
                id_article = ""
            }
            val call = getRestEngine().create(OrderEntryService:: class.java).getOrderEntry(
                code,
                QRSanerApplication.preference.getKeyValue(),
                QRSanerApplication.preference.getIdEvent(),
                funciones
            )
            val response =call.body()
            runOnUiThread {
                if(call.isSuccessful) {
                    Log.d("FRAGMENT_ORDEN_isSuccessful", response.toString())
                    if (response != null) {
                        Log.d("FRAGMENT_ORDEN_RESPONSE", response.toString())
                        Log.d("FRAGMENT_ORDEN_RESPONSE", i.toString())
                        Log.d("FRAGMENT_ORDEN_RESPONSE_dogCodeQR", dogCodeQR[i].toString())
                        if(response.success== true) {
                            addTicketDB(code, response.status!!)
                            dogCodeQR[i].status = "Fue al evento"
                            adpater_code_qr.UpdateData(dogCodeQR)
                            adpater_code_qr.notifyDataSetChanged()
                            //aquibasededatos
                        }else{
                            showResponse(response.status.toString())
                        }
                    }else{
                        showResponse("Error de conexion")
                    }
                }
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun addTicketDB(code:String, status:String ) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date()).toString()

        DBHelper= MySQLiteHelper(this)
        DBHelper.addTicket(code, status, QRSanerApplication.preference.getKeyValue(), currentDate)
    }
    private fun checkAllList(value: Boolean) {
        dogCodeQR.forEach {
            it.check = value
        }
        adpater_code_qr.UpdateData(dogCodeQR)
        adpater_code_qr.notifyDataSetChanged()
    }

    private fun returnView() {
        imagen_toolbar_return.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "565656565")
            }
            finish()
            startActivity(intent)
        }
    }

    private fun compartir() {
        //if(compartir){
            imagen_toolbar.setOnClickListener {
                val intent = Intent().apply{
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, info)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, "DETALLE DE ORDEN")
                startActivity(shareIntent)
            }
        //}
    }

    private fun iniRecycleViewArticle() {
        adpater_article= OrderDetailArticleAdapter(dogArticle)
        binding.listOrderDetailArticle.layoutManager= LinearLayoutManager(this)
        binding.listOrderDetailArticle.adapter = adpater_article
        adpater_location= OrderDetailLocationAdapter(dogLocation)
        binding.listLocation.layoutManager= LinearLayoutManager(this)
        binding.listLocation.adapter = adpater_location
    }

    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.makeidsystems.com/makeid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getCodeValue(code:String){
        Log.d("DETALLEORDEN_CODE", code.toString())
        Log.d("DETALLEORDEN_KEY", QRSanerApplication.preference.getKeyValue())
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getOrderDetail(code, QRSanerApplication.preference.getKeyValue())
            val response =call.body()
            runOnUiThread {
                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("DETALLEORDEN", response.toString())
                        if (response.success!!){
                            //compartir=true
                            loadOrder(
                                response.purchaseOrder,
                                response.articles,
                                response.partaker,
                                response.purchaseOrderTicket,
                                response.location
                            )
                        }else{
                            showResponse("Accesso negado")
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

    @SuppressLint("NotifyDataSetChanged")
    private fun getCodeChair(chair:String){
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getDetailSilla(QRSanerApplication.preference.getKeyValue(), "399786")
            val response =call.body()
            runOnUiThread {
                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("getDetailSilla", response.toString())
                        if(response.success==true) {
                            getCodeValue("${response.data?.id_purchase_order}")
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

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun loadOrder(
        purchaseOrder: PurchaseOrder?,
        articles: ArrayList<Articles>?,
        partaker: Partaker?,
        purchaseOrderTicket: ArrayList<PurchaseOrderTicket>?,
        location: ArrayList<Location>?
    ) {
        binding.detailDni.setText("${partaker?.id}")
        binding.detailName.setText("${purchaseOrder?.name}")
        binding.detalLastName.setText("${purchaseOrder?.lastname}")
        binding.detailPhone.setText("${purchaseOrder?.number_phone}")
        binding.detailEmail.setText("${partaker?.email}")
        info = "CÉDULA N° ${partaker?.id} \nNOMBRE: ${purchaseOrder?.name} \nAPELLIDO: ${purchaseOrder?.lastname} \nTELÉFONO: ${purchaseOrder?.number_phone} \nEMAIL: ${partaker?.email} \nARTÍCULO\n"
        if (articles != null) {
            articles.forEach {
                info = "$info  ${it.name}  cantidad ${it.cantidad}\n"
            }
        }else{
            info = "$info No posee artículos \n"
        }
        info = "$info CODIGOS QR\n"
        var orden_n = ""
        if (purchaseOrderTicket != null) {
            purchaseOrderTicket.forEach {
                info = "$info  ${it.code}   ${it.status}\n"
                orden_n = it.id_purchase_order.toString()
            }
        }else{
            info = "$info No posee  códigos\n"
        }
        if (location != null) {
            location.forEach {
                info = "$info  ${it.name}\n"
            }
        }else{
            info = "$info No posee  puestos\n"
        }
        info= "ORDEN N° $orden_n \n $info"

        binding.detailCodeOrden.setText(orden_n)
        dogArticle.clear()
        if (articles != null) {
            dogArticle.addAll(articles)
        }
        adpater_article.notifyDataSetChanged()
        dogCodeQR.clear()
        if (purchaseOrderTicket != null) {
            dogCodeQR.addAll(purchaseOrderTicket)
        }
        adpater_code_qr.notifyDataSetChanged()
        dogLocation.clear()
        if (location != null) {
            dogLocation.addAll(location)
        }
        adpater_location.notifyDataSetChanged()

    }

    private fun showResponse(body: String?) {
        Toast.makeText(this, "$body", Toast.LENGTH_LONG).show()
    }
    private fun iniRecycleViewCodeQR() {
        adpater_code_qr= OrderDetailCodeQRAdapter(dogCodeQR)
        binding.listOrderDetailCodeQr.layoutManager= LinearLayoutManager(this)
        binding.listOrderDetailCodeQr.adapter = adpater_code_qr
    }


}