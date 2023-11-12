package com.maketicket.qrscaner

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import android.widget.*
import com.google.zxing.integration.android.IntentIntegrator
import com.maketicket.qrscaner.QRSanerApplication.Companion.preference
import com.maketicket.qrscaner.databinding.ActivityKeyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KeyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKeyBinding
    private lateinit var btn_register: Button
    private lateinit var btn_return: ImageView
    private lateinit var input_token: EditText
    private lateinit var text_body: TextView
    private lateinit var text_header: TextView
    private lateinit var btn_qr: ImageView
    private var ex:Int = 0


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeyBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_key)
        val bundle = intent.extras
        ex = bundle?.getInt("KEY") ?: 0
        btn_return =  findViewById(R.id.key_return)
        btn_register = findViewById(R.id.key_btn_registrer)
        input_token = findViewById(R.id.key_input_token)
        text_header = findViewById(R.id.key_text_header)
        text_body = findViewById(R.id.key_text_body)
        btn_qr = findViewById(R.id.key_qr)
        if(ex == 1){
            text_header.setText("Registro token")
            text_body.setText("Ingresa el token, para la verificación  de QR, el token estara activo por el evento correspondiente")
        }else if(ex == 2){
            text_header.setText("ID de evento")
            text_body.setText("Ingresa el ID del evento, este ID estara disponible para verificar el número de asistentes al evento correspondiente")
        }else if(ex == 3){
            text_header.setText("ID de la función")
            text_body.setText("Ingresa el ID de la función, este ID estara disponible para verificar el número de asistentes al evento correspondiente")
        }
        btn_return.setOnClickListener{
            returnDashboar()
        }
        btn_register.setOnClickListener{
            if(input_token.text.toString().isNotEmpty()) {
                tokenSesion(input_token.text.toString())
            }else {
                if(ex == 1){
                    Toast.makeText(this, "El token no debe ser nulo", Toast.LENGTH_LONG).show()
                }else if(ex == 2){
                    Toast.makeText(this, "El ID del evento no debe ser nulo", Toast.LENGTH_LONG).show()
                }else if(ex == 3){
                    tokenSesion(input_token.text.toString())
                }
            }
        }
        btn_qr.setOnClickListener {
            setQRCode()
        }
    }

    private fun returnDashboar() {
        // Toast.makeText(this, "Entro", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DashboardActivity::class.java).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "565656565")
        }
        finish()
        startActivity(intent)
    }

    private fun tokenSesion(key:String) {
        if(ex == 1){
            preference.setKeyValue(key)
            if(preference.getKeyValue().isNotEmpty()){
                Toast.makeText(this, "Registro exitoso ", Toast.LENGTH_LONG).show()
                returnDashboar()
            }
        }else if(ex == 2){
            iniEvent(key.toInt())
            preference.setIdEvent(key.toInt())
            if(preference.getIdEvent()>0){
                Toast.makeText(this, "Registro exitoso ", Toast.LENGTH_LONG).show()
                returnDashboar()
            }
        } else if(ex == 3){
            if(!key.equals("")){
                iniEvent(key.toInt())
                preference.setIdArticle(key.toInt())
            }else{
                iniEvent(0)
                preference.setIdArticle(0)
            }
            Toast.makeText(this, "Registro exitoso ", Toast.LENGTH_LONG).show()
            returnDashboar()
        }
    }

    private fun iniEvent(id_event:Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RestEngine.getRestEngine().create(OrderEntryService::class.java)
                .getOrderAdmitted(id_event, preference.getKeyValue())
            val response = call.body()
            // runOnUiThread {
            if (call.isSuccessful) {
                if (response != null) {
                    if (response.success) {
                        response.total?.forEach {
                            preference.setNameEvent(it.name.toString())
                        }
                        Log.d("FUNCTION", "json ${response.toString()}")
                        Log.d("FUNCTION", "key ${preference.getKeyValue()}")
                        Log.d("FUNCTION", "event ${preference.getIdEvent()}")
                        Log.d("FUNCTION", "function ${preference.getIdArticle()}")
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

    private fun setQRCode() {
        IntentIntegrator(this)
            .setPrompt("ESCANEO DE TOKEN")
            .setTorchEnabled(false)
            .setBeepEnabled(true)
            .initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null){
            if(result.contents !== null){
                input_token.setText( result.contents.toString())
            }else{
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}


