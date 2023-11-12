package com.maketicket.qrscaner.ui.function

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ItenListFunctionEventBinding
import com.maketicket.qrscaner.ui.model.MySQLiteHelper
import com.maketicket.qrscaner.ui.qr_scaner.QRScanerHolder
import com.maketicket.qrscaner.ui.response.funciones
//import okhttp3.internal.notify

class FunctionDetailHolder(
    private val view: View,
    private val activity: FragmentActivity,

    ): RecyclerView.ViewHolder(view) {
    //lateinit var  checkBox: CheckBox
    private val binding = ItenListFunctionEventBinding.bind(view)
    val checkBox = view.findViewById(R.id.radio_id_article) as CheckBox
    private lateinit var DBHelper:MySQLiteHelper

    @SuppressLint("SetTextI18n")
    fun bind(
        iten: funciones,
    ){
       // checkBox = binding.radioIdArticle
        checkBox .setText(iten.id.toString())
        binding.textArticleModel.setText(iten.funcion)
        //checkBox.isChecked = false
        DBHelper= MySQLiteHelper(activity)
        val funct = DBHelper.ShowFunction(iten.id)
        if (funct!= null) {
            iten.isSelectd = true
            checkBox.isChecked = true
        }else{
            checkBox.isChecked = false
            iten.isSelectd = false
        }

        checkBox.setOnClickListener {
            QRSanerApplication.preference.setIdArticle(iten.id)
            QRSanerApplication.preference.setNameArticle(iten.funcion.toString())
            if(iten.isSelectd==false){
                iten.isSelectd = true
                DBHelper.addFunction(iten.id, "", "")
                Log.d("MODELHELPER ADD HOLDER TRUE", "query = ${it.isSelected}")
            }else{
                iten.isSelectd = false
                DBHelper.functionDelete(iten.id)
                Log.d("MODELHELPER DELETE HOLDER FALSE", "query =${it.isSelected}")
            }
        }
    }
}


