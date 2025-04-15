package com.dinocode.tiendavirtualapp_kotlin.Vendedor.ListaClientes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dinocode.tiendavirtualapp_kotlin.Adaptadores.AdaptadorCliente
import com.dinocode.tiendavirtualapp_kotlin.Modelos.ModeloUsuario
import com.dinocode.tiendavirtualapp_kotlin.R
import com.dinocode.tiendavirtualapp_kotlin.databinding.ActivityListaClientesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaClientesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityListaClientesBinding

    private lateinit var clientesArrayList : ArrayList<ModeloUsuario>
    private lateinit var adaptadorCliente : AdaptadorCliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        listarClientes()

    }

    private fun listarClientes() {
         clientesArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.orderByChild("tipoUsuario").equalTo("cliente")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    clientesArrayList.clear()

                    for (ds in snapshot.children){
                        val modeloCliente = ds.getValue(ModeloUsuario::class.java)
                        clientesArrayList.add(modeloCliente!!)
                    }

                    adaptadorCliente = AdaptadorCliente(this@ListaClientesActivity, clientesArrayList)
                    binding.clienteRV.adapter = adaptadorCliente
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })









    }
}