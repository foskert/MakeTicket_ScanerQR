package com.maketicket.qrscaner.ui.order

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.zxing.integration.android.IntentIntegrator
import com.maketicket.qrscaner.R


class OrderCodeQRViewModal: DialogFragment(){
    private lateinit var imageCanel: ImageView
    private lateinit var editCodeQR: EditText
    private lateinit var btnVerificate: Button
    private lateinit var btnScanQR: Button
    private var code: String? = null

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.modal_code_qr_orden_detail, container, false)
        imageCanel = rootView.findViewById(R.id.img_cancel_order_detail_code_qr)

        imageCanel.setOnClickListener {
            dialog!!.dismiss()
        }
        editCodeQR = rootView.findViewById(R.id.edit_dateil_orden_qr)
        btnVerificate = rootView.findViewById(R.id.btn_verification_code_qr)
        btnVerificate.setOnClickListener {
            if (!editCodeQR.text.equals("")) {
                //parentFragmentManager.setFragmentResultListener("10", viewLifecycleOwner,  )
                setFragmentResult("scannedCode", bundleOf("codeqrmodal" to editCodeQR.text.toString()))
                dismiss()
                //sendCode(editCodeQR.text.toString())
            } else {
                showResponse("Debe ingresar un código")
            }
        }
        btnScanQR = rootView.findViewById(R.id.btn_scan_qr_detail_orden)
        btnScanQR.setOnClickListener {
            CodeQR(rootView)
            dialog!!.dismiss()
        }
        return rootView
    }

    private fun sendCode(toString: String) {

    }

    private fun CodeQR(rootView: View) {
        IntentIntegrator.forSupportFragment(this).addExtra("CODE_F", 5)
            .setPrompt("ESCANEO DE CÓDIGO QR")
            .setTorchEnabled(false)
            .setBeepEnabled(true)
            .initiateScan()

    }

    private fun showResponse(body: String?) {
        Toast.makeText(context, "$body", Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance(
        ) = OrderCodeQRViewModal().apply {
            arguments = bundleOf(KEY_REQUEST to "key_request")
        }

        const val KEY_CLICK = "click"
        private const val KEY_REQUEST = "key_request"
    }

   
}


