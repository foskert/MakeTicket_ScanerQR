package com.maketicket.qrscaner.ui.qr_scaner

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.OrderDetailActivity
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.model.Ticket

class QRScanerAdapter(val ticket: List<Ticket>,): RecyclerView.Adapter<QRScanerHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QRScanerHolder {
        val layoutInflater:LayoutInflater= LayoutInflater.from(parent.context)
        view= parent.context
        return QRScanerHolder(layoutInflater.inflate(R.layout.iten_qr_scaner, parent, false))

    }

    override fun onBindViewHolder(holder: QRScanerHolder, position: Int) {
        //val item:String = code[position]

            holder.bind(ticket[position])
            holder.itenIco.setOnClickListener {
                val intent = Intent(view, OrderDetailActivity::class.java)
                intent.putExtra("CODE", ticket[position].code)
                intent.putExtra("ORDEN", "")
                view.startActivity(intent)
            }

    }

    override fun getItemCount(): Int = ticket.size
}


