package com.example.epicfitapp

import GestorDeHistoricos
import adaptadores.HistoricoAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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
            val usuarioTxt: TextView = findViewById(R.id.usuarioTxt)
            usuarioTxt.text = " @" + usuarioActual.usuario
            usuarioTxt.setOnClickListener {
                val intent = Intent(this, PerfilActivity::class.java)
                startActivity(intent)
            }
            (findViewById<TextView>(R.id.nivelValorTxt)).text = usuarioActual.nivel.toString()
        }

        val noHistoricos: TextView = findViewById(R.id.noHistoricos)
        val txtFiltrar: TextView = findViewById(R.id.txtFiltrar)
        val spinner = findViewById<Spinner>(R.id.spinnerNiveles)
        val recycler = findViewById<RecyclerView>(R.id.listaHistorico)
        recycler.layoutManager = LinearLayoutManager(this)
        val gdh: GestorDeHistoricos = GestorDeHistoricos()

        if (usuarioActual != null) {
            gdh.obtenerHistoricosPorUsuario(
                usuarioActual,
                onSuccess = { historicos ->
                    if (historicos.isEmpty()){
                        mostrarOcultar(noHistoricos, true)
                        mostrarOcultar(recycler, false)
                        mostrarOcultar(spinner, false)
                        mostrarOcultar(txtFiltrar, false)
                    } else {
                        val adapter = HistoricoAdapter(this@HistoricoActivity, historicos)
                        recycler.adapter = adapter

                        // Obtener niveles únicos y ordenados de menor a mayor
                        val niveles =
                            historicos.mapNotNull { it.workoutObj?.nivel }.toSet().sorted()
                        configurarSpinner(niveles.toList(), historicos, adapter)
                    }
                },
                onFailure = { _ ->
                    mostrarOcultar(noHistoricos, true)
                    mostrarOcultar(recycler, false)
                    mostrarOcultar(spinner, false)
                    mostrarOcultar(txtFiltrar, false)
                    Toast.makeText(this, "No se han podido obtener los históricos", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun configurarSpinner(
        niveles: List<Int>,
        historicos: List<Historico>,
        historicoAdapter: HistoricoAdapter
    ) {
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
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: android.view.View,
                position: Int,
                id: Long
            ) {
                val selectedLevel = nivelesConTodos[position]
                if (selectedLevel == getString(R.string.todos_niveles)) {
                    // Si se selecciona "Todos los niveles", mostrar todos los históricos
                    historicoAdapter.updateData(historicos)
                } else {
                    // Filtrar por nivel seleccionado
                    val levelToFilter = selectedLevel.toInt()
                    val filteredHistoricos =
                        historicos.filter { it.workoutObj?.nivel == levelToFilter }
                    historicoAdapter.updateData(filteredHistoricos)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // TODO
            }
        }
    }

    // Método para cambiar la visibilidad de las vistas
    private fun mostrarOcultar(view: View, mostrar: Boolean) {
        view.visibility = if (mostrar) View.VISIBLE else View.GONE
    }

}