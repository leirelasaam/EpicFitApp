package com.example.epicfitapp

import adaptadores.WorkoutsAdapter
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bbdd.GestorDeWorkouts
import modelo.pojos.Workout

class EntrenadorActivity : BaseActivity() {

    private lateinit var workoutsRecyclerView: RecyclerView
    private lateinit var workoutsAdapter: WorkoutsAdapter
    private var workoutsList: List<Workout> = listOf()
    private val gdw = GestorDeWorkouts()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_entrenador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.entrenador)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        workoutsRecyclerView = findViewById(R.id.workoutsRecyclerView)
        workoutsRecyclerView.layoutManager = LinearLayoutManager(this)

        loadWorkouts()

        val btnAniadirWorkout = findViewById<Button>(R.id.btnAgregarWorkout)
        btnAniadirWorkout.setOnClickListener {
            mostrarDialogoAniadirWorkout()
        }
        workoutsAdapter = WorkoutsAdapter(this, workoutsList)
        workoutsRecyclerView.adapter = workoutsAdapter
    }

    private fun loadWorkouts() {
        val recycler = findViewById<RecyclerView>(R.id.workoutsRecyclerView)
        recycler.layoutManager = LinearLayoutManager(this)

        gdw.obtenerWorkouts(
            onSuccess = { workouts: List<Workout> ->
                workoutsList = workouts
                workoutsAdapter = WorkoutsAdapter(this@EntrenadorActivity, workoutsList)
                workoutsRecyclerView.adapter = workoutsAdapter

                val niveles = workoutsList.mapNotNull { it.nivel }.toSet().sorted()
                val spinner = findViewById<Spinner>(R.id.spinnerNiveles)

                val nivelesConTodos = mutableListOf(getString(R.string.todos_niveles)).apply {
                    addAll(niveles.map { it.toString() })
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nivelesConTodos)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: android.view.View, position: Int, id: Long
                    ) {
                        val selectedLevel = nivelesConTodos[position]
                        if (selectedLevel == getString(R.string.todos_niveles)) {
                            workoutsAdapter.updateData(workoutsList)
                        } else {
                            val levelToFilter = selectedLevel.toInt()
                            val filteredWorkouts = workoutsList.filter { it.nivel == levelToFilter }
                            workoutsAdapter.updateData(filteredWorkouts)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        workoutsAdapter.updateData(workoutsList)
                    }
                }
            },
            onFailure = { exception ->
                Log.e("loadWorkouts", "Error al obtener workouts: ${exception.message}")
            }
        )
    }

    private fun mostrarDialogoAniadirWorkout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.agregar_workout))

        val nombreInput = EditText(this)
        nombreInput.hint = getString(R.string.nombre_hint)
        val nivelInput = EditText(this)
        nivelInput.hint = getString(R.string.nivel_hint)
        val tiempoInput = EditText(this)
        tiempoInput.hint = getString(R.string.tiempo_hint)
        val videoInput = EditText(this)
        videoInput.hint = getString(R.string.video_hint)
        val tipoInput = EditText(this)
        tipoInput.hint = getString(R.string.tipo_hint)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(nombreInput)
        layout.addView(nivelInput)
        layout.addView(tiempoInput)
        layout.addView(videoInput)
        layout.addView(tipoInput)
        builder.setView(layout)

        builder.setPositiveButton(getString(R.string.agregar)) { dialog: DialogInterface, _: Int ->
            val nuevoWorkout = Workout(
                nombre = nombreInput.text.toString(),
                nivel = nivelInput.text.toString().toIntOrNull() ?: 0,
                tiempo = tiempoInput.text.toString().toIntOrNull() ?: 0,
                video = videoInput.text.toString(),
                tipo = tipoInput.text.toString()
            )
            gdw.subirWorkout(this, nuevoWorkout)
            loadWorkouts()
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.cancelar)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}