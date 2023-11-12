package com.maketicket.qrscaner.ui.user

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maketicket.qrscaner.*
import com.maketicket.qrscaner.databinding.FragmentUserBinding
import com.maketicket.qrscaner.ui.function.FunctionFragment
import com.maketicket.qrscaner.ui.order.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val orderViewModel =
            ViewModelProvider(this).get(UserViewModel::class.java)

        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

      //  val textView: TextView = binding.textUser
        orderViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }
        _binding!!.userTextEmail.setText(QRSanerApplication.preference.getUserMail().toString())
        _binding!!.userTextName.setText(QRSanerApplication.preference.getUserName().toString())
        _binding!!.textVersionApp.setText("v-${BuildConfig.VERSION_NAME.toString()}")
        if(!QRSanerApplication.preference.getKeyValue().equals("")){
            _binding!!.userTextToken.setText("Key verificada")
            _binding!!.userTextToken.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }else{
            _binding!!.userTextToken.setText("Key no verificada")
            _binding!!.userTextToken.setTextColor(ContextCompat.getColor(requireContext(), R.color.p1))
        }
        if(QRSanerApplication.preference.getIdEvent()>0){
            _binding!!.userTextEvent.setText("Evento verificado")
            _binding!!.userTextEvent.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }else{
            _binding!!.userTextEvent.setText("Evento no verificado")
            _binding!!.userTextEvent.setTextColor(ContextCompat.getColor(requireContext(), R.color.p1))
        }
        if(QRSanerApplication.preference.getIdArticle()>0){
            _binding!!.userTextArticle.setText("Función verificada")
            _binding!!.userTextArticle.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }else{
            _binding!!.userTextArticle.setText("Función no verificado")
            _binding!!.userTextArticle.setTextColor(ContextCompat.getColor(requireContext(), R.color.p1))
        }
        if(QRSanerApplication.preference.getTypeEvent().equals("")){
            _binding!!.userTextTypeFunction.setText("Tipo de evento no seleccionada")
            _binding!!.userTextTypeFunction.setTextColor(ContextCompat.getColor(requireContext(), R.color.p1))
        }else{
            val type =QRSanerApplication.preference.getTypeEvent()
            _binding!!.userTextTypeFunction.text = "Evento por $type"
            _binding!!.userTextTypeFunction.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

        }


        _binding!!.configInfoKey.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle(R.string.alert)
                setMessage(R.string.info_key)
                setNegativeButton("Continuar", null)
            }.show()
        }
        _binding!!.configInfoIdEvent.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle(R.string.alert)
                setMessage(R.string.info_id_event)
                setNegativeButton("Continuar", null)
            }.show()
        }
        _binding!!.configInfoIdArticle.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle(R.string.alert)
                setMessage(R.string.info_article)
                setNegativeButton("Continuar", null)
            }.show()
        }
        _binding!!.configInfoTypeEvent.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle(R.string.alert)
                setMessage(R.string.info_type_event)
                setNegativeButton("Continuar", null)
            }.show()
        }
        _binding!!.deleteImgKey.setOnClickListener {
            if(!QRSanerApplication.preference.getKeyValue().equals("")){
                AlertDialog.Builder(context).apply {
                    setTitle(R.string.info)
                    setMessage(R.string.delete_token)
                    setNegativeButton("No", null)
                    setPositiveButton("Si") { _: DialogInterface, _: Int ->
                        QRSanerApplication.preference.setKeyValue("")
                        _binding!!.userTextToken.setText("Key verificada")
                        _binding!!.userTextToken.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    }
                }.show()
            }else{
                AlertDialog.Builder(context).apply {
                    setTitle(R.string.alert)
                    setMessage(R.string.credencial_token)
                    setNegativeButton("Continuar", null)
                }.show()
            }
        }
        _binding!!.deleteImgIdEvent.setOnClickListener {
            if(QRSanerApplication.preference.getIdEvent()>0 ){
                AlertDialog.Builder(context).apply {
                    setTitle(R.string.info)
                    setMessage(R.string.delete_event)
                    setNegativeButton("No", null)
                    setPositiveButton("Si") { _: DialogInterface, _: Int ->
                        QRSanerApplication.preference.setIdEvent(0)
                        QRSanerApplication.preference.setNameEvent("")
                        QRSanerApplication.preference.setIdArticle(0)
                        QRSanerApplication.preference.setNameArticle("")
                        _binding!!.userTextEvent.setText("Evento no verificado")
                        _binding!!.userTextEvent.setTextColor(ContextCompat.getColor(requireContext(), R.color.p1))
                        _binding!!.userTextArticle.setText("Función no verificado")
                        _binding!!.userTextArticle.setTextColor(ContextCompat.getColor(requireContext(), R.color.p1))
                    }
                }.show()
            }else{
                AlertDialog.Builder(context).apply {
                    setTitle(R.string.alert)
                    setMessage(R.string.credencial_event)
                    setNegativeButton("Continuar", null)
                }.show()
            }
        }
        _binding!!.deleteImgArticle.setOnClickListener {
            if(QRSanerApplication.preference.getIdArticle() > 0){
                AlertDialog.Builder(context).apply {
                    setTitle(R.string.info)
                    setMessage(R.string.delete_article)
                    setNegativeButton("No", null)
                    setPositiveButton("Si") { _: DialogInterface, _: Int ->
                        QRSanerApplication.preference.setIdArticle(0)
                        QRSanerApplication.preference.setNameArticle("")
                        _binding!!.userTextArticle.setText("Función no verificado")
                        _binding!!.userTextArticle.setTextColor(ContextCompat.getColor(requireContext(), R.color.p1))
                    }
                }.show()
            }else{
                AlertDialog.Builder(context).apply {
                    setTitle(R.string.alert)
                    setMessage(R.string.credencial_article)
                    setNegativeButton("Continuar", null)
                }.show()
            }
        }
        _binding!!.configImgKey.setOnClickListener {
            val intent = Intent(context, KeyActivity::class.java).apply {
                putExtra("KEY", 1)
            }
            startActivity(intent)
        }
        _binding!!.configImgIdEvent.setOnClickListener{
            val intent = Intent(context, KeyActivity::class.java).apply {
                putExtra("KEY", 2)
            }
            startActivity(intent)
        }
        _binding!!.configImgIdArticle.setOnClickListener {
            var dialog= FunctionFragment()
            dialog.show(activity?.supportFragmentManager!!, "Function")
        }
        _binding!!.configImgTypeFunction.setOnClickListener {
            var dialog= TypeFragment()
            dialog.show(activity?.supportFragmentManager!!, "Function")
        }
        return root
    }

    /* por eliminar
    private fun listEvent() {
        CoroutineScope(Dispatchers.IO).launch{
            val id_article:String
            if(QRSanerApplication.preference.getIdArticle()>0){
                id_article = QRSanerApplication.preference.getIdArticle().toString()
            }else{
                id_article = ""
            }
            val call = getRestEngine().create(OrderEntryService:: class.java).getOrderAdmitted( QRSanerApplication.preference.getIdEvent(), QRSanerApplication.preference.getKeyValue())
            val response =call.body()
            activity?.runOnUiThread {
                if(call.isSuccessful) {
                    if (response != null) {
                        if (response.success){

                        }else{
                            showResponse("No hay datos disponibles para el evento")
                        }
                    }else{
                        showResponse("Error de respuesta")
                    }
                    // showResponse(dogCode.size.toString())
                }else{
                    showResponse("Error de conexión ")
                }
            }
        }
    }
    private fun addList(){
        AlertDialog.Builder(context).apply {
            setTitle(R.string.alert)
            setMessage(R.string.info_id_event)
            setNegativeButton("Continuar", null)
        }.show()
    }*/
    private fun getRestEngine(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.makeidsystems.com/makeid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun showResponse(body: String?) {
        Toast.makeText(context, "$body", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}