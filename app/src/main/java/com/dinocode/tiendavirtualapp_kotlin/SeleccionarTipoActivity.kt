package com.dinocode.tiendavirtualapp_kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dinocode.tiendavirtualapp_kotlin.Cliente.LoginClienteActivity
import com.dinocode.tiendavirtualapp_kotlin.Vendedor.LoginVendedorActivity
import com.dinocode.tiendavirtualapp_kotlin.databinding.ActivitySeleccionarTipoBinding

class SeleccionarTipoActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySeleccionarTipoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeleccionarTipoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*Iniciar sesión con vendedor*/
        binding.tipoVendedor.setOnClickListener {
            startActivity(Intent(this@SeleccionarTipoActivity, LoginVendedorActivity::class.java))
        }

        /*Iniciar sesión con cliente*/
        binding.tipoCliente.setOnClickListener {
            startActivity(Intent(this@SeleccionarTipoActivity, LoginClienteActivity::class.java))
        }
    }
}