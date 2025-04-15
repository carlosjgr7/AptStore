package com.dinocode.tiendavirtualapp_kotlin.Cliente.Pago

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.dinocode.tiendavirtualapp_kotlin.databinding.ActivityPagoBinding

class PagoActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPagoBinding
    private var init_point = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init_point = intent.getStringExtra("init_point").toString()

        //Toast.makeText(this, init_point, Toast.LENGTH_LONG).show()
        abrirChromeCustomTab(init_point)

        Handler(Looper.getMainLooper()).postDelayed({
            onBackPressedDispatcher
            finish()
        } , 3000)
    }

    private fun abrirChromeCustomTab(init_point : String){
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, Uri.parse(init_point))
    }
}