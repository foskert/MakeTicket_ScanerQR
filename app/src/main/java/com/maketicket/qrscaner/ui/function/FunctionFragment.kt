package com.maketicket.qrscaner.ui.function

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.maketicket.qrscaner.R
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maketicket.qrscaner.OrderEntryService
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.ui.response.funciones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FunctionFragment: DialogFragment(), DialogInterface {


    private lateinit var adpater: FunctionDetailAdapter
    private val dogFunction = mutableListOf<funciones>()
    private lateinit var listFunction: RecyclerView
    private lateinit var textEventTitle: TextView
    private lateinit var imageLoader: ImageView
    lateinit var imageCanel: ImageView
    private lateinit var auxDogFunction: MutableList<funciones>
    var ban= false

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView:View = inflater.inflate(R.layout.modal_list_function_event, container, false)
        listFunction= rootView.findViewById(R.id.list_function_modal)
        textEventTitle = rootView.findViewById(R.id.text_event_title)
        textEventTitle.setText(QRSanerApplication.preference.getNameEvent())
        imageLoader = rootView.findViewById(R.id.loading)
        Glide.with(activity!!).load(R.drawable.load).into(imageLoader)
        imageCanel =  rootView.findViewById(R.id.img_cancel)
        imageCanel.setOnClickListener {
            dialog!!.dismiss()
        }
        adpater= FunctionDetailAdapter(dogFunction, activity!!)

        listFunction.layoutManager= LinearLayoutManager(activity)
        listFunction.adapter = adpater
        listEvent()
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    private fun listEvent() {
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRestEngine().create(OrderEntryService:: class.java).getOrderAdmitted( QRSanerApplication.preference.getIdEvent(), QRSanerApplication.preference.getKeyValue())
            val response =call.body()
            activity?.runOnUiThread {
                imageLoader.visibility = View.GONE
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success!!){
                            iniRecycleView(response.funciones)
                            Log.d("LIST_FUNCTION", "respuesta ${response.funciones}")
                        }else{
                            showResponse("No hay datos disponibles para el evento")
                            Log.d("LIST_FUNCTION", "respuesta success false")
                        }
                    }else{
                        showResponse("Error de respuesta")
                        Log.d("LIST_FUNCTION", "respuesta error de respuesta")
                    }
                    // showResponse(dogCode.size.toString())
                }else{
                    showResponse("Error de conexión ")
                    Log.d("LIST_FUNCTION", "respuesta error de conexion")
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun iniRecycleView(funciones: ArrayList<funciones>?) {
        dogFunction.clear()
        if (funciones != null) {
            dogFunction.addAll(funciones)
        }
      /*  dogFunction.forEach {
            if(QRSanerApplication.preference.getIdArticle()== it.id){
                it.isSelectd = true
            }
        }
        auxDogFunction = dogFunction*/
        Log.d("TAG_F", "funciones $funciones")
        adpater.notifyDataSetChanged()


       /* dogFunction.clear()
        dogFunction.addAll(funciones!!)
        adpater= FunctionDetailAdapter(dogFunction)
        listFunction.layoutManager= LinearLayoutManager(activity)
        listFunction.adapter = adpater*/
    }

    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.makeidsystems.com/makeid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun showResponse(body: String?) {
        Toast.makeText(context, "$body", Toast.LENGTH_LONG).show()
    }


    override fun cancel() {
        TODO("Not yet implemented")
    }

}