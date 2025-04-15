package com.dinocode.tiendavirtualapp_kotlin.Vendedor

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
import com.dinocode.tiendavirtualapp_kotlin.R
import com.dinocode.tiendavirtualapp_kotlin.SeleccionarTipoActivity
import com.dinocode.tiendavirtualapp_kotlin.Vendedor.Bottom_Nav_Fragments_Vendedor.FragmentMisProductosV
import com.dinocode.tiendavirtualapp_kotlin.Vendedor.Bottom_Nav_Fragments_Vendedor.FragmentOrdenesV
import com.dinocode.tiendavirtualapp_kotlin.Vendedor.Nav_Fragments_Vendedor.FragmentCategoriasV
import com.dinocode.tiendavirtualapp_kotlin.Vendedor.Nav_Fragments_Vendedor.FragmentInicioV
import com.dinocode.tiendavirtualapp_kotlin.Vendedor.Nav_Fragments_Vendedor.FragmentMiTiendaV
import com.dinocode.tiendavirtualapp_kotlin.Vendedor.Nav_Fragments_Vendedor.FragmentProductosV
import com.dinocode.tiendavirtualapp_kotlin.databinding.ActivityMainVendedorBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivityVendedor : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainVendedorBinding
    private var firebaseAuth : FirebaseAuth?=null

    private var dobleClick = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainVendedorBinding.inflate(layoutInflater)
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
                Toast.makeText(this@MainActivityVendedor, "Presione nuevamente para salir",
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

        replaceFragment(FragmentInicioV())
        binding.navigationView.setCheckedItem(R.id.op_inicio_v)

    }

    private fun cerrarSesion(){
        firebaseAuth!!.signOut()
        startActivity(Intent(applicationContext, SeleccionarTipoActivity::class.java))
        finish()
        Toast.makeText(applicationContext, "Has cerrado sesión", Toast.LENGTH_SHORT).show()
    }

    private fun comprobarSesion(){
        /*Si el usuario no ha iniciado sesión, que lo diriga a OpcionesLogin*/
        if (firebaseAuth!!.currentUser==null){
            startActivity(Intent(applicationContext, SeleccionarTipoActivity::class.java))
        }else{
            Toast.makeText(applicationContext,"Usuario en línea", Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.op_inicio_v->{
                replaceFragment(FragmentInicioV())
            }
            R.id.op_mi_tienda_v->{
                replaceFragment(FragmentMiTiendaV())
            }
            R.id.op_categorias_v->{
                replaceFragment(FragmentCategoriasV())
            }
            R.id.op_productos_v->{
                replaceFragment(FragmentProductosV())
            }
            R.id.op_cerrar_sesion_v->{
                cerrarSesion()
            }
            R.id.op_mis_productos_v->{
                replaceFragment(FragmentMisProductosV())
            }
            R.id.op_mis_ordenes_v->{
                replaceFragment(FragmentOrdenesV())
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}