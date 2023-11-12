package com.maketicket.qrscaner.ui.order_entered

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.model.OrderEntered

class OrderEnteredAdapter (val listOrderEntered: List<OrderEntered>): RecyclerView.Adapter<OrderEnteredHolder>() {
    private lateinit var  view: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderEnteredHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view = parent.context
        return OrderEnteredHolder(layoutInflater.inflate(R.layout.iten_qr_scaner, parent, false))
    }
    override fun onBindViewHolder(holder: OrderEnteredHolder, position: Int) {
        holder.bind(listOrderEntered, position)
    }
    override fun getItemCount(): Int = listOrderEntered.size

}