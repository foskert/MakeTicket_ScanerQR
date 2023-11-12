package com.maketicket.qrscaner.ui.partaker

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.maketicket.qrscaner.databinding.ItenQrScanerPartakerBinding

class PartakertHolder (view: View): RecyclerView.ViewHolder(view) {

    private var binding_partaker=ItenQrScanerPartakerBinding.bind(view)

    init {
    }

    @SuppressLint("SetTextI18n")
    fun bind(code: String, dni: String, email: String, full_name: String, company:String, observation:String) {
            binding_partaker.pkItenCode.setText(code)
            binding_partaker.pkDni.setText("Cédula: $dni")
            binding_partaker.pkEmail.setText("Email: $email")
            binding_partaker.pkCompany.setText("Copañia: $company")
            binding_partaker.pkObservation.setText("Observacíon: $observation")
            if(full_name.equals("")){
                binding_partaker.pkName.setText("Particiánte: No disponible")
            }else {
                binding_partaker.pkName.setText("Particiánte: $full_name")
            }
    }

}