package com.maketicket.qrscaner.ui.mesa.table

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
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ActivityMesaTableBinding
import com.maketicket.qrscaner.databinding.ActivityMesaZonaBinding
import com.maketicket.qrscaner.ui.mesa.zona.MesaZonaAdapter
import com.maketicket.qrscaner.ui.model.MesaTabla
import com.maketicket.qrscaner.ui.model.MesaZona
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MesaTableActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMesaTableBinding
    lateinit var text_name_article: TextView
    private lateinit var adpater_mesa_table: MesaTableAdapter
    private val dogMesaTarble = mutableListOf<MesaTabla>()
    private var  id_zone:Int = 0
    private var  name_zone:String = ""
    private var  count:Int = 0
    private var  image_zone:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesaTableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReturnDetailSilla.setOnClickListener {
            finish()
        }
        val bundle = intent.extras
        id_zone    = bundle?.getInt("ID_ZONE") ?: 0
        name_zone  = bundle?.getString("NAME_ZONE") ?: ""
        count      = bundle?.getInt("COUNT") ?: 0
       // image_zone = bundle?.getString("IMAGE_ZONE") ?: ""
        /*Picasso.get()
            .load(image_zone)
            .placeholder(R.drawable.imagennd)
            .error(R.drawable.imagennd)
            .into(binding.imageMesaZone)*/
        binding.textIdZone.setText(id_zone.toString())
        binding.textZonaName.setText("Ubicación: $name_zone")
        binding.textColumnRow.setText("Cantidad: $count")
        if(!QRSanerApplication.preference.getKeyValue().equals("") && !QRSanerApplication.preference.getIdArticle().equals("")) {
            iniRecycleViewMesaTable()
            getMesaTable()
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
    private fun iniRecycleViewMesaTable() {
        adpater_mesa_table = MesaTableAdapter(dogMesaTarble, this, id_zone, name_zone, count )
        binding.listMesaTable.layoutManager= LinearLayoutManager(this)
        binding.listMesaTable.adapter = adpater_mesa_table
    }
    private fun getMesaTable(){
        binding.loadingProgessbar.visibility= View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getMesaTablas(QRSanerApplication.preference.getKeyValue(),id_zone)
            val response =call.body()
            runOnUiThread {
                binding.loadingProgessbar.visibility= View.GONE

                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("LIST_MESA_TABLE", "respuesta $response")
                        if(response.success==true){
                            loadMesaTable(response.data)
                        }else{
                            showResponse(response.status)
                        }
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
    private fun loadMesaTable(
        mesa_table: ArrayList<MesaTabla>?
    ) {
        dogMesaTarble.clear()
        if (mesa_table != null) {
            dogMesaTarble.addAll(mesa_table)
        }
        adpater_mesa_table.notifyDataSetChanged()
    }
}