package com.maketicket.qrscaner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.maketicket.qrscaner.databinding.ActivitySpaceMesaBinding
import com.maketicket.qrscaner.ui.detail_silla.DetailSillaAdapter
import com.maketicket.qrscaner.ui.model.EspaciosSilla
import com.maketicket.qrscaner.ui.model.Mesa
import com.maketicket.qrscaner.ui.model.SpaceMesa
import com.maketicket.qrscaner.ui.response.SpaceMesaResponse
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SpaceMesaActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpaceMesaBinding
    private lateinit var adpater_silla: DetailSillaAdapter
    private val dogEspacioSilla = mutableListOf<EspaciosSilla>()
    private lateinit var loading_progessbar: ProgressBar
    private lateinit var imagen_espacio: ImageView
    private lateinit var viewDisableLayout: View
    private lateinit var layout_lienzo: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpaceMesaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReturnDetailMesa.setOnClickListener {
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
            val call = getRestEngine().create(OrderEntryService::class.java).getMesa(
                QRSanerApplication.preference.getKeyValue(),
                QRSanerApplication.preference.getIdEvent(),
                id_article
            )//QRSanerApplication.preference.getIdArticle())
            val response = call.body()
            runOnUiThread {

                loading_progessbar.visibility = View.GONE
                if (call.isSuccessful) {
                    if (response != null) {
                       load(response)
                    } else {
                        showResponse("Error de respuesta  ")
                    }
                } else {
                    showResponse("Error de conexión ")
                }
            }
        }
    }

    private fun load(response: SpaceMesaResponse) {
        if(response.array.size==0){
            Snackbar.make(binding.root , "El evento no posee asignación de mesas", Snackbar.LENGTH_INDEFINITE)
                .setAction("Cerrar", View.OnClickListener {
                    System.out.println("Cerrar.")
                }  ).show()
        }
        var img = response.image!!.replace(" ", "%20")
        Picasso.get()
            .load(img)
            .placeholder(R.drawable.noaviliado)
            .error(R.drawable.noaviliado)
            .fit()
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .priority(Picasso.Priority.NORMAL)
            .into(imagen_espacio)
        var fondo: Lienzo
        if(response.array.size>0){
            layout_lienzo.maxHeight=(160*6*response.array.size)
        }else{
            layout_lienzo.maxHeight=(160*7)
        }

        fondo = Lienzo(this, response.array )
        layout_lienzo.addView(fondo)
    }

    class Lienzo(context: Context, val it: ArrayList<SpaceMesa>) :
        View(context) {
       // var it  = it
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            var rectWidth  = (getWidth()/8).toFloat()
            val rectHeight = (getHeight()*it.size*6).toFloat()
            /*if(rectWidth>rectHeight){
                rectWidth=rectHeight
            }*/
            canvas.drawRGB(0, 0, 0)


            canvas.apply {
                val pincel1 = Paint()
                var cuadricula= 0
                var x=0
                it.forEach {
                    it.sillas.sortBy {
                        it.name_chair
                    }
                    for (i in 0..5) {
                        for (j in 0..7) {
                            pincel1.setColor(Color.parseColor("#FFFFFF"))
                            pincel1.setStrokeWidth(1F)
                            pincel1.setARGB(255, 0, 0, 0)
                            save()
                            if(i==2 && (j>=3 || j<=4) ) {
                                pincel1.setColor(Color.BLACK);
                                pincel1.textSize = 75f
                                canvas.drawText(
                                    it.mesa,
                                    rectWidth * 3,
                                    (rectWidth * cuadricula ) + 130,
                                    pincel1
                                )
                            }
                            //sillas superiores
                            if (i == 1) {
                                if (j >= 2 && j <= 5) {
                                    if (it.sillas.size>= x){
                                        if (it.sillas[x].status == 1) {
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +75,
                                                pincel1
                                            )

                                            pincel1.setARGB(150, 20, 130, 70) //verde
                                            x++
                                        } else if (it.sillas[x].status == 2) { //Rojo
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +75,
                                                pincel1
                                            )
                                            //save()
                                            pincel1.setARGB(150, 220, 50, 20)//rojo
                                            x++
                                        } else if (it.sillas[x].status == 3) {  //azul
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +75,
                                                pincel1
                                            )

                                            pincel1.setARGB(150, 10, 90, 180) //azul
                                            x++
                                        } else {
                                            pincel1.setARGB(255, 152, 0, 0)
                                        }
                                    }else{
                                        pincel1.setARGB(255, 160, 160, 160)
                                    }
                                }
                            }
                            //sillas inferiores
                            if (i == 4) {
                                if(j==5){
                                    if (it.sillas.size-1>=6){
                                        if (it.sillas[6].status == 1) {
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[6].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )//verde
                                            save()
                                            pincel1.setARGB(150, 20, 130, 70) //verde
                                            x++
                                        } else if (it.sillas[6].status == 2) { //Rojo
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[6].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )
                                            save()
                                            pincel1.setARGB(150, 220, 50, 20)//rojo
                                            x++
                                        } else if (it.sillas[6].status == 3) {  //azul
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[6].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )

                                            pincel1.setARGB(150, 10, 90, 180) //azul
                                            x++
                                        } else {
                                            pincel1.setARGB(255, 152, 0, 0)
                                        }
                                    }else{
                                        pincel1.setARGB(255, 160, 160, 160)
                                    }
                                }
                                if(j==4){
                                    if (it.sillas.size-1 >=7){
                                        if (it.sillas[7].status == 1) {
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[7].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )//verde
                                            save()
                                            pincel1.setARGB(150, 20, 130, 70) //verde
                                            x++
                                        } else if (it.sillas[7].status == 2) { //Rojo
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[7].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )
                                            save()
                                            pincel1.setARGB(150, 220, 50, 20)//rojo
                                            x++
                                        } else if (it.sillas[7].status == 3) {  //azul
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[7].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )

                                            pincel1.setARGB(150, 10, 90, 180) //azul
                                            x++
                                        } else {
                                            pincel1.setARGB(255, 152, 0, 0)
                                        }
                                    }else{
                                        pincel1.setARGB(255, 160, 160, 160)
                                    }
                                }
                                if(j==3){
                                    if (it.sillas.size-1 >=8){
                                        if (it.sillas[8].status == 1) {
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[8].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )//verde
                                            save()
                                            pincel1.setARGB(150, 20, 130, 70) //verde

                                            x++
                                        } else if (it.sillas[8].status == 2) { //Rojo
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[8].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )
                                            save()
                                            pincel1.setARGB(150, 220, 50, 20)//rojo
                                            x++
                                        } else if (it.sillas[8].status == 3) {  //azul
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[8].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )

                                            pincel1.setARGB(150, 10, 90, 180) //azul
                                            x++
                                        } else {
                                            pincel1.setARGB(255, 152, 0, 0)
                                        }
                                    }else{
                                        pincel1.setARGB(255, 160, 160, 160)
                                    }
                                }
                                if(j==2){
                                    if (it.sillas.size-1 >= 9){
                                        if (it.sillas[9].status == 1) {
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[9].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )//verde
                                            save()
                                            pincel1.setARGB(150, 20, 130, 70) //verde

                                            x++
                                        } else if (it.sillas[9].status == 2) { //Rojo
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[9].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )
                                            save()
                                            pincel1.setARGB(150, 220, 50, 20)//rojo
                                            x++
                                        } else if (it.sillas[9].status == 3) {  //azul
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[9].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )

                                            pincel1.setARGB(150, 10, 90, 180) //azul
                                            x++
                                        } else {
                                            pincel1.setARGB(255, 152, 0, 0)
                                        }
                                    }else{
                                        pincel1.setARGB(255, 160, 160, 160)
                                    }
                                }
                            }
                            //pinta la mesa
                            if (i == 2 ) {
                                if (j == 1 ) {
                                    if (it.sillas.size>= x && x == 11){
                                        if (it.sillas[x].status == 1) {
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )//verde
                                            save()
                                            pincel1.setARGB(150, 20, 130, 70) //verde

                                            x++
                                        } else if (it.sillas[x].status == 2) { //Rojo
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )
                                            save()
                                            pincel1.setARGB(150, 220, 50, 20)//rojo
                                            x++
                                        } else if (it.sillas[x].status == 3) {  //azul
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )

                                            pincel1.setARGB(150, 10, 90, 180) //azul
                                            x++
                                        } else {
                                            pincel1.setARGB(255, 152, 0, 0)
                                        }
                                    }else{
                                        pincel1.setARGB(255, 160, 160, 160)
                                    }
                                } else if (j >= 2 && j <= 5) {
                                    pincel1.setColor(Color.WHITE);
                                } else if (j == 6) {
                                    if (it.sillas.size >= x  && x==4){
                                        if (it.sillas[x].status == 1) {
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )//verde
                                            save()
                                            pincel1.setARGB(150, 20, 130, 70) //verde

                                            x++
                                        } else if (it.sillas[x].status == 2) { //Rojo
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )
                                            save()
                                            pincel1.setARGB(150, 220, 50, 20)//rojo
                                            x++
                                        } else if (it.sillas[x].status == 3) {  //azul
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )

                                            pincel1.setARGB(150, 10, 90, 180) //azul
                                            x++
                                        } else {
                                            pincel1.setARGB(255, 152, 0, 0)
                                        }
                                    }else{
                                        pincel1.setARGB(255, 160, 160, 160)
                                    }
                                }
                            }
                            if ( i == 3) {
                                if (j == 1) {
                                    if (it.sillas.size >= x  && x==10){
                                        if (it.sillas[x].status == 1) {
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )//verde
                                            save()
                                            pincel1.setARGB(150, 20, 130, 70) //verde

                                            x++
                                        } else if (it.sillas[x].status == 2) { //Rojo
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )
                                            save()
                                            pincel1.setARGB(150, 220, 50, 20)//rojo
                                            x++
                                        } else if (it.sillas[x].status == 3) {  //azul
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )

                                            pincel1.setARGB(150, 10, 90, 180) //azul
                                            x++
                                        } else {
                                            pincel1.setARGB(255, 152, 0, 0)
                                        }
                                    }else{
                                        pincel1.setARGB(255, 160, 160, 160)
                                    }
                                } else if (j >= 2 && j <= 5) {
                                    pincel1.setColor(Color.WHITE);
                                } else if (j == 6) {
                                    if (it.sillas.size+1 >= x  && x==5){
                                        if (it.sillas[x].status == 1) {
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )//verde
                                            save()
                                            pincel1.setARGB(150, 20, 130, 70) //verde

                                            x++
                                        } else if (it.sillas[x].status == 2) { //Rojo
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )
                                            save()
                                            pincel1.setARGB(150, 220, 50, 20)//rojo
                                            x++
                                        } else if (it.sillas[x].status == 3) {  //azul
                                            pincel1.setColor(Color.WHITE);
                                            pincel1.textSize = 55f
                                            canvas.drawText(
                                                it.sillas[x].name_chair,
                                                (rectWidth * j)+25,
                                                (rectWidth * cuadricula ) +80,
                                                pincel1
                                            )

                                            pincel1.setARGB(150, 10, 90, 180) //azul
                                            x++
                                        } else {
                                            pincel1.setARGB(255, 152, 0, 0)
                                        }
                                    }else{
                                        pincel1.setARGB(255, 160, 160, 160)
                                    }
                                }
                            }

                            translate(rectWidth * j, rectWidth * cuadricula)
                            pincel1.setStyle(Paint.Style.FILL);
                            drawRect(0F, 0F, rectWidth, rectWidth, pincel1)
                            pincel1.setStyle(Paint.Style.STROKE);
                            if (i == 2 || i == 3) {
                                if (j >= 2 && j <= 5) {
                                    pincel1.setColor(Color.WHITE);
                                }else{
                                    pincel1.setColor(Color.BLACK);
                                }
                            }else {
                                pincel1.setColor(Color.BLACK);
                            }

                            pincel1.setStrokeWidth(2F)
                            drawRect(0F, 0F, rectWidth, rectHeight, pincel1)
                            restore()

                        }
                        cuadricula++
                    }
                    x=0
                }
                /*
                x= 0F
                it.forEach {
                    y=0F
                    it.sillas.forEach {
                        pincel1.setColor(Color.parseColor("#FFFFFF"))
                        pincel1.setStrokeWidth(1F)
                        if (it.status == 1) {       //verde
                            pincel1.setARGB(255, 0, 135, 60)
                        } else if (it.status == 2) { //Rojo
                            pincel1.setARGB(255, 0, 75, 152)
                        } else if (it.status == 3) {  //azul
                            pincel1.setARGB(255, 152, 0, 0)
                        } else {
                            pincel1.setARGB(255, 152, 0, 0)
                        }
                        save()

                        translate(rectWidth * y, rectWidth * x)
                        pincel1.setStyle(Paint.Style.FILL);
                        drawRect(0F, 0F, rectWidth, rectWidth, pincel1)
                        pincel1.setStyle(Paint.Style.STROKE);
                        pincel1.setColor(Color.BLACK);
                        pincel1.setStrokeWidth(2F)
                        drawRect(0F, 0F, rectWidth, rectWidth, pincel1)
                        restore()
                        y++

                    }
                    x++
                }*/
                /*  for(x in 0..it.size-1) {

                    // var y=0
                    for(y in 0..it.[x].size-1) {
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
            }*/
            }
        }
    }

}