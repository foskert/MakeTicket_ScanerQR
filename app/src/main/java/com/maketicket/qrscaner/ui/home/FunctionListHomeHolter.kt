package com.maketicket.qrscaner.ui.home

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ItenListFunctionHomeBinding
import com.maketicket.qrscaner.ui.model.MySQLiteHelper
import com.maketicket.qrscaner.ui.response.funciones

class FunctionListHomeHolter(
    private val view: View,
    private val activity: FragmentActivity?,

    ): RecyclerView.ViewHolder(view) {
    lateinit var  radioButton: TextView
    private val binding = ItenListFunctionHomeBinding.bind(view)
    private lateinit var DBHelper:MySQLiteHelper

    @SuppressLint("SetTextI18n")
    fun bind(
        iten: funciones
    ){
        radioButton = binding.textCodeFunction
        radioButton .setText(iten.id.toString())
        binding.textHomeFunction.setText(iten.funcion)
        //if(QRSanerApplication.preference.getIdArticle()== iten[position].id){
            binding.textHomeFunction.setTextColor(ContextCompat.getColor(view.context, R.color.p1))
        //}
        DBHelper= MySQLiteHelper(activity!!)
        val funct = DBHelper.ShowFunction(iten.id)
        if (funct!= null) {
            binding.textHomeFunction.setTextColor(ContextCompat.getColor(view.context, R.color.p1))

        }else{
            binding.textHomeFunction.setTextColor(ContextCompat.getColor(view.context, R.color.gris_medio_alto))
        }
    }
}

