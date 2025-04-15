package com.dinocode.tiendavirtualapp_kotlin.Vendedor.Nav_Fragments_Vendedor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dinocode.tiendavirtualapp_kotlin.Vendedor.ListaClientes.ListaClientesActivity
import com.dinocode.tiendavirtualapp_kotlin.databinding.FragmentMiTiendaVBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.cos

class FragmentMiTiendaV : Fragment() {

    private lateinit var binding : FragmentMiTiendaVBinding
    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMiTiendaVBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        estadosOrden()
        gananciasYperdidas()

        binding.btnVerCliente.setOnClickListener {
            startActivity(Intent(mContext , ListaClientesActivity::class.java))
        }
    }

    private fun gananciasYperdidas() {
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0.0
                var totalPerdida = 0.0

                for (ordenSn in snapshot.children){
                    val estadoOrden = ordenSn.child("estadoOrden").value
                    val costo = ordenSn.child("costo").getValue(String::class.java)

                    if (estadoOrden == "Entregado" && costo !=null){
                        val costoValue = costo.replace("[^\\d.]".toRegex(), "").toDoubleOrNull()
                        if (costoValue != null){
                            total += costoValue
                        }
                    }else if (estadoOrden == "Cancelado" && costo!= null){
                        val costoValue = costo.replace("[^\\d.]".toRegex(), "").toDoubleOrNull()
                        if (costoValue != null){
                            totalPerdida += costoValue
                        }
                    }
                }

                binding.tvGanancias.setText(total.toString().plus(" USD"))
                binding.tvPerdidas.setText(totalPerdida.toString().plus(" USD"))
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun estadosOrden() {
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var contador_1 = 0
                var contador_2 = 0
                var contador_3 = 0
                var contador_4 = 0
                var contador_5 = 0

                for (ordenSn in snapshot.children){
                    val estadoOrden = ordenSn.child("estadoOrden").value

                    if (estadoOrden == "Solicitud recibida"){
                        contador_1++
                        binding.tvEstado1.setText("Solicitudes recibidas: ${contador_1}")
                    }else if (estadoOrden == "Pago Pendiente"){
                        contador_2++
                        binding.tvEstado2.setText("Pagos pendientes: ${contador_2}")
                    }else if (estadoOrden == "En Preparación"){
                        contador_3++
                        binding.tvEstado3.setText("Órdenes en preparación: ${contador_3}")
                    }else if (estadoOrden == "Entregado"){
                        contador_4++
                        binding.tvEstado4.setText("Órdenes entregadas: ${contador_4}")
                    }else if (estadoOrden == "Cancelado"){
                        contador_5++
                        binding.tvEstado5.setText("Órdenes canceladas: ${contador_5}")
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}