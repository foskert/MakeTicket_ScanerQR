package com.maketicket.qrscaner.ui.list_event

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.databinding.ItListEventBinding
import com.maketicket.qrscaner.ui.model.Attribute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListEventHolder (
    private val view: View,


    ): RecyclerView.ViewHolder(view) {

    private val binding = ItListEventBinding.bind(view)
    private lateinit var activity: ListEventActivity

    @SuppressLint("SetTextI18n")
    fun bind(
        iten: Attribute,
        position: Int,
         listEventActivity: ListEventActivity
    ){
        activity = listEventActivity
        binding.idEvent.setText(iten.id.toString())
        binding.nameEvent.setText(iten.name.toString())
        binding.category.setText(iten.category.toString())
        binding.dateIni.setText("Fecha inició: ${iten.start_at}")
        binding.dateEnd.setText("Fecha fin: ${iten.end_at}")
        binding.position.setText("Posición: ${iten.position}")
        binding.icoBanner.setOnClickListener {
            val dialog= ArgonModal(iten, 1, activity, "EVENTO ARGOR")
            dialog.show(activity?.supportFragmentManager!!, "Function")
        }
        binding.icoEvent.setOnClickListener {
            val dialog= ArgonModal(iten, 2, activity, "ARTE ARGOR")
            dialog.show(activity?.supportFragmentManager!!, "Function")
        }

    }




}


