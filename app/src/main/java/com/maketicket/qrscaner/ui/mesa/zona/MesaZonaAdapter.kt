package com.maketicket.qrscaner.ui.mesa.zona

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.model.MesaZona

class MesaZonaAdapter (val mesa_zona: List<MesaZona>, val activity: Activity?): RecyclerView.Adapter<MesaZonaHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaZonaHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context
        return MesaZonaHolder(layoutInflater.inflate(R.layout.iten_list_mesa_zona, parent, false))
    }
    override fun onBindViewHolder(holder: MesaZonaHolder, position: Int) {
        holder.bind(mesa_zona[position], activity)
    }
    override fun getItemCount(): Int = mesa_zona.size
}