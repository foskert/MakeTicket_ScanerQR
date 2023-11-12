package com.maketicket.qrscaner

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.maketicket.qrscaner.ui.home.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderEventService : Service() {

    var count_register:Int = 0
    var count_event:Int = 0
    var count_result:Int = 0
    val handrle:Handler= Handler()
    private val binder = OrderEventBinder()

    /*
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("SERVICIO", "onStartCommand")

        handrle.apply {
            val runnable = object : Runnable{
                override fun run() {
                    Log.d("SERVICIO_ORDEN", "onStartCommand")
                    iniOrderAdmittedApi()
                    postDelayed(this, 9000)
                }
            }
            postDelayed(runnable, 9000)
        }
        return START_STICKY

    }
*/
   /*
   override fun onDestroy() {
        handrle.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
    */
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class OrderEventBinder:Binder(){
        fun getService():OrderEventService {
            return this@OrderEventService
        }
    }
    fun caluleValue(){
        //handrle.apply {
          //  val runnable = object : Runnable{
            //    override fun run() {
                    Log.d("SERVICIO_ORDEN", "onStartCommand")
                    iniOrderAdmittedApi()
           //         postDelayed(this, 100000)
           //     }
           // }
           // postDelayed(runnable, 100000)
       // }
    }
    private fun iniOrderAdmittedApi() {
        if(QRSanerApplication.preference.getIdEvent() > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val call = RestEngine.getRestEngine().create(OrderEntryService::class.java)
                    .getOrderAdmitted(QRSanerApplication.preference.getIdEvent(), QRSanerApplication.preference.getKeyValue())
                val response = call.body()
                // runOnUiThread {
                if (call.isSuccessful) {
                    if (response != null) {
                        if (response.success) {
                            //iniTextTicket(response.total)
                            response.total?.forEach {
                                if(it.status.equals("Registrado")){
                                    count_register = it.count
                                }else if(it.status.equals("Fue al evento")){
                                    count_event = it.count
                                }
                            }
                            count_register= count_register + count_event
                            count_result  = count_register
                            Log.d("SERVICIO_COUNT_RESULT", count_result.toString())
                        }
                    } else {
                        onDestroy()
                        // showResponse("Error de respuesta")
                    }
                    // showResponse(dogCode.size.toString())
                } else {
                    onDestroy()
                    //showResponse("Error de conexión ")
                }
                // }
            }
        }
    }

}