package com.maketicket.qrscaner.ui.partaker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.model.Partaker

class PartakerAdapter(val partakers: MutableList<Partaker>): RecyclerView.Adapter<PartakertHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartakertHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context
        return PartakertHolder(layoutInflater.inflate(R.layout.iten_qr_scaner_partaker, parent, false))
    }

    override fun onBindViewHolder(holder: PartakertHolder, position: Int) {
        Log.d("MODELHELPER version 3 fin", "")
        Log.d("MODELHELPER pos", position.toString())
        Log.d("MODELHELPER partakers", partakers.toString())
        holder.bind(
            partakers[position].code,
            partakers[position].id,
            partakers[position].email.toString(),
            "${partakers.get(position).name} ${partakers.get(position).lastname}",
            partakers[position].company,
            partakers[position].category.toString()
        )

    }
    override fun getItemCount(): Int = partakers.size



}
