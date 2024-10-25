package com.example.epicfitapp

import adaptadores.HistoricoAdapter
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bbdd.GestorDeHistoricos
import kotlinx.coroutines.launch
import modelo.pojos.Usuario

class HistoricoActivity : AppCompatActivity() {
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

        val recycler = findViewById<RecyclerView>(R.id.listaHistorico)
        recycler.layoutManager = LinearLayoutManager(this)
        val gdh: GestorDeHistoricos = GestorDeHistoricos()

        lifecycleScope.launch {
            val usuarioId = "zoVUUYKznIh8KDXOxjUc" // ID de ejemplo del usuario leire
            val historicos = gdh.obtenerHistoricos(Usuario(usuarioId))

            val adapter = HistoricoAdapter(this@HistoricoActivity, historicos)
            recycler.adapter = adapter
        }

    }
}