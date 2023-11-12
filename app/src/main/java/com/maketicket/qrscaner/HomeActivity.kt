package com.maketicket.qrscaner

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.maketicket.qrscaner.databinding.ActivityHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarHome.textToolbar)

        binding.appBarHome.fab.setOnClickListener {
            setQRCode()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setQRCode() {
        IntentIntegrator(this)
            .setPrompt("ESCANEO DE CÓDIGO QR")
            .setTorchEnabled(false)
            .setBeepEnabled(true)
            .initiateScan()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null){
            if(result.contents !== null){
                getCodeValue( result.contents)
            }else{
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun getCodeValue(code:String){
        CoroutineScope(Dispatchers.IO).launch{
            // Toast.makeText(context, "code: $code", Toast.LENGTH_LONG).show()
            val retro = RestEngine.getRestEngine()
            val service = retro.create(OrderEntryService:: class.java)
            val call =  service.getOrdercode(code)
            runOnUiThread {
                showResponse(call.body().toString())
                /*if(call.isNotEmpty()){
                    showResponse(call..toString())
                }else{
                    showError("Error de conexión ")

                }*/
            }
        }
    }

    private fun showError(errorBody: String?) {
        Toast.makeText(this, "error: $errorBody", Toast.LENGTH_LONG).show()
    }
    private fun showResponse(body: String?) {
        Toast.makeText(this, "status: $body", Toast.LENGTH_LONG).show()
    }
}