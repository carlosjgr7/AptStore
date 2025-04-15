package com.dinocode.tiendavirtualapp_kotlin.Cliente

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.dinocode.tiendavirtualapp_kotlin.Cliente.Nav_Fragments_Cliente.FragmentInicioC
import com.dinocode.tiendavirtualapp_kotlin.Cliente.Nav_Fragments_Cliente.FragmentMiPerfilC
import com.dinocode.tiendavirtualapp_kotlin.R
import com.dinocode.tiendavirtualapp_kotlin.SeleccionarTipoActivity
import com.dinocode.tiendavirtualapp_kotlin.databinding.ActivityMainClienteBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivityCliente : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainClienteBinding
    private var firebaseAuth : FirebaseAuth?=null

    private var dobleClick = false
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        binding.navigationView.setNavigationItemSelectedListener(this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (dobleClick){
                    /*Salimos de la app*/
                    finish()
                    return
                }

                dobleClick = true
                Toast.makeText(this@MainActivityCliente, "Presione nuevamente para salir",
                    Toast.LENGTH_SHORT).show()

                handler.postDelayed({dobleClick = false}, 2000)
            }
        })

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        replaceFragment(FragmentInicioC())
    }

    private fun comprobarSesion(){
        if (firebaseAuth!!.currentUser==null){
            startActivity(Intent(this@MainActivityCliente, SeleccionarTipoActivity::class.java))
            finishAffinity()
        }else{
            Toast.makeText(this, "Usuario en línea", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cerrarSesion(){
        firebaseAuth!!.signOut()
        startActivity(Intent(this@MainActivityCliente, SeleccionarTipoActivity::class.java))
        finishAffinity()
        Toast.makeText(this, "Cerraste sesión", Toast.LENGTH_SHORT).show()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment,fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
         when(item.itemId){
             R.id.op_inicio_c->{
                 replaceFragment(FragmentInicioC())
             }
             R.id.op_mi_perfil_c->{
                 replaceFragment(FragmentMiPerfilC())
             }
             R.id.op_cerrar_sesion_c->{
                 cerrarSesion()
             }

         }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}