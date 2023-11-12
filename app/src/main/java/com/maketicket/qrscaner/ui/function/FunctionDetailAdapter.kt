package com.maketicket.qrscaner.ui.function

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.ui.response.funciones

class FunctionDetailAdapter(val listFunction: List<funciones>, val activity: FragmentActivity): RecyclerView.Adapter<FunctionDetailHolder>() {
    private lateinit var  view: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionDetailHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view= parent.context

        return FunctionDetailHolder(layoutInflater.inflate(R.layout.iten_list_function_event, parent, false), activity)
    }
    override fun onBindViewHolder(holder: FunctionDetailHolder, position: Int) {
        holder.bind(listFunction[position])
        /*holder.checkBox.setOnClickListener {
            QRSanerApplication.preference.setIdArticle(listFunction[position].id)
            QRSanerApplication.preference.setNameArticle("${listFunction[position].funcion.toString()}.")
           /* listFunction.forEach {
                it.isSelectd = false
            }
            listFunction.get(position).isSelectd = true*/
            notifyDataSetChanged()
        }*/

    }
    override fun getItemCount(): Int = listFunction.size


}
