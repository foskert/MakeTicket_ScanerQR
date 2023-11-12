package com.maketicket.qrscaner.ui.list_event

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.model.EventDetail

class ListEventAdapter(val list: List<EventDetail>, val listEventActivity: ListEventActivity): RecyclerView.Adapter<ListEventHolder>() {
    private lateinit var  view: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEventHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context

        return ListEventHolder(layoutInflater.inflate(R.layout.it_list_event, parent, false))
    }
    override fun onBindViewHolder(holder: ListEventHolder, position: Int) {
        holder.bind(list[position].attributes, position, listEventActivity)

    }
    override fun getItemCount(): Int = list.size


}
