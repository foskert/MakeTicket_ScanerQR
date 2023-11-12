package com.maketicket.qrscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(QRSanerApplication.preference.getUserName().isNotEmpty()){
            startActivity(Intent(this, DashboardActivity::class.java))
        }else{
            startActivity(Intent(this, SplashActivity::class.java))
        }
    }
    /*fun isSesion(){
       val intent = Intent(this, SplashActivity::class.java).apply {
           putExtra(AlarmClock.EXTRA_MESSAGE, "hola")
       }
       startActivity(intent)
   }*/
    override fun onRestart() {
        super.onRestart()
        finish()
    }
}