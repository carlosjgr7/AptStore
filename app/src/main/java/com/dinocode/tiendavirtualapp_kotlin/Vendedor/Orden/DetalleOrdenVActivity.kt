package com.dinocode.tiendavirtualapp_kotlin.Vendedor.Orden

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dinocode.tiendavirtualapp_kotlin.Adaptadores.AdaptadorProductoOrden
import com.dinocode.tiendavirtualapp_kotlin.Constantes
import com.dinocode.tiendavirtualapp_kotlin.Modelos.ModeloProductoOrden
import com.dinocode.tiendavirtualapp_kotlin.R
import com.dinocode.tiendavirtualapp_kotlin.databinding.ActivityDetalleOrdenVactivityBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class DetalleOrdenVActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleOrdenVactivityBinding

    private var idOrden = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var productosArrayList : ArrayList<ModeloProductoOrden>
    private lateinit var productoOrdenAdaptador : AdaptadorProductoOrden

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleOrdenVactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        idOrden = intent.getStringExtra("idOrden") ?: ""

        datosOrden()
        productosOrden()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.verInfoCliente.setOnClickListener {
            infoCliente(ordenadoPor)
        }

        binding.IbActualizarOrden.setOnClickListener {
            estadoOrdenMenu()
        }

    }

    var telefono = ""
    private fun infoCliente(uidCliente: String) {
        val ivPerfil : ImageView
        val tvNombresC : TextView
        val tvDniC : TextView
        val tvTelC : TextView
        val tvDireccionC : TextView
        val btnLlamar : MaterialButton
        val btnSms : MaterialButton
        val ibCerrar : ImageButton

        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_info_cliente)

        ivPerfil = dialog.findViewById(R.id.ivPerfil)
        tvNombresC = dialog.findViewById(R.id.tvNombresC)
        tvDniC = dialog.findViewById(R.id.tvDniC)
        tvTelC = dialog.findViewById(R.id.tvTelC)
        tvDireccionC = dialog.findViewById(R.id.tvDireccionC)
        btnLlamar = dialog.findViewById(R.id.btnLlamar)
        btnSms = dialog.findViewById(R.id.btnSms)
        ibCerrar = dialog.findViewById(R.id.ibCerrar)

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios").child(uidCliente)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val imagen = "${snapshot.child("imagen").value}"
                val nombres = "${snapshot.child("nombres").value}"
                val dni = "${snapshot.child("dni").value}"
                telefono = "${snapshot.child("telefono").value}"
                val direccion = "${snapshot.child("direccion").value}"

                tvNombresC.text = nombres
                tvDniC.text = dni
                tvTelC.text = telefono
                tvDireccionC.text = direccion

                try {

                    Glide.with(this@DetalleOrdenVActivity)
                        .load(imagen)
                        .placeholder(R.drawable.img_perfil)
                        .into(ivPerfil)

                }catch (e : Exception){

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        btnLlamar.setOnClickListener {
            if (telefono.isNotEmpty()){
                if (ContextCompat.checkSelfPermission(applicationContext,
                    android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    /*El permiso de teléfono está activado o concedido*/
                    llamarCliente(telefono)
                }else{
                    /*Cuadro de diálogo para conceder o no el permiso*/
                    permisoLlamar.launch(Manifest.permission.CALL_PHONE)
                }

            }else{
                Toast.makeText(applicationContext, "El cliente no registró su número tel.",Toast.LENGTH_SHORT).show()
            }
        }

        btnSms.setOnClickListener {
            if (telefono.isNotEmpty()){
                if (ContextCompat.checkSelfPermission(applicationContext,
                    android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    /*Si el permiso está concedido*/
                    smsCliente(telefono)
                }else{
                    /*Si el permiso no está concedido*/
                    permisoSms.launch(Manifest.permission.SEND_SMS)
                }
            }else{
                Toast.makeText(applicationContext, "El cliente no registró su número tel.",Toast.LENGTH_SHORT).show()
            }
        }

        ibCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)

    }

    private fun llamarCliente(telefono : String){
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData(Uri.parse("tel:${telefono}"))
        startActivity(intent)
    }

    private val permisoLlamar =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){concedido->
            if (concedido){
                llamarCliente(telefono)
            }else{
                Toast.makeText(applicationContext, "El permiso de teléfono está desactivada",Toast.LENGTH_SHORT).show()

            }

        }

    private fun smsCliente(telefono : String){
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.setData(Uri.parse("smsto:$telefono"))
        intent.putExtra("sms_body","Estimado(a) cliente , le escribimos de la tienda xyz ...")
        startActivity(intent)
    }

    private val permisoSms =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){concedido->
            if (concedido){
                smsCliente(telefono)
            }else{
                Toast.makeText(applicationContext, "El permiso de mensajería SMS está desactivada",Toast.LENGTH_SHORT).show()

            }

        }

    private fun estadoOrdenMenu() {
        val popupMenu = PopupMenu(this, binding.IbActualizarOrden)

        popupMenu.menu.add(Menu.NONE , 0 , 0 , "Orden entregada")
        popupMenu.menu.add(Menu.NONE, 1 , 1 , "Orden cancelada")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item->
            val itemId = item.itemId

            if (itemId == 0){
                //Marcar la orden como entregada
                actualizarEstado("Entregado")
            }else if (itemId == 1){
                //Marcar la orden como cancelada
                actualizarEstado("Cancelado")
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun actualizarEstado( estado : String) {
        val hashMap = HashMap<String , Any> ()
        hashMap["estadoOrden"] = estado

        val ref = FirebaseDatabase.getInstance().getReference("Ordenes").child(idOrden)
        ref.updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this , "El estado de la orden ha pasado a: ${estado}",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(this , "Ha ocurrido un error: ${e.message}",Toast.LENGTH_SHORT).show()
            }

    }

    private fun productosOrden(){
        productosArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes").child(idOrden).child("Productos")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productosArrayList.clear()
                for (ds in snapshot.children){
                    val modeloProductoOrden = ds.getValue(ModeloProductoOrden::class.java)
                    productosArrayList.add(modeloProductoOrden!!)
                }

                productoOrdenAdaptador = AdaptadorProductoOrden(this@DetalleOrdenVActivity, productosArrayList)
                binding.ordenesRv.adapter = productoOrdenAdaptador

                binding.cantidadOrdenD.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun direccionCliente(uidCliente : String){
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidCliente)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val direccion = "${snapshot.child("direccion").value}"


                    if (direccion.isNotEmpty()){
                        //Si el usuario registró su ubicación
                        binding.direccionOrdenD.text = direccion
                    }else{
                        //Si el usuario no registró su ubicación
                        binding.direccionOrdenD.text = "El cliente no registró su dirección."

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    var costo = ""
    var ordenadoPor = ""
    private fun datosOrden(){
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes").child(idOrden)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val idOrden = "${snapshot.child("idOrden").value}"
                costo = "${snapshot.child("costo").value}"
                val tiempoOrden = "${snapshot.child("tiempoOrden").value}"
                val estadoOrden = "${snapshot.child("estadoOrden").value}"
                ordenadoPor = "${snapshot.child("ordenadoPor").value}"

                val fecha = Constantes().obtenerFecha(tiempoOrden.toLong())

                binding.idOrdenD.text = idOrden
                binding.fechaOrdenD.text = fecha
                binding.estadoOrdenD.text = estadoOrden
                binding.costoOrdenD.text = costo

                if (estadoOrden.equals("Solicitud recibida")){
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenVActivity, R.color.azul_marino_oscuro))
                }else if (estadoOrden.equals("En preparación")){
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenVActivity, R.color.naranja))
                }else if (estadoOrden.equals("Entregado")){
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenVActivity, R.color.verde_oscuro2))
                }else if (estadoOrden.equals("Cancelado")){
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenVActivity, R.color.rojo))
                }

                direccionCliente(ordenadoPor)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })







    }
}