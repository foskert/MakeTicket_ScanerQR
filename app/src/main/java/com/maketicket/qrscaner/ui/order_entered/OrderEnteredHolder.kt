package com.maketicket.qrscaner.ui.order_entered

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.OrderDetailActivity
import com.maketicket.qrscaner.databinding.ItOrderEnteredBinding
import com.maketicket.qrscaner.databinding.ItenQrScanerBinding
import com.maketicket.qrscaner.ui.model.OrderEntered


class OrderEnteredHolder (view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItenQrScanerBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(
        iten: List<OrderEntered>,
        position: Int
    ){
        if(iten[position].id_purchase_order>0) {
            binding.itenCode.setText(iten[position].id_purchase_order.toString())
        }
        binding.itenDate.setText(iten[position].status)
        binding.itenIcoDetail.setOnClickListener {
            val intent = Intent(it.context, OrderDetailActivity::class.java)
            //intent.putExtra("CODE", iten.id_purchase_order.toString())
            intent.putExtra("ORDEN", "")
            intent.putExtra("SEND", true)
            it.context.startActivity(intent)
        }
    }
}