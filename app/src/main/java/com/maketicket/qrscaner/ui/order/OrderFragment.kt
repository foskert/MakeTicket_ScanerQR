package com.maketicket.qrscaner.ui.order

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.FragmentOrderBinding
import com.maketicket.qrscaner.ui.menu.MyBottomSheetDialogFragment
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

class OrderFragment : Fragment(), FragmentResultListener{

    private var _binding: FragmentOrderBinding? = null
    private lateinit var adpater_code_qr: OrderDetailCodeQRAdapter
    private val dogCodeQR = mutableListOf<PurchaseOrderTicket>()
    private lateinit var adpater_article: OrderDetailArticleAdapter
    private lateinit var adpater_location: OrderDetailLocationAdapter
    private val dogArticle = mutableListOf<Articles>()
    private val dogLocation = mutableListOf<Location>()
    private var info: String = ""
    private lateinit var DBHelper:MySQLiteHelper



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setFragmentResultListener("scannedCode") { requestKey, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val result = bundle.getString("codeqrmodal")
            Log.d("FRAGMENT_RESUL_R 2 codeqrmodal", result.toString())
            // Do something with the result
        }*/


    }
    override fun onFragmentResult(requestCode: String, result: Bundle) {
        Log.d("FRAGMENT_RESUL_R 1", requestCode)
    }

