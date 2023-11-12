package com.maketicket.qrscaner.ui.detail_silla

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.model.Silla

class DetailSillaAdapter(
    val silla: List<Silla>,
    val activity: Activity?,
    val loading_progessbar: ProgressBar,
    val viewDisableLayout: View,
): RecyclerView.Adapter<DetailSillaHolder>() {
        private lateinit var  view: Context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailSillaHolder {
            val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
            view= parent.context
            return DetailSillaHolder(layoutInflater.inflate(R.layout.iten_list_detail_silla, parent, false))
        }
        override fun onBindViewHolder(holder: DetailSillaHolder, position: Int) {
            holder.bind(silla[position], activity, loading_progessbar, viewDisableLayout)
        }
        override fun getItemCount(): Int = silla.size
}