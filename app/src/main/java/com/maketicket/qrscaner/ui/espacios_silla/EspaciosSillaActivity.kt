package com.maketicket.qrscaner.ui.espacios_silla

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ActivityEspaciosSillaBinding
import com.maketicket.qrscaner.ui.detail_silla.DetailSillaAdapter
import com.maketicket.qrscaner.ui.model.EspacioArray
import com.maketicket.qrscaner.ui.model.EspaciosSilla
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EspaciosSillaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEspaciosSillaBinding
    private lateinit var adpater_silla: DetailSillaAdapter
    private val dogEspacioSilla = mutableListOf<EspaciosSilla>()
    private lateinit var loading_progessbar: ProgressBar
    private lateinit var imagen_espacio: ImageView
    private lateinit var viewDisableLayout: View
    private lateinit var layout_lienzo: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEspaciosSillaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReturnDetailSilla.setOnClickListener {
            finish()
        }
        val bundle   = intent.extras
        val id_article = bundle?.getInt("ID_ARTICLE") ?: 0
        val name_article = bundle?.getString("NAME_ARTICLE")?: ""
        binding.textEscenario.setText("ESCENARIO $name_article")
        imagen_espacio = findViewById(R.id.image_espacio)
        loading_progessbar = findViewById(R.id.loading_progessbar)
        viewDisableLayout = findViewById(R.id.viewDisableLayout)
        layout_lienzo = findViewById<ConstraintLayout>(R.id.layout_lienzo)

        if (!QRSanerApplication.preference.getKeyValue().equals("") && id_article> 0) {
            loading_progessbar.visibility = View.VISIBLE
            getEspacioArray(id_article)
        } else {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun getEspacioArray(id_article: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRestEngine().create(OrderEntryService::class.java).getEspacioSillas(
                QRSanerApplication.preference.getKeyValue(),
                id_article
            )//QRSanerApplication.preference.getIdArticle())
            val response = call.body()
            Log.d("LIST_SILLAS1", "respuesta ${call.body().toString()}")
            runOnUiThread {


                loading_progessbar.visibility = View.GONE
                if (call.isSuccessful) {
                    if (response != null) {
                        Log.d("LIST_SILLAS", "respuesta $response")
                        if(response.success==true){
                            load(response.data!!)
                        }else{
                            showResponse(response.status)
                        }
                    } else {
                        showResponse("Error de respuesta  ")
                    }
                } else {
                    showResponse("Error de conexión ")
                    //finish()
                    //onBackPressed()
                }
            }
        }
    }

    private fun load(it: EspacioArray) {
        var img = it.image!!.replace(" ", "%20")
        Picasso.get()
            .load(img)
            .placeholder(R.drawable.noaviliado)
            .error(R.drawable.noaviliado)
            .fit()
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .priority(Picasso.Priority.NORMAL)
            .into(imagen_espacio)
        var tam = it.cantRows
        var fondo:Lienzo
        fondo = Lienzo(this, it.array, it.cantColumns, it.cantRows )
        layout_lienzo.addView(fondo)

    }

    class Lienzo(context: Context, it:ArrayList<ArrayList<EspaciosSilla>>, col:Int, row:Int ) :
        View(context) {
        var it  = it
        var col = col
        var row = row
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            var rectWidth:Float
            if(col>0){
                rectWidth  = (getWidth()/col).toFloat()
            }else{
                rectWidth  = (getWidth()).toFloat()
            }
            var rectHeight:Float
            if(col>0){
                rectHeight  = (getHeight()/row).toFloat()
            }else{
                rectHeight  = (getHeight()).toFloat()
            }

            if(rectWidth>rectHeight){
                rectWidth=rectHeight
            }
            canvas.drawRGB(0, 0, 0)


            canvas.apply {

                val pincel1 = Paint()

                for(x in 0..it.size-1) {
                   // var y=0
                    for(y in 0..it[x].size-1) {
                        pincel1.setColor(Color.parseColor("#FFFFFF"))
                        pincel1.setStrokeWidth(1F)
                        if(it[x][y].status==1){
                            if(it[x][y].sold==0){
                                pincel1.setARGB(255, 0, 135, 60)
                                //pincel1.setARGB(255, 152, 0, 0)
                            }else if(it[x][y].sold==2){
                                pincel1.setARGB(255, 0, 75, 152)
                                //pincel1.setARGB(255, 152, 0, 0)
                            }else if(it[x][y].sold==3){
                                pincel1.setARGB(255, 152, 0, 0)
                            }else{
                                pincel1.setARGB(255, 152, 0, 0)
                                //pincel1.setARGB(255, 0, 135, 60)
                               // pincel1.setARGB(255, 255, 255, 255)
                            }

                        }else if(it[x][y].status==0){
                            pincel1.setARGB(0, 0, 0, 0)
                        }
                        save()

                        translate(rectWidth*y, rectWidth*x)

                        pincel1.setStyle(Paint.Style.FILL);
                        drawRect(0F, 0F,  rectWidth, rectWidth, pincel1)
                        pincel1.setStyle(Paint.Style.STROKE);
                        pincel1.setColor(Color.BLACK);
                        pincel1.setStrokeWidth(2F)
                        drawRect(0F, 0F,  rectWidth, rectWidth, pincel1)
                        restore()
                    }
                }
            }
        }
    }


}



