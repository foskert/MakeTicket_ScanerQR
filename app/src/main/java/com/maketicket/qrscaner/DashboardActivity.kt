package com.maketicket.qrscaner


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.AlarmClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.maketicket.qrscaner.databinding.ActivityDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.maketicket.qrscaner.QRSanerApplication.Companion.preference
import com.maketicket.qrscaner.ui.menu.MyBottomSheetDialogFragment
import com.maketicket.qrscaner.ui.model.MySQLiteHelper
import com.maketicket.qrscaner.ui.model.Partaker
import com.maketicket.qrscaner.ui.model.Ticket
import com.maketicket.qrscaner.ui.partaker.PartakerAdapter
import com.maketicket.qrscaner.ui.qr_scaner.QRScanerAdapter
import com.maketicket.qrscaner.ui.response.total
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var loading_qr_code: ProgressBar
    private lateinit var btn_toolbar_qr: ImageView
    private lateinit var navController: Fragment
    private lateinit var adpater_audiencia: QRScanerAdapter
    private lateinit var adpater_partake: PartakerAdapter
    private val dogPartaker = mutableListOf<Partaker>()
    private val dogCode = mutableListOf<Ticket>()
    private val dogtotal = mutableListOf<total>()
    private lateinit var DBHelper:MySQLiteHelper
    private lateinit var mFragmentManager:FragmentManager
    lateinit var code_f:String


   // private lateinit var appBarConfiguration2: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        code_f= "hola mundo"

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btn_toolbar_qr = findViewById(R.id.img_toolbar_qr)
        loading_qr_code = findViewById(R.id.loading_qr_code)
        btn_toolbar_qr.isEnabled = true
        loading_qr_code.visibility =View.GONE
        btn_toolbar_qr.setOnClickListener {
            if(!preference.getKeyValue().equals("")  && !preference.getIdEvent().equals("")) {
                btn_toolbar_qr.isEnabled = false
                setQRCode()
            }else{
                btn_toolbar_qr.isEnabled = true
                AlertDialog.Builder(this).apply {
                    setTitle(R.string.alert)
                    setMessage(R.string.credenciales)
                    setNegativeButton("Continuar", null)
                }.show()
            }
        }
       // showResponse(preference.getTypeEvent())
        if(preference.getTypeEvent()=="orden"){
            binding!!.imgToolbarPdf.visibility = View.GONE
        }else{
            //showResponse(preference.getTypeEvent())
            binding!!.imgToolbarPdf.visibility = View.VISIBLE
            binding!!.imgToolbarPdf.setOnClickListener{
                excelIngresoOrder()
            }
        }

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_menu_qr,
                R.id.navigation_home,
                R.id.navigation_qr_scaner,
                R.id.navigation_order_ticket_qr_scaner,
                R.id.navigation_user
            )
        )

        //val bottomSheet = findViewById<LinearLayout>



        navView.setupWithNavController(navController)
        /*navView.get(R.id.navigation_menu).setOnClickListener{
            MyBottomSheetDialogFragment().apply {
                show(supportFragmentManager, "MENU")
            }
        }*/
         binding.btnMenuHomeOrdenQr.setOnClickListener {
            MyBottomSheetDialogFragment().apply {
                show(supportFragmentManager, "MENU")
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->

            if(preference.getTypeEvent()=="orden"){
                binding!!.imgToolbarPdf.visibility = View.GONE
            }else{
                //showResponse(preference.getTypeEvent())
                binding!!.imgToolbarPdf.visibility = View.VISIBLE
                binding!!.imgToolbarPdf.setOnClickListener{
                    excelIngresoOrder()
                }
            }
            if(destination.id == R.id.navigation_qr_scaner) {
                    binding.toolbarOrder.visibility = View.VISIBLE
                    binding.textToolbar.setText(R.string.qr_scaner)
                    btn_toolbar_qr.setImageResource(R.drawable.ic_baseline_qr_code_24)
                    btn_toolbar_qr.visibility = View.VISIBLE
                    binding.imgToolbarKey.visibility = View.GONE
                    binding.imgToolbarSingOut.visibility = View.GONE
                    binding.recyclerQrScaner.visibility= View.VISIBLE
                    binding.imgToolbarQrShare.visibility = View.VISIBLE

                    binding.tollTypeEvent.setText(preference.getTypeEvent())
                    //iniRecycleView()
                    binding.imgToolbarQrShare.setOnClickListener {
                        shareCodeQR()
                    }
                } else if(destination.id == R.id.navigation_home) {
                    binding.textToolbar.setText("Maketicket")
                    binding.toolbarOrder.visibility = View.VISIBLE
                    binding.imgToolbarKey.setImageResource(R.drawable.ic_baseline_vpn_key_24)
                    binding.imgToolbarKey.visibility = View.GONE
                    binding.imgToolbarQr.visibility = View.GONE
                    binding.imgToolbarSingOut.visibility = View.GONE
                    binding.recyclerQrScaner.visibility= View.GONE
                    binding.imgToolbarQrShare.visibility = View.GONE
                    binding.tollTypeEvent.setText("")
                    binding.imgToolbarKey.setOnClickListener {
                        validateKey()
                    }
                    //iniOrderAdmittedApi()
                } else if(destination.id == R.id.navigation_order_ticket_qr_scaner) {
                    binding.toolbarOrder.visibility = View.GONE
                    binding.textToolbar.setText(R.string.title_order)
                    binding.imgToolbarQr.visibility = View.VISIBLE
                    binding.imgToolbarKey.visibility = View.GONE
                    binding.imgToolbarSingOut.visibility = View.GONE
                    binding.recyclerQrScaner.visibility= View.GONE
                    binding.imgToolbarQrShare.visibility = View.VISIBLE
                    binding.tollTypeEvent.setText("")
                    binding.imgToolbarQrShare.setOnClickListener {
                        shareCodeQR()

                    }

                } else if(destination.id == R.id.navigation_user) {
                    binding!!.imgToolbarPdf.visibility = View.GONE
                    binding.toolbarOrder.visibility = View.VISIBLE
                    binding.imgToolbarSingOut.setImageResource(R.drawable.ic_outline_sing_out_white)
                    binding.textToolbar.setText(R.string.user)
                    binding.imgToolbarQr.visibility = View.GONE
                    binding.imgToolbarKey.visibility = View.GONE
                    binding.recyclerQrScaner.visibility= View.GONE
                    binding.imgToolbarSingOut.visibility = View.VISIBLE
                    binding.imgToolbarQrShare.visibility = View.GONE
                    binding.tollTypeEvent.setText("")
                    binding.imgToolbarSingOut.setOnClickListener {
                        alertSingOut()
                    }

                }
        }
    }


    private fun iniOrderAdmittedApi() {
        if(preference.getIdEvent() > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val call = getRestEngine().create(OrderEntryService::class.java)
                    .getOrderAdmitted(preference.getIdEvent(), preference.getKeyValue())
                val response = call.body()
                runOnUiThread {
                    if (call.isSuccessful) {
                        if (response != null) {
                            if (response.success!!) {
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

    private fun iniTextTicket(total: ArrayList<total>?) {

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

    private fun singOut() {
        QRSanerApplication.preference.sesionOut()
        onBackPressed()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun alertSingOut() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.alert)
            setMessage(R.string.sing_out)
            setPositiveButton("Si") { _: DialogInterface, _: Int ->
                singOut()
            }
            setNegativeButton("No", null)
        }.show()

    }
    private fun alertShow( message:String) {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.alert)
            setMessage(message)
            setNegativeButton("Continuar", null)
        }.show()

    }

    private fun validateKey() {
        val intent = Intent(this, KeyActivity::class.java).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "145236521452")
        }
        startActivity(intent)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(preference.getTypeEvent()=="orden"){
            binding!!.imgToolbarPdf.visibility = View.GONE
        }else{
            //showResponse(preference.getTypeEvent())
            binding!!.imgToolbarPdf.visibility = View.VISIBLE
            binding!!.imgToolbarPdf.setOnClickListener{
                excelIngresoOrder()
            }
        }
        loading_qr_code.visibility =View.VISIBLE
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null){
            if(result.contents !== null){
                if(preference.getTypeEvent().equals("orden")){
                    getCodeValue(result.contents.toString())
                }else{
                    Log.d("DASHBOARDPARTAKER code", result.contents.toString())
                    getCodeValuePartaker(result.contents.toString())
                }

            }else{
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
                loading_qr_code.visibility =View.GONE
                btn_toolbar_qr.isEnabled = true
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
            loading_qr_code.visibility =View.GONE
            btn_toolbar_qr.isEnabled = true
        }
    }
    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.makeidsystems.com/makeid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getCodeValuePartaker(code:String){
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getEventPartaker(code, preference.getKeyValue(), preference.getIdEvent())
            val response =call.body()
            runOnUiThread {
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success!!){
                            //dogCode.indexOf(code)
                            Log.d("DASHBOARDPARTAKER", call.body().toString())
                            addPartakerDB(code, response.partaker!!)
                            response.partaker.code = code
                            dogPartaker.add(response.partaker!!)
                            dogPartaker.reverse()

                            adpater_partake.notifyDataSetChanged()
                            loading_qr_code.visibility =View.GONE
                            btn_toolbar_qr.isEnabled = true
                        }else{
                            if(response.status.equals("Ticket Ya Ingreso") ){
                                detalleOrder(code, response.status!!.uppercase())
                                loading_qr_code.visibility =View.GONE
                                btn_toolbar_qr.isEnabled = true
                            }else{
                                if(response.partaker != null){
                                    alertShow("${response.status} \n${response.partaker!!.name} ${response.partaker!!.lastname} \n${response.partaker!!.email} \n${response.partaker!!.company} \n${response.partaker!!.category}")
                                    loading_qr_code.visibility =View.GONE
                                    btn_toolbar_qr.isEnabled = true
                                }else{
                                    alertShow(response.status!!)
                                    loading_qr_code.visibility =View.GONE
                                    btn_toolbar_qr.isEnabled = true
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
                        btn_toolbar_qr.isEnabled = true
                    }
                    // showResponse(dogCode.size.toString())
                }else{
                    showResponse("Error de conexión ")
                    loading_qr_code.visibility =View.GONE
                    btn_toolbar_qr.isEnabled = true
                }
            }
        }
    }

    private fun addPartakerDB(code: String, partaker: Partaker) {


        DBHelper= MySQLiteHelper(this)
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

    @SuppressLint("NotifyDataSetChanged")
     private fun getCodeValue(code:String){
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
            if(preference.getIdArticle()>0){
                id_article = preference.getIdArticle().toString()
            }else{
                id_article = ""
            }


            val call = getRestEngine().create(OrderEntryService:: class.java).getOrderEntry(
                code,
                preference.getKeyValue(),
                preference.getIdEvent(),
                funciones
            )
            val response =call.body()
            runOnUiThread {
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success == true){
                            //dogCode.indexOf(code)
                            val ticket = addTicketDB(code, response.status!!)
                            dogCode.add(ticket)

                            dogCode.reverse()
                            adpater_audiencia.notifyDataSetChanged()
                            loading_qr_code.visibility =View.GONE
                            btn_toolbar_qr.isEnabled = true
                        }else{
                            if(response.status.equals("Ticket Ya Ingreso")){
                                detalleOrder(code, response.status!!.uppercase())
                                loading_qr_code.visibility =View.GONE
                                btn_toolbar_qr.isEnabled = true
                            }else{
                                alertShow(response.status!!)
                                loading_qr_code.visibility =View.GONE
                                btn_toolbar_qr.isEnabled = true
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
                        btn_toolbar_qr.isEnabled = true
                    }
                   // showResponse(dogCode.size.toString())
                }else{
                    showResponse("Error de conexión ")
                    loading_qr_code.visibility =View.GONE
                    btn_toolbar_qr.isEnabled = true
                }
            }
        }
    }

    private fun detalleOrder(code: String, value:String) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.putExtra("CODE", code)
        intent.putExtra("ORDEN", value)
        startActivity(intent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun addTicketDB(code:String, status:String ): Ticket {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date()).toString()

        DBHelper= MySQLiteHelper(this)
        DBHelper.addTicket(code, status, preference.getKeyValue(), currentDate)
        val ticket =Ticket(code, currentDate)
        return ticket
    }

    private fun iniRecycleView() {
        DBHelper = MySQLiteHelper(this)
        dogCode.clear()
        dogCode.addAll(DBHelper.ListTicket())

        if(preference.getTypeEvent().equals("orden")) {
            adpater_audiencia = QRScanerAdapter(dogCode)
            binding.recyclerQrScaner.layoutManager= LinearLayoutManager(this)
            binding.recyclerQrScaner.adapter = adpater_audiencia
        }else{
            dogPartaker.clear()
            dogPartaker.addAll(DBHelper.ListPartaker())
            adpater_partake = PartakerAdapter( dogPartaker)
            binding.recyclerQrScaner.layoutManager= LinearLayoutManager(this)
            binding.recyclerQrScaner.adapter = adpater_partake
        }
    }

    private fun showResponse(body: String?) {
        Toast.makeText(this, "$body", Toast.LENGTH_LONG).show()
    }
    private fun setQRCode() {
        IntentIntegrator(this)
            .setPrompt("ESCANEO DE CÓDIGO QR")
            .setTorchEnabled(false)
            .setBeepEnabled(true)
            .initiateScan()
    }
    fun mostrarXls(name: String, absolutePath: String) {
        // Obtener el directorio donde se guardó el archivo
        val dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        // Crear un archivo con el nombre del archivo xls
        val file = File(dir, name)
        // Crear un Intent para abrir el archivo con una aplicación externa
        val intent = Intent(Intent.ACTION_VIEW)
        // Indicar la ruta y el tipo de archivo
        intent.setDataAndType(Uri.parse(absolutePath), "application/vnd.ms-excel")
        // Iniciar la actividad
        startActivity(intent)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun excelIngresoOrder() {
        if(preference.getKeyValue().equals("")){
            showResponse("No posee autorización")
        }
        if(preference.getIdEvent() == 0){
            showResponse("Se requiere el ID del evento")
        }
        if(!preference.getKeyValue().equals("") && preference.getIdEvent() != 0) {
            loading_qr_code.visibility =View.VISIBLE
            val call = getRestEngine().create(OrderEntryService::class.java).downloadFileWithUrl(
                preference.getKeyValue(), preference.getIdEvent()
            )
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    loading_qr_code.visibility =View.GONE
                    if (response.isSuccessful) {
                        val name = "ORDENENTRADA_${obtenerFechaHora()}.xls"
                        val dir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val file = File(dir, name)
                        val inputStream = response.body()?.byteStream()
                        if (inputStream != null) {
                            val outputStream = FileOutputStream(file)
                            val bufferedInputStream = BufferedInputStream(inputStream)
                            val bufferedOutputStream = BufferedOutputStream(outputStream)
                            val buffer = ByteArray(5024)
                            var bytesRead: Int
                            while (bufferedInputStream.read(buffer).also { bytesRead = it } != -1) {
                                bufferedOutputStream.write(buffer, 0, bytesRead)
                            }
                            bufferedOutputStream.flush()
                            bufferedOutputStream.close()
                            bufferedInputStream.close()
                            MediaScannerConnection.scanFile(
                                this@DashboardActivity,
                                arrayOf(file.path),
                                null,
                                null
                            )
                            if (dir != null) {
                                Toast.makeText(
                                    this@DashboardActivity,
                                    "Archivo guardado ${file.path}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(
                                Uri.parse(file.absolutePath),
                                "application/vnd.ms-excel"
                            )
                            startActivity(intent)

                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@DashboardActivity,
                                    "Error al obtener el archivo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        // Mostrar un mensaje con el código de error
                        Toast.makeText(
                            this@DashboardActivity,
                            "Error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Mostrar un mensaje con la excepción
                    Toast.makeText(
                        this@DashboardActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }else{

        }

    }
    fun InputStream.saveToMusicFolder(context: Context, fileName: String) {
        val resolver = context.contentResolver
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            ).toUri()
        }
        resolver.openOutputStream(uri!!)!!.use { copyTo(it) }
        close()
    }
    fun obtenerFechaHora(): String {
        // Obtener la fecha y hora actual
        val fechaHora = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        // Crear un formateador con el patrón deseado
        val formateador = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        // Formatear la fecha y hora según el formateador
        val resultado = fechaHora.format(formateador)
        // Devolver el resultado
        return resultado
    }


}