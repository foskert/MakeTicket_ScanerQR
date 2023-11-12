package com.maketicket.qrscaner.ui.qr_scaner

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ItenQrScanerBinding
import com.maketicket.qrscaner.ui.model.Ticket

class QRScanerHolder(view:View):RecyclerView.ViewHolder(view) {

     var itenIco: ImageView
    private  var binding_audiencia = ItenQrScanerBinding.bind(view)
    init {
        itenIco= view.findViewById(R.id.iten_ico_detail)
    }
    @SuppressLint("SetTextI18n")
    fun bind(ticket: Ticket){
            binding_audiencia.itenCode.setText(ticket.code)
            binding_audiencia.itenDate.setText(ticket.date)

    }
}