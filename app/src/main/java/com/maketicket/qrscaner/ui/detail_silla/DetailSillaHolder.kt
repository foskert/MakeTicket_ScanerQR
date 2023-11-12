package com.maketicket.qrscaner.ui.detail_silla

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.OrderDetailActivity
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R
import com.maketicket.qrscaner.databinding.ItenListDetailSillaBinding
import com.maketicket.qrscaner.ui.model.Silla
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailSillaHolder (view: View): RecyclerView.ViewHolder(view) {
    private val binding= ItenListDetailSillaBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(iten: Silla, activity: Activity?, loading_progessbar: ProgressBar, viewDisableLayout: View){

        binding.textIdSpaceChair.setText("${iten.id_space_chair}")
        binding.textName.setText(" ${iten.name}")
        binding.textColumnRow.setText(" ${iten.column} Fila: ${iten.row}")
        binding.layoutSilla.setOnClickListener {
            viewDisableLayout.visibility = View.VISIBLE
            loading_progessbar.visibility = View.VISIBLE
            getCodeChair(iten.id_space_chair.toString(), activity, it, loading_progessbar, viewDisableLayout)

            /*val intent = Intent(it.context, OrderDetailActivity::class.java)
            intent.putExtra("CODE", "")
            intent.putExtra("CHAIR", iten.id_space_chair)
            intent.putExtra("ORDEN", "")
            it.context.startActivity(intent)*/
        }
    }
    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.makeidsystems.com/makeid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getCodeChair(
        chair: String,
        activity: Activity?,
        view: View,
        loading_progessbar: ProgressBar,
        viewDisableLayout: View,
    ){
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getDetailSilla(
                QRSanerApplication.preference.getKeyValue(), chair)
            val response =call.body()
            activity?.runOnUiThread {
                viewDisableLayout.visibility = View.GONE
                loading_progessbar.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        Log.d("DETALLE_SILLA", response.chair.toString())
                        OrderDetail(response.id_purchase_order.toString(),  view)
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

    private fun OrderDetail(chair: String,  view: View) {
        val intent = Intent(view.context, OrderDetailActivity::class.java)
        intent.putExtra("CODE", chair)
        intent.putExtra("ORDEN", "")
        view.context.startActivity(intent)
    }
}




