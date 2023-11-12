package com.maketicket.qrscaner.ui.mesa.table

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.model.MesaTabla

class MesaTableAdapter(val mesa_table: List<MesaTabla>, val activity: Activity?, val  id_zone:Int, val name_zone:String, val count_zone:Int): RecyclerView.Adapter<MesaTableHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaTableHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context
        return MesaTableHolder(layoutInflater.inflate(R.layout.iten_list_mesa_table, parent, false))
    }
    override fun onBindViewHolder(holder: MesaTableHolder, position: Int) {
        holder.bind(mesa_table[position], activity, id_zone, name_zone, count_zone)
    }
    override fun getItemCount(): Int = mesa_table.size

}