package com.dinocode.tiendavirtualapp_kotlin.Vendedor

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dinocode.tiendavirtualapp_kotlin.Constantes
import com.dinocode.tiendavirtualapp_kotlin.databinding.ActivityRegistroVendedorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroVendedorActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegistroVendedorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarV.setOnClickListener {
            validarInformacion()
        }
    }

    private var nombres = ""
    private var email = ""
    private var password = ""
    private var cpassword =""
    private fun validarInformacion() {
        nombres = binding.etNombresV.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        cpassword = binding.etCPassword.text.toString().trim()

        if (nombres.isEmpty()){
            binding.etNombresV.error = "Ingrese sus nombres"
            binding.etNombresV.requestFocus()
        } else if (email.isEmpty()){
            binding.etEmail.error = "Ingrese email"
            binding.etEmail.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Email no válido"
            binding.etEmail.requestFocus()
        } else if (password.isEmpty()){
            binding.etPassword.error = "Ingrese password"
            binding.etPassword.requestFocus()
        } else if (password.length < 6){
            binding.etPassword.error = "Necesita 6 o más car."
            binding.etPassword.requestFocus()
        } else if (cpassword.isEmpty()){
            binding.etCPassword.error = "Confirme password"
            binding.etCPassword.requestFocus()
        } else if (password!=cpassword){
            binding.etCPassword.error = "No coincide"
            binding.etCPassword.requestFocus()
        } else{
            registrarVendedor()
        }

    }

    private fun registrarVendedor() {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                insertarInfoBD()
            }
            .addOnFailureListener { e->
                Toast.makeText(this, "Falló el registro debido a ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun insertarInfoBD(){
        progressDialog.setMessage("Guardando información...")

        val uidBD = firebaseAuth.uid
        val nombreBD = nombres
        val emailBD = email
        val tiempoBD = Constantes().obtenerTiempoD()

        val datosVendedor = HashMap<String, Any>()

        datosVendedor["uid"] = "$uidBD"
        datosVendedor["nombres"] = "$nombreBD"
        datosVendedor["email"] = "$emailBD"
        datosVendedor["tipoUsuario"] = "vendedor"
        datosVendedor["tiempo_registro"] = tiempoBD

        val references = FirebaseDatabase.getInstance().getReference("Usuarios")
        references.child(uidBD!!)
            .setValue(datosVendedor)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityVendedor::class.java))
                finish()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Falló el registro en BD debido a ${e.message}",Toast.LENGTH_SHORT).show()
            }

    }
}