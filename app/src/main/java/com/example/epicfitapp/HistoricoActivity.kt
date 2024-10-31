package com.example.epicfitapp

import GestorDeHistoricos
import adaptadores.HistoricoAdapter
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import modelo.pojos.Historico
import modelo.pojos.UsuarioLogueado

class HistoricoActivity : BaseActivity() {
    @SuppressLint("SetTextI18n")
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

                    // Obtener niveles únicos y ordenados de menor a mayor
                    val niveles = historicos.mapNotNull { it.workoutObj?.nivel }.toSet().sorted()
                    configurarSpinner(niveles.toList(), historicos, adapter)
                },
                onFailure = { exception ->
                    Log.e("HIS", "Error al obtener históricos: ${exception.message}")
                }
            )
        }
    }

    private fun configurarSpinner(niveles: List<Int>, historicos: List<Historico>, historicoAdapter: HistoricoAdapter) {
        val spinner = findViewById<Spinner>(R.id.spinnerNiveles)

        // Añadir la opción por defecto, muestra todos los niveles
        val nivelesConTodos = mutableListOf(getString(R.string.todos_niveles)).apply {
            addAll(niveles.map { it.toString() })
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nivelesConTodos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Configurar el listener para cambios en el Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedLevel = nivelesConTodos[position]
                if (selectedLevel == getString(R.string.todos_niveles)) {
                    // Si se selecciona "Todos los niveles", mostrar todos los históricos
                    historicoAdapter.updateData(historicos)
                } else {
                    // Filtrar por nivel seleccionado
                    val levelToFilter = selectedLevel.toInt()
                    val filteredHistoricos = historicos.filter { it.workoutObj?.nivel == levelToFilter }
                    historicoAdapter.updateData(filteredHistoricos)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // TODO
            }
        }
    }

}