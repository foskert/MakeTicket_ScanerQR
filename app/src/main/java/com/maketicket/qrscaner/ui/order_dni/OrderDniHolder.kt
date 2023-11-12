package com.maketicket.qrscaner.ui.order_dni

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.OrderDetailActivity
import com.maketicket.qrscaner.databinding.ItenQrScanerBinding
import com.maketicket.qrscaner.ui.model.OrderDNI

class OrderDniHolder (view: View): RecyclerView.ViewHolder(view) {
    private val binding= ItenQrScanerBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(iten: OrderDNI, activity: Activity?, loading_progessbar: ProgressBar, viewDisableLayout: View){

        binding.itenCode.setText("${iten.id_purchase_order}")
        binding.itenDate.setText("")
        binding.itenIcoDetail.setOnClickListener {
            viewDisableLayout.visibility = View.VISIBLE
            loading_progessbar.visibility = View.VISIBLE

            val intent = Intent(it.context, OrderDetailActivity::class.java)
            intent.putExtra("CODE", iten.id_purchase_order.toString())
            intent.putExtra("ORDEN", "")
            intent.putExtra("SEND", true)
            it.context.startActivity(intent)
        }
    }


}


