package com.maketicket.qrscaner.ui.menu


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.maketicket.qrscaner.*
import com.maketicket.qrscaner.ui.articles.ArticleActivity
import com.maketicket.qrscaner.ui.detail_mesa.DetailMesaActivity
import com.maketicket.qrscaner.ui.detail_silla.DetailSillaActivity
import com.maketicket.qrscaner.ui.detail_silla.funtion.FuntionActivity
import com.maketicket.qrscaner.ui.espacios_silla.EspaciosSillaActivity
import com.maketicket.qrscaner.ui.list_event.ListEventActivity
import com.maketicket.qrscaner.ui.mesa.zona.MesaZonaActivity
import com.maketicket.qrscaner.ui.model.OrderDNI
import com.maketicket.qrscaner.ui.model.OrderEntered
import com.maketicket.qrscaner.ui.order_dni.OrderDniActivity
import com.maketicket.qrscaner.ui.order_entered.OrderEnteredActivity
import com.maketicket.qrscaner.ui.order_entry.OrderEntryActivity
import kotlinx.android.parcel.Parcelize

//import kotlinx.android.synthetic.main.iten_list_menu_principal.*

@Parcelize
class MyBottomSheetDialogFragment : Parcelable, BottomSheetDialogFragment(), View.OnClickListener {
    private var mListener:ItemClickListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.iten_list_menu_principal, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
       /* mListener = if(context is ItemClickListener ){
            context
        }else{
            throw  RuntimeException(
                con
            )
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        mListener= null
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ConstraintLayout>(R.id.iten_mesa).setOnClickListener{
            Log.d("MENUMODALDIALOG", "mesa" )
            this.startActivity(Intent(activity, MesaZonaActivity::class.java))
        }
        view.findViewById<ConstraintLayout>(R.id.iten_silla).setOnClickListener{
            Log.d("MENUMODALDIALOG", "silla" )
            this.startActivity(Intent(activity, FuntionActivity ::class.java))
        }
        view.findViewById<ConstraintLayout>(R.id.iten_report_icomes).setOnClickListener{
            Log.d("MENUMODALDIALOG", "reporte icomes" )
            this.startActivity(Intent(activity, ReportIcomesActivity::class.java))
        }
        view.findViewById<ConstraintLayout>(R.id.iten_report_ventas).setOnClickListener{
            Log.d("MENUMODALDIALOG", "reporte ventas" )
            this.startActivity(Intent(activity, ReportVentasActivity::class.java))
        }
        view.findViewById<ConstraintLayout>(R.id.iten_report_detail_order).setOnClickListener{
            Log.d("MENUMODALDIALOG", "reporte detalle de ordenes" )
            this.startActivity(Intent(activity, ReportDetailORderActivity::class.java))
        }
        view.findViewById<ConstraintLayout>(R.id.iten_order_entry).setOnClickListener {
            Log.d("MENUMODALDIALOG", "lista de ordenes de entrada" )
            this.startActivity(Intent(activity, OrderEntryActivity::class.java))
        }
        view.findViewById<ConstraintLayout>(R.id.iten_order_dni).setOnClickListener {
            Log.d("MENUMODALDIALOG", "lista de ordenes por usuario" )
            this.startActivity(Intent(activity, OrderDniActivity::class.java))
        }

        view.findViewById<ConstraintLayout>(R.id.iten_order_entered).setOnClickListener {
            Log.d("MENUMODALDIALOG", "Distribucion de sillas" )
            this.startActivity(Intent(activity, OrderEnteredActivity::class.java))
        }
        view.findViewById<ConstraintLayout>(R.id.iten_menu_list_event).setOnClickListener {
            Log.d("MENUMODALDIALOG", "Distribucion de sillas" )

            this.startActivity(Intent(activity, ListEventActivity::class.java))
        }
        view.findViewById<ConstraintLayout>(R.id.iten_espacios_silla).setOnClickListener {
            Log.d("MENUMODALDIALOG", "Distribucion de sillas" )
            val intent = Intent(activity, ArticleActivity::class.java)
            intent.putExtra("TYPE", "silla")
            startActivity(intent)
            //this.startActivity(Intent(activity, ArticleActivity::class.java))
        }
        view.findViewById<ConstraintLayout>(R.id.iten_espacios_mesa).setOnClickListener {
            Log.d("MENUMODALDIALOG", "Distribucion de mesa" )
            val intent = Intent(activity, ArticleActivity::class.java)
            intent.putExtra("TYPE", "mesa")
            startActivity(intent)
           // this.startActivity(Intent(activity, ListEventActivity::class.java))
        }

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        /*listViewOptions.adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOf<String>(
                "Detalle de sillas",
                "Detalle de mesas",
            )
        )*/
    }

    override fun onClick(v: View?) {
        mListener!!.onItemClick("hola")
        Toast.makeText(v!!.context, "Escaneo cancelado", Toast.LENGTH_LONG).show()

    }
}