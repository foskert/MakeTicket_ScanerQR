package com.maketicket.qrscaner.ui.list_event

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.maketicket.qrscaner.LoginActivity
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.body.EventBody
import com.maketicket.qrscaner.ui.model.Attribute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArgonModal(val iten: Attribute, val ban: Int, val activity: ListEventActivity, val til: String) : DialogFragment() {
    private lateinit var imageCanel: ImageView
    private lateinit var edit: EditText
    private lateinit var error: TextView
    private lateinit var title: TextView
    private lateinit var btn: Button
    private lateinit var progressBar: ProgressBar

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.modal_argon, container, false)
        imageCanel = rootView.findViewById(R.id.img_cancel_type)
        error = rootView.findViewById(R.id.error_text_pocition)
        error.visibility = View.GONE

        imageCanel.setOnClickListener {
            dialog!!.dismiss()
        }
        progressBar = rootView.findViewById(R.id.loading_progessbar_event)
        progressBar.visibility = View.GONE

        edit  = rootView.findViewById(R.id.edit_text_position)
        title = rootView.findViewById(R.id.text_title_position)
        title.text = til
        btn = rootView.findViewById(R.id.btn_pocition)
        btn.setOnClickListener {
            if(!edit.text.toString().equals("")){
                if (ban == 1) {
                    setBanner( edit.text.toString().toInt());
                } else if (ban == 2) {
                    setEvent( edit.text.toString().toInt());
                }else{
                    error.text = "No es posible definir el tipo de evento"
                    error.visibility = View.VISIBLE
                }
            }else{
                error.text = "Debes ingresar un valor numerico"
                error.visibility = View.VISIBLE
            }
        }
        return rootView
    }
    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.maketicket.com.ve/api/makeidsystems/external/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun setEvent( toInt: Int) {
        progressBar.visibility = View.VISIBLE
        btn.isEnabled=false
        val body = EventBody(iten.id.toInt(), toInt)
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java)
                .setEvent("makeidsystems", "63b4b3eba51e48.20916573", body )
            val response =call.body()
            activity.runOnUiThread {
                btn.isEnabled=true
                progressBar.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        if(response.data.success){
                            showResponse("Actualización completada")
                            activity.finish()
                            startActivity(Intent(activity, ListEventActivity::class.java))
                            dismiss()

                        }else{
                            load("No se logro completar la actualización")
                        }
                    }else{
                        load("Error de respuesta")
                    }
                }else{
                    load("Error de conexión")
                }
            }
        }
    }
    private fun setBanner(toInt: Int) {
        progressBar.visibility = View.VISIBLE
        btn.isEnabled=false
        CoroutineScope(Dispatchers.IO).launch{
            val body = EventBody(iten.id.toInt(), toInt)
            val call = getRestEngine().create(OrderEntryService:: class.java)
                .setBanner("makeidsystems", "63b4b3eba51e48.20916573", body )
            val response =call.body()
            error.visibility = View.GONE
            activity.runOnUiThread {
                btn.isEnabled=true
                progressBar.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        if(response.data.success){
                            showResponse("Actualización completada")
                            activity.finish()
                            startActivity(Intent(activity, ListEventActivity::class.java))
                            dismiss()
                        }else{
                            load("No se logro completar la actualización")
                        }

                    }else{
                        load("Error de respuesta")
                    }
                }else{
                    load("Error de conexión")
                }
            }
        }
    }
    private fun load(message: String) {
            error.text = message
            error.visibility = View.VISIBLE
    }
    private fun showResponse(body: String?) {
        Toast.makeText(activity, "$body", Toast.LENGTH_LONG).show()
    }
}