package com.maketicket.qrscaner.ui.detail_silla

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ActivityDetailSillaBinding
import com.maketicket.qrscaner.ui.model.Silla
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailSillaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailSillaBinding
    lateinit var text_name_article:TextView
    private lateinit var adpater_silla: DetailSillaAdapter
    private val dogSilla = mutableListOf<Silla>()
    private lateinit var imageLoader:ImageView
    private lateinit var loading_progessbar: ProgressBar
    private lateinit var viewDisableLayout: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailSillaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReturnDetailSilla.setOnClickListener{
            finish()
        }

        loading_progessbar = findViewById(R.id.loading_progessbar)
        imageLoader = findViewById(R.id.loading)
        viewDisableLayout = findViewById(R.id.viewDisableLayout)
        imageLoader.visibility = View.GONE
        val bundle   = intent.extras
        val id_function = bundle?.getInt("ID_FUNCTION") ?: 0
        if(!QRSanerApplication.preference.getKeyValue().equals("") && !QRSanerApplication.preference.getIdArticle().equals("")) {
            iniRecycleViewSilla()
            getSillaValue(id_function)
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
    private fun iniRecycleViewSilla() {
        adpater_silla= DetailSillaAdapter(dogSilla, this, loading_progessbar, viewDisableLayout)
        binding.listSillas.layoutManager= LinearLayoutManager(this)
        binding.listSillas.adapter = adpater_silla
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getSillaValue(id_function: Int) {
        binding.loadingProgessbar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getSillas(QRSanerApplication.preference.getKeyValue(), id_function)
            val response =call.body()
            runOnUiThread {
                binding.loadingProgessbar.visibility = View.GONE

                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("LIST_SILLAS", "respuesta $response")

                        loadSilla(response)
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
    private fun loadSilla(
        silla: ArrayList<Silla>?
    ) {
        dogSilla.clear()
        if (silla != null) {
            dogSilla.addAll(silla)
        }
        if (silla != null) {
            dogSilla.addAll(silla)

            Snackbar.make(binding.root , "Total de Sillas en evento disponibles: ${dogSilla.size}  ", Snackbar.LENGTH_LONG)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("Cerrar.")

                }  ).show()
        }else{
            Snackbar.make(binding.root , "No existe data para de evento  ", Snackbar.LENGTH_LONG)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("Cerrar.")

                }  ).show()
        }
        adpater_silla.notifyDataSetChanged()

    }


}
