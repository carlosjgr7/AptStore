package com.dinocode.tiendavirtualapp_kotlin.Cliente.Orden

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dinocode.tiendavirtualapp_kotlin.Adaptadores.AdaptadorProductoOrden
import com.dinocode.tiendavirtualapp_kotlin.Cliente.Pago.PagoActivity
import com.dinocode.tiendavirtualapp_kotlin.Constantes
import com.dinocode.tiendavirtualapp_kotlin.Modelos.ModeloProductoOrden
import com.dinocode.tiendavirtualapp_kotlin.Modelos.ResponseHttp
import com.dinocode.tiendavirtualapp_kotlin.Network.RetrofitClient
import com.dinocode.tiendavirtualapp_kotlin.R
import com.dinocode.tiendavirtualapp_kotlin.data.ApiService
import com.dinocode.tiendavirtualapp_kotlin.databinding.ActivityDetalleOrdenCactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetalleOrdenCActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleOrdenCactivityBinding
    private var idOrden = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var productosArrayList : ArrayList<ModeloProductoOrden>
    private lateinit var productoOrdenAdapter : AdaptadorProductoOrden

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleOrdenCactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        idOrden = intent.getStringExtra("idOrden") ?: ""

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnContinuarPago.setOnClickListener {
            launchMasterATP()
        }

        datosOrden()
        direccionCliente()
        productosOrden()
    }

    private fun launchMasterATP() {
        val packageName = "com.carlosjgr7.masterscanner"
        val className = "$packageName.MainActivity"
        Log.d("LaunchApp", "Attempting to launch: $packageName")

        try {
            val intent = Intent()
            intent.component = ComponentName(packageName, className)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Log.d("LaunchApp", "Intent found for: $packageName")
        } catch (e: Exception) {
            Log.e("LaunchApp", "Error launching app: $packageName", e)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("market://details?id=$packageName")
            startActivity(intent)
            Toast.makeText(this, "Master Scanner app not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enviarProductosAlServidor(productos : ArrayList<ModeloProductoOrden>) {
        val datosParaEnviar = mapOf(
            "idOrden" to idOrden,
            "costoTotal" to costo,
            "productos" to productos,
            "currentId" to ordenadoPor)

        println("Mapa de datos para convertir a json : ${datosParaEnviar}")

        //Convertir los datos del mapa a una cadena de texto en formato json
        val gson = Gson()
        val jsonDatos = gson.toJson(datosParaEnviar)

        println("Datos de texto en formato json: ${jsonDatos}")

        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = jsonDatos.toRequestBody(mediaType)

        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)
        val call = apiService.enviarOrdenDeCompra(requestBody)
        call.enqueue(object : Callback<ResponseHttp>{
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.isSuccessful){
                    println("Orden enviada correctamente")
                    println("Respuesta del servidor: ${response.body()?.message}")
                    println("preferenceId: ${response.body()?.preferenceId}")
                    println("init_point: ${response.body()?.init_point}")

                    val init_point = response.body()?.init_point.toString()

                    val intent = Intent(this@DetalleOrdenCActivity , PagoActivity::class.java)
                    intent.putExtra("init_point", init_point)
                    startActivity(intent)

                }else if (response.code() == 500){
                    println("Ha ocurrido un error en el servidor: ${response.errorBody()?.toString()}")
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                println("Ha ocurrido un error al hacer la petición: ${t.message}")

            }
        })

    }

    private fun productosOrden(){
        productosArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes").child(idOrden).child("Productos")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                productosArrayList.clear()
                for (ds in snapshot.children){
                    val modeloProductoOrden = ds.getValue(ModeloProductoOrden::class.java)
                    productosArrayList.add(modeloProductoOrden!!)
                }

                productoOrdenAdapter = AdaptadorProductoOrden(this@DetalleOrdenCActivity, productosArrayList)
                binding.ordenesRv.adapter = productoOrdenAdapter

                binding.cantidadOrdenD.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })





    }

    private fun direccionCliente(){
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
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
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val idOrden = "${snapshot.child("idOrden").value}"
                costo = "${snapshot.child("costo").value}"
                val tiempoOrden = "${snapshot.child("tiempoOrden").value}"
                ordenadoPor = "${snapshot.child("ordenadoPor").value}"

                val fecha = Constantes().obtenerFecha(tiempoOrden.toLong())

                binding.idOrdenD.text = idOrden
                binding.fechaOrdenD.text = fecha
                binding.costoOrdenD.text = costo

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })







    }
}