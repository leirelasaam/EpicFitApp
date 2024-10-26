package com.example.epicfitapp

import adaptadores.HistoricoAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bbdd.GestorDeHistoricos
import kotlinx.coroutines.launch
import modelo.pojos.Usuario
import modelo.pojos.UsuarioLogueado

class HistoricoActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historico)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.historico)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usuarioActual = UsuarioLogueado.usuario

        if (usuarioActual != null) {
            (findViewById<TextView>(R.id.nivelTxt)).text =
                (findViewById<TextView>(R.id.nivelTxt)).text.toString() + " @" + usuarioActual.usuario
            (findViewById<TextView>(R.id.nivelValorTxt)).text = usuarioActual.nivel.toString()
        }

        val recycler = findViewById<RecyclerView>(R.id.listaHistorico)
        recycler.layoutManager = LinearLayoutManager(this)
        val gdh: GestorDeHistoricos = GestorDeHistoricos()

        if (usuarioActual != null) {
            gdh.obtenerHistoricosPorUsuario(
                usuarioActual,
                onSuccess = { historicos ->
                    val adapter = HistoricoAdapter(this@HistoricoActivity, historicos)
                    recycler.adapter = adapter
                },
                onFailure = { exception ->
                    Log.e("HIS", "Error al obtener históricos: ${exception.message}")
                }
            )
        }
    }

}