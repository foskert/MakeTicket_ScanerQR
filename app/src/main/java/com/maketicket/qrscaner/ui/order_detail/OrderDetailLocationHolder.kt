package com.maketicket.qrscaner.ui.order_detail

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.databinding.ItenListOrderDetailLocationBinding
import com.maketicket.qrscaner.ui.response.Location

class OrderDetailLocationHolder (view: View): RecyclerView.ViewHolder(view) {
    private val binding= ItenListOrderDetailLocationBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(iten: Location){
        binding.itenName.setText(iten.name)

    }
}