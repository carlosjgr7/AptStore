package com.dinocode.tiendavirtualapp_kotlin.Vendedor.Bottom_Nav_Fragments_Vendedor

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dinocode.tiendavirtualapp_kotlin.Adaptadores.AdaptadorCategoriaV
import com.dinocode.tiendavirtualapp_kotlin.Adaptadores.AdaptadorProducto
import com.dinocode.tiendavirtualapp_kotlin.Modelos.ModeloCategoria
import com.dinocode.tiendavirtualapp_kotlin.Modelos.ModeloProducto
import com.dinocode.tiendavirtualapp_kotlin.databinding.FragmentMisProductosVBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentMisProductosV : Fragment() {

    private lateinit var binding : FragmentMisProductosVBinding
    private lateinit var mContext : Context



    private lateinit var categoriasArrayList : ArrayList<ModeloCategoria>
    private lateinit var adaptadorCategoriaV : AdaptadorCategoriaV

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMisProductosVBinding.inflate(LayoutInflater.from(mContext), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //listarProductos()
        listarCategorias()
    }

    private fun listarCategorias() {
        categoriasArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriasArrayList.add(modelo!!)
                }
                adaptadorCategoriaV = AdaptadorCategoriaV(mContext, categoriasArrayList)
                binding.categoriasRV.adapter = adaptadorCategoriaV
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }




}