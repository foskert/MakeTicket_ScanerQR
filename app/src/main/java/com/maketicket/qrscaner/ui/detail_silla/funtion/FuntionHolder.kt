package com.maketicket.qrscaner.ui.detail_silla.funtion

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.SpaceMesaActivity
import com.maketicket.qrscaner.databinding.ItenListFunctionHomeBinding
import com.maketicket.qrscaner.ui.detail_silla.DetailSillaActivity
import com.maketicket.qrscaner.ui.espacios_silla.EspaciosSillaActivity
import com.maketicket.qrscaner.ui.response.SpaceMesaResponse
import com.maketicket.qrscaner.ui.response.funciones

class FuntionHolder(view: View, val type: String): RecyclerView.ViewHolder(view) {
    private val binding= ItenListFunctionHomeBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(iten: funciones, activity: Activity?, ban: String?){
        binding.textCodeFunction.setText("${iten.id}")
        binding.textHomeFunction.setText("${iten.funcion}." )

        binding.layoutFunction.setOnClickListener {

            if(ban.equals("ESPACIOS")){
                val intent: Intent
                if(type == "silla"){
                    intent = Intent(it.context, EspaciosSillaActivity::class.java)
                }else{
                     intent = Intent(it.context, SpaceMesaActivity::class.java)
                }
                intent.putExtra("ID_ARTICLE", iten.id )
                intent.putExtra("NAME_ARTICLE", iten.funcion)
                it.context.startActivity(intent)
            }else{
                val intent = Intent(it.context, DetailSillaActivity::class.java)
                intent.putExtra("ID_FUNCTION", iten.id )
                intent.putExtra("NAME_FUNCTION", iten.funcion)
                it.context.startActivity(intent)
            }

        }
    }
}




