package com.maketicket.qrscaner.ui.order_detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.response.Articles

class OrderDetailArticleAdapter(val listArticle:List<Articles>): RecyclerView.Adapter<OrderDetailArticleHolder>() {
    private lateinit var  view: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailArticleHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context
        return OrderDetailArticleHolder(layoutInflater.inflate(R.layout.iten_list_order_detail_article, parent, false))

    }
    override fun onBindViewHolder(holder: OrderDetailArticleHolder, position: Int) {
        holder.bind(listArticle[position])
    }
    override fun getItemCount(): Int = listArticle.size
}
