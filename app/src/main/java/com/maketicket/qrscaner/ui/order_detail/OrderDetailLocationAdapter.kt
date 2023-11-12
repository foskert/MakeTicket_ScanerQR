package com.maketicket.qrscaner.ui.order_detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.response.Location

class OrderDetailLocationAdapter(val listLocation:List<Location>): RecyclerView.Adapter<OrderDetailLocationHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailLocationHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context
        return OrderDetailLocationHolder(layoutInflater.inflate(R.layout.iten_list_order_detail_location, parent, false))
    }
    override fun onBindViewHolder(holder: OrderDetailLocationHolder, position: Int) {
        holder.bind(listLocation[position])
    }
    override fun getItemCount(): Int = listLocation.size
}
