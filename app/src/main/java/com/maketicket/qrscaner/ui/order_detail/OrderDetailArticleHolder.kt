package com.maketicket.qrscaner.ui.order_detail

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ItenListOrderDetailArticleBinding
import com.maketicket.qrscaner.ui.response.Articles

class OrderDetailArticleHolder (view: View): RecyclerView.ViewHolder(view) {
    private val binding= ItenListOrderDetailArticleBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(iten: Articles){
        binding.itenCount.setText(""+iten.cantidad)
        binding.itenCount.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
        binding.itenName.setText(iten.name)

    }
}