/*    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*childFragmentManager.setFragmentResultListener(
            "scannedCode",
            viewLifecycleOwner
        ) { _, result: Bundle ->
            val text = result.getString("codeqrmodal", "codeqrmodal")
            Log.d("FRAGMENT_RESUL_R 3", text)
        }*/
       /* requireActivity().findViewById<Button>(R.id.btn_verification_code_qr).setOnClickListener {
            OrderCodeQRViewModal.newInstance(

            ).show(childFragmentManager, this::class.java.simpleName)
        }*/
    }*/
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        info = ""
        val orderViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        _binding = FragmentOrderBinding.inflate(inflater, container, false)

        val root: View = binding.root
        showContainer()

        orderViewModel.text.observe(viewLifecycleOwner) {
           // textView.text = it
        }

        //imagen_toolbar = root.findViewById(R.id.img_toolbar)
        if(!QRSanerApplication.preference.getCodeQR().equals("")) {
            Log.d("FRAGMENT_ORDEN", QRSanerApplication.preference.getCodeQR() )
            getCodeValue(QRSanerApplication.preference.getCodeQR())
        }
        binding!!.fab.setOnClickListener {
            CodeQR()
        }
        _binding!!.detailTextInfo.setText(info)
        iniRecycleViewCodeQR()
        iniRecycleViewArticle()
        _binding!!.imgToolbarQrShareFragment.setOnClickListener {
            Log.d("FRAGMENT_ORDEN", info )
            compartir()
        }
        _binding!!.imgToolbarQrFragment.setOnClickListener {
            //var dialog= OrderCodeQRViewModal()
            //dialog.show(activity?.supportFragmentManager!!, "Function")
            showdialog()
            //CodeQR()
        }
        _binding!!.checkAll.setOnClickListener {
            checkAllList(_binding!!.checkAll.isChecked)
        }
        _binding!!.sendCode.setOnClickListener{
            AlertDialog.Builder(context).apply {
                setTitle(R.string.alert)
                setMessage("¿Está seguro de realizar la operación?, se enviarán los códigos seleccionados para su verificación esta operación no se puede revertir")
                setNegativeButton("No", null)
                setPositiveButton("Si") { _: DialogInterface, _: Int ->
                    sendCodes()
                }
            }.show()
        }

        _binding!!.btnMenuHomeOrden.setOnClickListener {
            MyBottomSheetDialogFragment().show(activity?.supportFragmentManager!!, "menu")
        }

        _binding!!.checkAll.visibility = View.GONE
        _binding!!.sendCode.visibility = View.GONE
        return root
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
        CoroutineScope(Dispatchers.IO).launch{
            val id_article:String
            if(QRSanerApplication.preference.getIdArticle()>0){
                id_article = QRSanerApplication.preference.getIdArticle().toString()
            }else{
                id_article = ""
            }
            val call = getRestEngine().create(OrderEntryService::class.java).getOrderEntry(
                code,
                QRSanerApplication.preference.getKeyValue(),
                QRSanerApplication.preference.getIdEvent(),
                funciones
            )
            val response =call.body()
            activity?.runOnUiThread {
                _binding?.loadingProgessbar?.visibility = View.GONE
                if(call.isSuccessful) {
                    Log.d("FRAGMENT_ORDEN_isSuccessful", response.toString())
                    if (response != null) {
                        Log.d("FRAGMENT_ORDEN_RESPONSE", response.toString())
                        Log.d("FRAGMENT_ORDEN_RESPONSE", i.toString())
                      //  Log.d("FRAGMENT_ORDEN_RESPONSE_dogCodeQR", dogCodeQR[i].toString())
                        if(response.success==true) {
                            addTicketDB(code, response.status!!)
                            dogCodeQR[i].status = "Fue al evento"
                            adpater_code_qr.UpdateData(dogCodeQR)
                            adpater_code_qr.notifyDataSetChanged()
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

        DBHelper= MySQLiteHelper(requireContext())
        DBHelper.addTicket(code, status, QRSanerApplication.preference.getKeyValue(), currentDate)
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun checkAllList(value: Boolean) {
        dogCodeQR.forEach {
            it.check = value
            Log.d("FRAGMENT_VALUE value", value.toString())
        }
        adpater_code_qr.UpdateData(dogCodeQR)
        adpater_code_qr.notifyDataSetChanged()
    }

    @SuppressLint("ResourceType")
    private fun showdialog() {
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Verificación de Código QR")
        builder.setMessage("Puedes serleccionar el ingreso manual del Código QR para el detalle, o seleccionar el escaneo de QR.")

        val input = EditText(context)
        input.setHint("Código QR ")
        input.setBackgroundResource(R.drawable.rect_sald_input)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(100, 10, 10, 10)
        input.layoutParams = params
        input.inputType = InputType.TYPE_CLASS_NUMBER



        builder.setView(input)

        builder.setPositiveButton("VERIFICAR", DialogInterface.OnClickListener { dialog, which ->
            if(!input.text.toString().equals("")){
                Log.d("FRAGMENT_MODAL code", input.text.toString())
                getCodeValue(input.text.toString())
            }else{
                showResponse("Debe ingresar un código")
            }
        })

        builder.setNegativeButton("ESCANEO QR", DialogInterface.OnClickListener { dialog, which ->
            CodeQR()
            dialog.cancel()
        })

        builder.show()
    }

    private fun showContainer() {
        _binding!!.fragmentOrderContainer.isVisible= true
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
                Log.d("FRAGMENT_RESUL_RESUL", result.contents.toString() )
                QRSanerApplication.preference.setCodeQR(result.contents.toString())
                getCodeValue(result.contents.toString())

            }else{
                Toast.makeText(context, "Escaneo cancelado en orden", Toast.LENGTH_LONG).show()
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun iniRecycleViewArticle() {
        adpater_article= OrderDetailArticleAdapter(dogArticle)
        _binding!!.listOrderDetailArticle.layoutManager= LinearLayoutManager(context)
        _binding!!.listOrderDetailArticle.adapter = adpater_article
        adpater_location= OrderDetailLocationAdapter(dogLocation)
        _binding!!.listLocation.layoutManager= LinearLayoutManager(context)
        _binding!!.listLocation.adapter = adpater_location
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showResponse(body: String?) {
        Toast.makeText(context, "$body", Toast.LENGTH_LONG).show()
    }
    private fun iniRecycleViewCodeQR() {
        adpater_code_qr= OrderDetailCodeQRAdapter(dogCodeQR)
        _binding!!.listOrderDetailCodeQr.layoutManager= LinearLayoutManager(context)
        _binding!!.listOrderDetailCodeQr.adapter = adpater_code_qr
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
     fun loadOrder(
        purchaseOrder: PurchaseOrder?,
        articles: ArrayList<Articles>?,
        partaker: Partaker?,
        purchaseOrderTicket: ArrayList<PurchaseOrderTicket>?,
        location: ArrayList<Location>?

    ) {
        _binding!!.checkAll.visibility = View.VISIBLE
        _binding!!.sendCode.visibility = View.VISIBLE
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
        if(purchaseOrderTicket != null) {
            dogCodeQR.addAll(purchaseOrderTicket!!)
        }

        adpater_code_qr.notifyDataSetChanged()
        dogLocation.clear()
        if (location != null) {
            dogLocation.addAll(location)
        }
        adpater_location.notifyDataSetChanged()
    }
     fun compartir() {
        val intent = Intent().apply{
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, info)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, "DETALLE DE ORDEN")
        startActivity(shareIntent)
    }
    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.makeidsystems.com/makeid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getCodeValue(code:String){
        _binding?.loadingProgessbar?.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{
           val call = getRestEngine().create(OrderEntryService:: class.java).getOrderDetail(
                code,
                QRSanerApplication.preference.getKeyValue(),
                //QRSanerApplication.preference.getIdEvent(),
               // QRSanerApplication.preference.getIdArticle().toString()
            )
            val response =call.body()
            Log.d("FRAGMENT_RESUL", response.toString())
            activity?.runOnUiThread {
                _binding?.loadingProgessbar?.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success!!){
                            QRSanerApplication.preference.setCodeQR(code)
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
}