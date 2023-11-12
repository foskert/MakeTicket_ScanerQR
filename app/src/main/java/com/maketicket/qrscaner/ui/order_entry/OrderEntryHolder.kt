package com.maketicket.qrscaner.ui.order_entry

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.OrderDetailActivity
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ItenQrScanerBinding
import com.maketicket.qrscaner.ui.model.MesaOrden
import com.maketicket.qrscaner.ui.model.OrderEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OrderEntryHolder (view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItenQrScanerBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(iten: OrderEntry, activity: Activity? ) {
        binding.itenCode.setText("${iten.id_purchase_order}")
        binding.itenDate.setText(" ${iten.status} ")

        binding.itenIcoDetail.setOnClickListener {
            val intent = Intent(it.context, OrderDetailActivity::class.java)
            intent.putExtra("CODE", iten.id_purchase_order.toString())
            intent.putExtra("ORDEN", "")
            it.context.startActivity(intent)
        }
    }
}
