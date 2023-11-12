package com.maketicket.qrscaner.ui.detail_silla.funtion

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.response.funciones

class FuntionAdapter(
    val function: List<funciones>,
    val activity: Activity?,
    val ban: String?,
    val type: String
): RecyclerView.Adapter<FuntionHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuntionHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context
        return FuntionHolder(layoutInflater.inflate(R.layout.iten_list_function_home, parent, false), type )
    }
    override fun onBindViewHolder(holder: FuntionHolder, position: Int) {
        holder.bind(function[position], activity, ban)
    }
    override fun getItemCount(): Int = function.size
}