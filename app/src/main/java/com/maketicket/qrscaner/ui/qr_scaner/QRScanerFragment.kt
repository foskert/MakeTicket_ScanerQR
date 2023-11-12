package com.maketicket.qrscaner.ui.qr_scaner

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import com.maketicket.qrscaner.OrderDetailActivity
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.FragmentQrScanerBinding
import com.maketicket.qrscaner.ui.menu.MyBottomSheetDialogFragment
import com.maketicket.qrscaner.ui.model.MySQLiteHelper
import com.maketicket.qrscaner.ui.model.Partaker
import com.maketicket.qrscaner.ui.model.Ticket
import com.maketicket.qrscaner.ui.partaker.PartakerAdapter
import com.maketicket.qrscaner.ui.user.TypeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class QRScanerFragment : Fragment() {

    private var _binding: FragmentQrScanerBinding? = null
    private val dogCode = mutableListOf<Ticket>()
    private lateinit var DBHelper:MySQLiteHelper
    private lateinit var loading_qr_code: ProgressBar
    private val dogPartaker = mutableListOf<Partaker>()
    private lateinit var adpater_audiencia: QRScanerAdapter
    private lateinit var adpater_partake: PartakerAdapter

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val QRScanerViewModel =
            ViewModelProvider(this).get(QRScanerViewModel::class.java)
        _binding = FragmentQrScanerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        loading_qr_code = root.findViewById(R.id.loading_qr_code)
        loading_qr_code.visibility =View.GONE
        QRScanerViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }
        setFragmentResultListener("requestKey") { requestKey, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val result = bundle.getString("bundleKey")
            //_binding!!.accept.setText(result)
            // Do something with the result
        }
        _binding!!.imgToolbarQrFragment.setOnClickListener {
            CodeQR()
        }
        iniRecycleView()
        _binding!!.btnMenuHomeQrFragment.setOnClickListener {
            MyBottomSheetDialogFragment().show(activity?.supportFragmentManager!!, "menu")
        }
        _binding!!.imgToolbarQrShareFragment.setOnClickListener {
            shareCodeQR()
        }
        return root
    }

    private fun iniRecycleView() {
        DBHelper = MySQLiteHelper(requireContext())
        dogCode.clear()
        dogCode.addAll(DBHelper.ListTicket())

        if(QRSanerApplication.preference.getTypeEvent().equals("orden")) {
            adpater_audiencia = QRScanerAdapter(dogCode)
            _binding!!.listQrScaner.layoutManager= LinearLayoutManager(context)
            binding.listQrScaner.adapter = adpater_audiencia
        }else{
            dogPartaker.clear()
            dogPartaker.addAll(DBHelper.ListPartaker())
            adpater_partake = PartakerAdapter( dogPartaker)
            binding.listQrScaner.layoutManager= LinearLayoutManager(context)
            binding.listQrScaner.adapter = adpater_partake
        }
    }
    private fun shareCodeQR() {

        var listCode:String=""
        dogCode.forEach {
            listCode = "$listCode ${it.code}\n "
        }
        val intent = Intent().apply{
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, listCode)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, "LISTA DE CÓDIGOS QR")
        startActivity(shareIntent)
    }
    private fun CodeQR() {
        IntentIntegrator.forSupportFragment(this).addExtra("CODE_F", 5)
            .setPrompt("ESCANEO DE CÓDIGO QR")
            .setTorchEnabled(false)
            .setBeepEnabled(true)
            .initiateScan()

    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        Log.d("FRAGMENT_RESUL_R requestCode", requestCode.toString())
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null){
            if(result.contents !== null){
                if(QRSanerApplication.preference.getTypeEvent().equals("orden")){
                    loading_qr_code.visibility =View.VISIBLE
                    getCodeValue(result.contents.toString())
                }else{
                    Log.d("DASHBOARDPARTAKER code", result.contents.toString())
                    getCodeValuePartaker(result.contents.toString())
                }

            }else{
                showResponse("Escaneo cancelado")
                loading_qr_code.visibility =View.GONE
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
            loading_qr_code.visibility =View.GONE
        }
    }
    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.makeidsystems.com/makeid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getCodeValue(code:String){
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
            activity?.runOnUiThread {
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success){
                            //dogCode.indexOf(code)
                            val ticket = addTicketDB(code, response.status)
                            dogCode.reverse()
                            dogCode.add(ticket)
                            dogCode.reverse()
                            adpater_audiencia.notifyDataSetChanged()
                            loading_qr_code.visibility =View.GONE
                            alertShowQRScaner("El código fue encontrado $code ")
                        }else{
                            if(response.status.equals("Ticket Ya Ingreso")){
                                alertShowQRScaner("Ticket Ya Ingreso $code ")
                                //detalleOrder(code, response.status.uppercase())
                                loading_qr_code.visibility =View.GONE
                            }else{
                                alertShowQRScaner(response.status)
                                //alertShow(response.status)
                                loading_qr_code.visibility =View.GONE
                                // if(!response.status.equals("Pertenece Otro Evento")) {
                                //     showResponse(" ${response.status}")
                                // }else {
                                //     alertShow(response.status)
                                //} 04149774618
                            }
                        }
                    }else{
                        showResponse("Error de respuesta")
                        loading_qr_code.visibility =View.GONE
                    }
                    // showResponse(dogCode.size.toString())
                }else{
                    showResponse("Error de conexión ")
                    loading_qr_code.visibility =View.GONE
                }
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun addTicketDB(code:String, status:String ): Ticket {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date()).toString()

        DBHelper= MySQLiteHelper(requireContext())
        DBHelper.addTicket(code, status, QRSanerApplication.preference.getKeyValue(), currentDate)
        val ticket =Ticket(code, currentDate)
        return ticket
    }
    private fun alertShow( message:String) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.alert)
            setMessage(message)
            setNegativeButton("Continuar", null)
        }.show()

    }
    private fun alertShowQRScaner( message:String) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.alert)
            setMessage(message)
            setNegativeButton("Cancelar", null)
            setPositiveButton("Scaner QR") { _: DialogInterface, _: Int ->
                CodeQR()
            }
        }.show()

    }
    private fun showResponse(body: String?) {
        Toast.makeText(context, "$body", Toast.LENGTH_LONG).show()
    }
    private fun detalleOrder(code: String, value:String) {
        val intent = Intent(activity, OrderDetailActivity::class.java)
        intent.putExtra("CODE", code)
        intent.putExtra("SEND", true)
        intent.putExtra("ORDEN", value)
        startActivity(intent)
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getCodeValuePartaker(code:String){
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getEventPartaker(code, QRSanerApplication.preference.getKeyValue(), QRSanerApplication.preference.getIdEvent())
            val response =call.body()
            activity?.runOnUiThread {
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success){
                            //dogCode.indexOf(code)
                            Log.d("DASHBOARDPARTAKER", call.body().toString())
                            alertShowQRScaner("Ticket ingresado correctamente $code")
                            addPartakerDB(code, response.partaker!!)
                            response.partaker.code = code
                            dogCode.reverse()
                            dogPartaker.add(response.partaker!!)
                            dogPartaker.reverse()

                            adpater_partake.notifyDataSetChanged()
                            loading_qr_code.visibility =View.GONE
                        }else{
                            if(response.status.equals("Ticket Ya Ingreso") ){
                                detalleOrder(code, response.status.uppercase())
                                alertShowQRScaner("Ticket Ya Ingreso $code")
                                loading_qr_code.visibility =View.GONE
                            }else{
                                if(response.partaker != null){
                                    alertShowQRScaner("${response.status} \n${response.partaker!!.name} ${response.partaker!!.lastname} \n${response.partaker!!.email} \n${response.partaker!!.company} \n${response.partaker!!.category}")
                                    //alertShow("${response.status} \n${response.partaker!!.name} ${response.partaker!!.lastname} \n${response.partaker!!.email} \n${response.partaker!!.company} \n${response.partaker!!.category}")
                                    loading_qr_code.visibility =View.GONE
                                }else{
                                    alertShowQRScaner(response.status)
                                   // alertShow(response.status)
                                    loading_qr_code.visibility =View.GONE
                                }
                                //alertShow(response.status)// \n${response.partaker!!.name} ${response.partaker!!.lastname} \n ${response.partaker!!.email}")
                                // if(!response.status.equals("Pertenece Otro Evento")) {
                                //     showResponse(" ${response.status}")
                                // }else {
                                //     alertShow(response.status)
                                //}
                            }
                        }
                    }else{
                        showResponse("Error de respuesta")
                        loading_qr_code.visibility =View.GONE
                    }
                    // showResponse(dogCode.size.toString())
                }else{
                    showResponse("Error de conexión ")
                    loading_qr_code.visibility =View.GONE
                }
            }
        }
    }
    private fun addPartakerDB(code: String, partaker: Partaker) {


        DBHelper= MySQLiteHelper(requireContext())
        if(partaker.name.isNullOrEmpty()){
            partaker.name=""
        }
        if(partaker.lastname.isNullOrEmpty()){
            partaker.lastname=""
        }
        if(partaker.phone.isNullOrEmpty()){
            partaker.phone="No disponible"
        }
        if(partaker.sex.isNullOrEmpty()){
            partaker.sex="No disponible"
        }
        if(partaker.city.isNullOrEmpty()){
            partaker.city="No disponible"
        }
        if(partaker.id.isNullOrEmpty()){
            partaker.id="No disponible"
        }
        if(partaker.email.isNullOrEmpty()){
            partaker.email="No disponible"
        }
        if(partaker.id_system_user.toString().isNullOrEmpty()){
            partaker.id_system_user=0
        }
        if(partaker.category.isNullOrEmpty()){
            partaker.category="No disponible"
        }
        DBHelper.addPartaker(
            code,
            partaker.id!!,
            partaker.name!!,
            partaker.lastname,
            partaker.email!!,
            partaker.phone!!,
            "",
            partaker.id_system_user.toString(),
            partaker.sex,
            partaker.status,
            partaker.city,
            partaker.company,
            partaker.category!!
        )
    }


    /* override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
         val layoutManager = LinearLayoutManager(context)
         recyclerView = view.findViewById(R.id.list_qr_scaner)
         recyclerView.setHasFixedSize(true)
         adapter = QRScanerAdapter(code)
         recyclerView.adapter=adapter
     }
     private fun iniRecyclerView(code:List<String>){
         adapter = QRScanerAdapter(qrCode)
         binding.listQrScaner.layoutManager = LinearLayoutManager(context)
         binding.listQrScaner.adapter = adapter

     }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}