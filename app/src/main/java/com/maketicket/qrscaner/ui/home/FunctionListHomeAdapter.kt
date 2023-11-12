package com.maketicket.qrscaner.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.response.funciones

class FunctionListHomeAdapter(val listFunction: List<funciones>, val  activity: FragmentActivity?): RecyclerView.Adapter<FunctionListHomeHolter>() {
    private lateinit var  view: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionListHomeHolter {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context

        return FunctionListHomeHolter(layoutInflater.inflate(R.layout.iten_list_function_home, parent, false), activity)
    }
    override fun onBindViewHolder(holder: FunctionListHomeHolter, position: Int) {
        holder.bind(listFunction[position])
    }
    override fun getItemCount(): Int = listFunction.size
}