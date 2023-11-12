package com.maketicket.qrscaner.ui.order_dni

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.detail_silla.DetailSillaHolder
import com.maketicket.qrscaner.ui.model.OrderDNI
import com.maketicket.qrscaner.ui.model.Silla

class OrderDniAdapter(
    val order_dni: List<OrderDNI>,
    val activity: Activity?,
    val loading_progessbar: ProgressBar,
    val viewDisableLayout: View,
): RecyclerView.Adapter<OrderDniHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDniHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context
        return OrderDniHolder(layoutInflater.inflate(R.layout.iten_qr_scaner, parent, false))
    }
    override fun onBindViewHolder(holder: OrderDniHolder, position: Int) {
        holder.bind(order_dni[position], activity, loading_progessbar, viewDisableLayout)
    }
    override fun getItemCount(): Int = order_dni.size
}