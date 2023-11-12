package com.maketicket.qrscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.maketicket.qrscaner.QRSanerApplication.Companion.preference
import com.maketicket.qrscaner.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var fragmentLoginOne: View
    private lateinit var fragmentLoginTow: View
    private lateinit var fragmentOneBtnNext: Button
    private lateinit var fragmentTowBtnLogin: Button
    private lateinit var fragmentInputEmail: EditText
    private lateinit var fragmentInputPassword: EditText
    //private lateinit var fragmentOneBtnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_login)

        fragmentLoginOne      = findViewById(R.id.fragment_login_one)
        fragmentLoginTow      = findViewById(R.id.fragment_login_tow)
        fragmentOneBtnNext    = findViewById(R.id.fragment_one_btn_next)
        fragmentTowBtnLogin   = findViewById(R.id.fragment_tow_btn_login)
        fragmentInputEmail    = findViewById(R.id.fragment_tow_edt_email)
        fragmentInputPassword = findViewById(R.id.fragment_tow_edt_password)

        fragmentLoginOne.visibility = View.VISIBLE
        fragmentLoginTow.visibility = View.GONE

        fragmentOneBtnNext.setOnClickListener{
            fragmentLoginOne.visibility = View.GONE
            fragmentLoginTow.visibility = View.VISIBLE
        }
        fragmentTowBtnLogin.setOnClickListener{
            val sesion = authUser(fragmentInputEmail.text.toString(), fragmentInputPassword.text.toString())
            if (sesion) {
                val intent = Intent(this, DashboardActivity::class.java).apply {
                    putExtra(AlarmClock.EXTRA_MESSAGE, "hola")
                }
                finish()
                startActivity(intent)
            }else{
                Toast.makeText(this, "Usuario o contrasña incorrecta", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun authUser(email:String, password:String ): Boolean {
        if(email.trim().equals("admin@maketicket.com") && password.equals("123456789")){
            preference.setUserEmail("admin@maketicket.com")
            preference.setUserName("Make Ticket")
            return true
        }else{
            Toast.makeText(this, "Usuario o contraseña incorrecto", Toast.LENGTH_LONG).show()
            return false
        }

    }
}