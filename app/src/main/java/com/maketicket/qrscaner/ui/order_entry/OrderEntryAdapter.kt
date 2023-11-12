package com.maketicket.qrscaner.ui.order_entry

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.model.OrderEntry

class OrderEntryAdapter(val order_entry: List<OrderEntry>, val activity: Activity?): RecyclerView.Adapter<OrderEntryHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderEntryHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context
        return OrderEntryHolder(layoutInflater.inflate(R.layout.iten_qr_scaner, parent, false))
    }
    override fun onBindViewHolder(holder: OrderEntryHolder, position: Int) {
        holder.bind(order_entry[position], activity)
    }
    override fun getItemCount(): Int = order_entry.size

}