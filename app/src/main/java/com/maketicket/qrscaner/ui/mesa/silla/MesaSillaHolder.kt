package com.maketicket.qrscaner.ui.mesa.silla

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
import com.maketicket.qrscaner.databinding.ItenListDetailSillaBinding
import com.maketicket.qrscaner.ui.model.MesaOrden
import com.maketicket.qrscaner.ui.model.MesaSilla
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MesaSillaHolder (view:View): RecyclerView.ViewHolder(view) {
        private val binding = ItenListDetailSillaBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(iten: MesaSilla, activity: Activity? ) {
        binding.textIdSpaceChair.setText("${iten.id_chair}")
        binding.textName.setText("Ubicación: ${iten.name_chair}")
        binding.textColumnRow.setText("Estado: ${iten.status} ")

        binding.layoutSilla.setOnClickListener {
            getCodeChair(iten.id_chair, activity, it)
        }
    }
    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.makeidsystems.com/makeid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getCodeChair(chair: Int, activity: Activity?, view: View){
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getMesaOrden(
                QRSanerApplication.preference.getKeyValue(), chair)
            val response =call.body()
            activity?.runOnUiThread {
                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("DETALLE_SILLA", response.toString())
                        if(response.success==true) {
                            OrderDetail(response.data!!, view)
                        }
                    }else{
                        Log.d("DETALLE_SILLA", "Es nulo")
                        ModalOrden(view)
                    }
                }else{
                    Log.d("DETALLE_SILLA", "no definido")
                }
            }
        }
    }

    private fun ModalOrden(view: View) {
        AlertDialog.Builder(view.context).apply {
            setTitle(R.string.alert)
            setMessage("La silla seleccionada no posee orden de compra")
            setNegativeButton("Continuar", null)
        }.show()
    }

    private fun OrderDetail(mesa_orden:MesaOrden,  view: View) {
        val intent = Intent(view.context, OrderDetailActivity::class.java)
        intent.putExtra("CODE", mesa_orden.id_purchase_order.toString())
        intent.putExtra("ORDEN", "")
        view.context.startActivity(intent)
    }
}
