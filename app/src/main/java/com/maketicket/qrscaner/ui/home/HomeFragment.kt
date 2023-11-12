package com.maketicket.qrscaner.ui.home

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.*
import com.maketicket.qrscaner.databinding.FragmentHomeBinding
import com.maketicket.qrscaner.ui.model.MySQLiteHelper
import com.maketicket.qrscaner.ui.model.TicketByFunction
import com.maketicket.qrscaner.ui.response.funciones
import com.maketicket.qrscaner.ui.response.total
import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment(){

    private var _binding: FragmentHomeBinding? = null
    lateinit var n_entradas:TextView
    lateinit var n_evento:TextView
    lateinit var n_result:TextView

    lateinit var n_aforo:TextView
    lateinit var n_ordenes:TextView
    lateinit var n_bloqueo :TextView
    private lateinit var DBHelper: MySQLiteHelper


    lateinit var listFunction:RecyclerView
    private val binding get() = _binding!!
    private lateinit var adpater: FunctionListHomeAdapter
    private val dogFunction = mutableListOf<funciones>()

    private lateinit var mService: OrderEventService
    private var mBound:Boolean = false
    private lateinit var imgMap: ImageView

    private lateinit var mConnection:ServiceConnection

    // This property is only valid between onCreateView and
    // onDestroyView.
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        n_entradas           =  root.findViewById(R.id.n_entradas)
        n_evento             =  root.findViewById(R.id.n_check)
        n_result             =  root.findViewById(R.id.n_falt)
        n_aforo              =  root.findViewById(R.id.n_aforo)
        n_ordenes            =  root.findViewById(R.id.n_ordenes)
        n_bloqueo            =  root.findViewById(R.id.n_bloqueo)
        imgMap               =  root.findViewById(R.id.img_map)
        if(!QRSanerApplication.preference.getNameEvent().equals("")){
            _binding!!.textNameEvento.setText(QRSanerApplication.preference.getNameEvent())
        }else{
            _binding!!.textNameEvento.setText("NOMBRE DE EVENTO NO DISPONIBLE")
        }
        //_binding!!.nameFuntion.setText(QRSanerApplication.preference.getNameArticle())
        listFunction= root.findViewById(R.id.list_function_home)
        // textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }
        adpater= FunctionListHomeAdapter(dogFunction, activity)
        listFunction.layoutManager= LinearLayoutManager(activity)
        listFunction.adapter = adpater
        if(QRSanerApplication.preference.getIdArticle()>0) {
            listEvent()
        }/*else{
            _binding!!.nameFuntion.setText("No hay funciones disponibles")
        }*/
        //val intent = Intent( activity,  OrderEventService::class.java)
        //activity?.startService(intent)
        if(QRSanerApplication.preference.getIdEvent()>0) {
            iniOrderAdmittedApi()
            iniOrderTicketApi()
            //setValueCount()
        }
        //setValueCount()


        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context )
        if(QRSanerApplication.preference.getIdEvent()>0) {
            mConnection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    val binder = service as OrderEventService.OrderEventBinder
                    mService = binder.getService()
                    mBound = true
                }
                override fun onServiceDisconnected(name: ComponentName?) {
                    mBound = false
                }
            }
            val intent = Intent(activity, OrderEventService::class.java)
            activity?.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            setValueCount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setValueCount(){
        if(mBound){
            mService.caluleValue()
            Handler().apply {
                val runnable = object : Runnable{
                    override fun run() {
                        Log.d("SERVICIO_ORDEN", "setValueCount true")

                        n_entradas.setText(mService.count_register.toString())
                        n_evento.setText(mService.count_event.toString())
                        n_result.setText(mService.count_result.toString())
                        postDelayed(this, 1500)
                    }
                }
            }

        }else{
            Log.d("SERVICIO_ORDEN", "setValueCount false")
        }
    }

    private fun iniOrderAdmittedApi() {
        _binding?.loadingProgessbar?.visibility = View.VISIBLE
        if(QRSanerApplication.preference.getIdEvent() > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val call = RestEngine.getRestEngine().create(OrderEntryService::class.java)
                    .getOrderAdmitted(QRSanerApplication.preference.getIdEvent(), QRSanerApplication.preference.getKeyValue())
                val response = call.body()
                activity?.runOnUiThread {
                    _binding?.loadingProgessbar?.visibility = View.GONE
                    if (call.isSuccessful) {
                        if (response != null) {
                            if (response.success) {
                                iniTextTicket(response.total)
                            }
                        } else {
                            showResponse("Error de respuesta")
                        }
                        // showResponse(dogCode.size.toString())
                    } else {
                        showResponse("Error de conexión ")
                    }
                }
            }
        }
    }
    private fun iniOrderTicketApi() {
        _binding?.loadingProgessbar?.visibility = View.VISIBLE
        var funciones:String=""
        var i=0
        DBHelper= MySQLiteHelper(requireActivity())
        DBHelper.ListFunction().forEach {
            if(i==0) {
                funciones += "${it.id_function}"
            }else{
                funciones += ",${it.id_function}"
            }
            i++
        }
        Log.d("HOME FRAGMENT funciones  ", funciones)
        if(QRSanerApplication.preference.getIdEvent() > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val call = RestEngine.getRestEngine().create(OrderEntryService::class.java)
                    .getTicketByOrden(QRSanerApplication.preference.getKeyValue(), QRSanerApplication.preference.getIdEvent(), funciones)
                val response = call.body()
                activity?.runOnUiThread {
                    _binding?.loadingProgessbar?.visibility = View.GONE
                    if (call.isSuccessful) {
                        if (response != null) {
                                iniTextOrdenes(response)
                        } else {
                            Log.d("HOME FRAGMENT  response", response.toString())
                            showResponse("Error de respuesta")
                        }
                        // showResponse(dogCode.size.toString())
                    } else {
                        Log.d("HOME FRAGMENT  conexión", call.isSuccessful.toString())
                        showResponse("Error de conexión ")
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun iniTextTicket(total: ArrayList<total>?) {
        var count_register:Int = 0
        var count_evento:Int = 0
        var url:String=""
        total?.forEach {
            if(it.status.equals("Registrado")){
                count_register = it.count
            }else if(it.status.equals("Fue al evento")){
                count_evento = it.count
            }
            url = it.imagen!!
         }
        count_register= count_register+ count_evento
        n_entradas.setText("$count_register")
        n_evento.setText("$count_evento")
        val result  = count_register-count_evento
        n_result.setText("$result")

        Picasso.get()
            .load(url)
            .fit()
            .centerCrop()
            .into(imgMap)

    }
    @SuppressLint("SetTextI18n")
    private fun iniTextOrdenes(ticket_by_function: ArrayList<TicketByFunction>?) {
        if (ticket_by_function != null) {
            var count_register:Int = 0
            var count_evento:Int = 0

            ticket_by_function.forEach {
                if(it.status.equals("Registrado")){
                    count_register = it.count!!
                }else if(it.status.equals("Fue al evento")){
                    count_evento = it.count!!
                }
                count_register= count_register+ count_evento
                n_aforo.setText("$count_register")
                n_ordenes.setText("$count_evento")
                val result  = count_register-count_evento
                n_bloqueo.setText("$result")

            }
        }
    }

    private fun showResponse(body: String?) {
        Toast.makeText(context, "$body", Toast.LENGTH_LONG).show()
    }

    private fun listEvent() {
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getOrderAdmitted( QRSanerApplication.preference.getIdEvent(), QRSanerApplication.preference.getKeyValue())
            val response =call.body()
            activity?.runOnUiThread {
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success){
                            iniRecycleView(response.funciones)
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
    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.makeidsystems.com/makeid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun iniRecycleView(funciones: ArrayList<funciones>?) {
        dogFunction.clear()
        if (funciones != null) {
            dogFunction.addAll(funciones)
        }

        Log.d("TAG_F", "funciones $funciones")
        adpater.notifyDataSetChanged()


        /* dogFunction.clear()
         dogFunction.addAll(funciones!!)
         adpater= FunctionDetailAdapter(dogFunction)
         listFunction.layoutManager= LinearLayoutManager(activity)
         listFunction.adapter = adpater*/
    }



}


