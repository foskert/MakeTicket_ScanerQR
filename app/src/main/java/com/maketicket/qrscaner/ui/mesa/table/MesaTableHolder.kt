package com.maketicket.qrscaner.ui.mesa.table


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ItenListMesaTableBinding
import com.maketicket.qrscaner.ui.mesa.silla.MesaSillaActivity
import com.maketicket.qrscaner.ui.model.MesaTabla
import com.squareup.picasso.Picasso

class MesaTableHolder(view:View): RecyclerView.ViewHolder(view) {
    private val binding = ItenListMesaTableBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(iten: MesaTabla, activity: Activity?, id_zone:Int, name_zone:String, count_zone:Int) {
        binding.textIdTable.setText("${iten.id_table}")
        binding.textTableName.setText("Ubicación: ${iten.name_table}")
        binding.textColumnRow.setText("Estado: ${iten.status} ")
        /*Picasso.get()
            .load(iten.imagen)
            .placeholder(R.drawable.imagennd)
            .error(R.drawable.imagennd)
            .into(binding.imageMesaTable)*/

        binding.layoutMesaTable.setOnClickListener {

            val intent = Intent(it.context, MesaSillaActivity::class.java)
            intent.putExtra("ID_TABLE", iten.id_table)
            intent.putExtra("NAME_TABLE", iten.name_table)
            intent.putExtra("STATUS_TABLE", iten.status)
           // intent.putExtra("IMAGE_TABLE", iten.imagen)

            intent.putExtra("ID_ZONE", id_zone)
            intent.putExtra("NAME_ZONE", name_zone)
            intent.putExtra("COUNT_ZONE", count_zone)
           // intent.putExtra("IMAGE_ZONE", image_zone)
            it.context.startActivity(intent)
        }
    }
}

