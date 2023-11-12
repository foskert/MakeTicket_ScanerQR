package com.maketicket.qrscaner.ui.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.maketicket.qrscaner.QRSanerApplication
import com.maketicket.qrscaner.R

class TypeFragment: DialogFragment()  {
    private lateinit var imageCanel: ImageView
    private lateinit var group: RadioGroup
    private lateinit var audiencia:RadioButton
    private lateinit var participacion:RadioButton

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView:View = inflater.inflate(R.layout.modal_type_event, container, false)
        imageCanel = rootView.findViewById(R.id.img_cancel_type)
        imageCanel.setOnClickListener {
            dialog!!.dismiss()
        }
        audiencia = rootView.findViewById(R.id.audiencia)
        participacion = rootView.findViewById(R.id.participacion)
        if(QRSanerApplication.preference.getTypeEvent().equals("orden")){
            audiencia.isChecked = true
        }else{
            participacion.isChecked = true
        }
        audiencia.setOnClickListener {
            QRSanerApplication.preference.setTypeEvent("orden")
        }
        participacion.setOnClickListener {
            QRSanerApplication.preference.setTypeEvent("participación")
        }
        return rootView
    }
}