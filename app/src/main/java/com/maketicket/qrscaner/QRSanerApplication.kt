package com.maketicket.qrscaner

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.widget.Toast

class QRSanerApplication: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var preference:PreferenceApplication
    }
    override fun onCreate() {
        super.onCreate()
        preference = PreferenceApplication(applicationContext)

    }

}