package com.maketicket.qrscaner.ui.detail_mesa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.maketicket.qrscaner.databinding.ActivityDetailMesaBinding

class DetailMesaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailMesaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailMesaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReturnDetailMesa.setOnClickListener{
            finish()
        }
    }
}