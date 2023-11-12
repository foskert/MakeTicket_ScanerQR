package com.maketicket.qrscaner.ui.order_detail

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ItenListOrderDetailCodeQrBinding
import com.maketicket.qrscaner.ui.response.PurchaseOrderTicket

class OrderDetailCodeQRHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding= ItenListOrderDetailCodeQrBinding.bind(view)
    var id_purchase_order:Int = 0
    var id_purchase_ticket:Int = 0
    val itenCodeQr = view.findViewById(R.id.iten_code_qr)  as TextView
    val itenStatus = view.findViewById(R.id.iten_status)   as TextView
    val checkEvento = view.findViewById(R.id.check_evento) as CheckBox

    @SuppressLint("SetTextI18n")
    fun bind(iten: PurchaseOrderTicket){
        itenCodeQr.setText(iten.code)
        itenStatus.setText(iten.status)
        if(iten.status.equals("Fue al evento")){
            itenStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
            iten.check = true
            binding.checkEvento.isChecked = true
            checkEvento.isEnabled = false
        }else if(iten.check && !iten.status.equals("Fue al evento")){
            iten.check = true
            binding.checkEvento.isChecked = true
            checkEvento.isEnabled = true
            itenStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.gris_medio_alto))
        }else{
            itenStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.gris_medio_alto))
            iten.check = false 
            binding.checkEvento.isChecked = false
            checkEvento.isEnabled = true


        }
        id_purchase_order  = iten.id_purchase_order
        id_purchase_ticket = iten.id_purchase_ticket
        checkEvento.setOnClickListener {
            if(iten.check){
                iten.check = false
                binding.checkEvento.isChecked = false
            }else{
                iten.check = true
                binding.checkEvento.isChecked = true
            }
        }

    }


}