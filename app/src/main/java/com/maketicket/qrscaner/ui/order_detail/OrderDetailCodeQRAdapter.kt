package com.maketicket.qrscaner.ui.order_detail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.response.PurchaseOrderTicket

class OrderDetailCodeQRAdapter (var listCodeQR:List<PurchaseOrderTicket>): RecyclerView.Adapter<OrderDetailCodeQRHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailCodeQRHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context
        return OrderDetailCodeQRHolder(layoutInflater.inflate(R.layout.iten_list_order_detail_code_qr, parent, false))

    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: OrderDetailCodeQRHolder, position: Int) {
       // val id_purchase_order: Int = listCodeQR[position].id_purchase_order
       // val id_purchase_order: Int = listCodeQR[position].id_purchase_order
        holder.bind(listCodeQR[position])
    }
    override fun getItemCount(): Int = listCodeQR.size
   /* override fun allCheck(){
        listCodeQR
    }*/
   @SuppressLint("NotifyDataSetChanged")
   fun UpdateData(listCodeQRUpdate:List<PurchaseOrderTicket>) {

        //listCodeQR=listCodeQRUpdate
        notifyDataSetChanged()
        //notifyDataSetChanged()
    }
}