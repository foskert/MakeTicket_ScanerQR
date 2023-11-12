package com.maketicket.qrscaner.ui.mesa.zona

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ItenListMesaZonaBinding
import com.maketicket.qrscaner.ui.mesa.table.MesaTableActivity
import com.maketicket.qrscaner.ui.model.MesaZona
import com.squareup.picasso.Picasso

class MesaZonaHolder (view: View): RecyclerView.ViewHolder(view) {
    private val binding= ItenListMesaZonaBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(iten: MesaZona, activity: Activity?){
        binding.textIdZone.setText("${iten.id_zone}")
        binding.textZonaName.setText("Ubicación: ${iten.zone_name}")
        binding.textColumnRow.setText("Cantidad: ${iten.quant_table} ")
        /*Picasso.get()
            .load(iten.imagen)
            .placeholder(R.drawable.imagennd)
            .error(R.drawable.imagennd)
            .into(binding.imageMesaZone)*/

        binding.layoutMesaZona.setOnClickListener {
            val intent = Intent(it.context, MesaTableActivity::class.java)
            intent.putExtra("ID_ZONE", iten.id_zone )
            intent.putExtra("NAME_ZONE", iten.zone_name)
            intent.putExtra("COUNT", iten.quant_table)
            //intent.putExtra("IMAGE_ZONE", iten.imagen)
            it.context.startActivity(intent)
        }
    }
}




