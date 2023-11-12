package com.maketicket.qrscaner.ui.mesa.silla

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
import com.maketicket.qrscaner.databinding.ActivityMesaSillaBinding
import com.maketicket.qrscaner.databinding.ActivityMesaTableBinding
import com.maketicket.qrscaner.ui.mesa.table.MesaTableAdapter
import com.maketicket.qrscaner.ui.model.MesaSilla
import com.maketicket.qrscaner.ui.model.MesaTabla
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MesaSillaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMesaSillaBinding
    lateinit var text_name_article: TextView
    private lateinit var adpater_mesa_silla: MesaSillaAdapter
    private val dogMesaSilla = mutableListOf<MesaSilla>()
    private var  id_zone:Int = 0
    private var  name_zone:String = ""
    private var  count_zone:Int = 0
    private var  image_zone:String = ""
    private var  id_table:Int = 0
    private var  name_table:String = ""
    private var  status_table:Int = 0
    private var  image_table:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesaSillaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReturnDetailSilla.setOnClickListener {
            finish()
        }

        val bundle   = intent.extras
        id_zone      = bundle?.getInt("ID_ZONE") ?: 0
        name_zone    = bundle?.getString("NAME_ZONE") ?: ""
        count_zone   = bundle?.getInt("COUNT_ZONE") ?: 0
        image_zone   = bundle?.getString("IMAGE_ZONE") ?: ""

        id_table     = bundle?.getInt("ID_TABLE") ?: 0
        name_table   = bundle?.getString("NAME_TABLE") ?: ""
        status_table = bundle?.getInt("STATUS_TABLE") ?: 0
        image_table  = bundle?.getString("IMAGE_TABLE") ?: ""
        /*if(image_table!= null &&  !image_table.equals("")){
            Picasso.get()
                .load(image_table)
                .placeholder(R.drawable.imagennd)
                .error(R.drawable.imagennd)
                .into(binding.imageMesaTable)
        }*/

        binding.textIdTable.setText(id_table.toString())
        binding.textTableName.setText("Ubicacón: $name_table")
        binding.textStatus.setText("Estado: $status_table")
      /*  Picasso.get()
            .load(image_zone)
            .placeholder(R.drawable.imagennd)
            .error(R.drawable.imagennd)
            .into(binding.imageMesaZone)*/
        binding.textIdZone.setText(id_zone.toString())
        binding.textZonaName.setText("Ubicación: $name_zone")
        binding.textCount.setText("Cantidad: $count_zone")
        if(!QRSanerApplication.preference.getKeyValue().equals("") && !QRSanerApplication.preference.getIdArticle().equals("")) {
            iniRecycleViewMesaSilla()
            getMesaSilla()
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
    private fun iniRecycleViewMesaSilla() {
        adpater_mesa_silla = MesaSillaAdapter(dogMesaSilla, this)
        binding.listMesaSillas.layoutManager= LinearLayoutManager(this)
        binding.listMesaSillas.adapter = adpater_mesa_silla
    }
    private fun getMesaSilla(){
        binding.loadingProgessbar.visibility= View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getMesaSilla(
                QRSanerApplication.preference.getKeyValue(),id_table)
            val response =call.body()
            runOnUiThread {
                binding.loadingProgessbar.visibility= View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("LIST_MESA_TABLE", "respuesta $response")

                        loadMesaSilla(response)
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
    private fun loadMesaSilla(
        mesa_silla: ArrayList<MesaSilla>?
    ) {
        dogMesaSilla.clear()
        if (mesa_silla != null) {
            dogMesaSilla.addAll(mesa_silla)
        }
        adpater_mesa_silla.notifyDataSetChanged()
    }
}