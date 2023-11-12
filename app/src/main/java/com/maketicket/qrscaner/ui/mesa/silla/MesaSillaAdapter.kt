package com.maketicket.qrscaner.ui.mesa.silla

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.model.MesaSilla

class MesaSillaAdapter(val mesa_silla: List<MesaSilla>, val activity: Activity?): RecyclerView.Adapter<MesaSillaHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaSillaHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context
        return MesaSillaHolder(layoutInflater.inflate(R.layout.iten_list_detail_silla, parent, false))
    }
    override fun onBindViewHolder(holder: MesaSillaHolder, position: Int) {
        holder.bind(mesa_silla[position], activity)
    }
    override fun getItemCount(): Int = mesa_silla.size

}