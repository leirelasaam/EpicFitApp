package com.example.epicfitapp

import adaptadores.WorkoutsAdapter
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bbdd.GestorDeWorkouts
import modelo.pojos.Ejercicio
import modelo.pojos.Usuario
import modelo.pojos.Workout

class EntrenadorActivity : BaseActivity() {

    private lateinit var workoutsRecyclerView: RecyclerView
    private lateinit var workoutsAdapter: WorkoutsAdapter
    private var workoutsList: List<Workout> = listOf() // Lista de workouts
    val gdw = GestorDeWorkouts()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrenador)

        // Configura el RecyclerView
        workoutsRecyclerView = findViewById(R.id.workoutsRecyclerView)
        workoutsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Carga los datos de los workouts
        loadWorkouts()

        val btnAniadirWorkout = findViewById<Button>(R.id.btn_aniadirWorkout)
        btnAniadirWorkout.setOnClickListener {
            mostrarDialogoAniadirWorkout()

        }

        // Configura el adaptador con los datos
        workoutsAdapter = WorkoutsAdapter(this, workoutsList)
        workoutsRecyclerView.adapter = workoutsAdapter
    }

    private fun loadWorkouts() {
        val recycler = findViewById<RecyclerView>(R.id.workoutsRecyclerView)
        recycler.layoutManager = LinearLayoutManager(this)

        gdw.obtenerWorkouts(
            onSuccess = { workouts ->
                workoutsList = workouts

                // Configurar el adapter con la lista de workouts
                val adapter = WorkoutsAdapter(this@EntrenadorActivity, workouts)
                recycler.adapter = adapter

                // Obtener niveles únicos y ordenados de menor a mayor
                val niveles = workouts.mapNotNull { it.nivel }.toSet().sorted()
                configurarSpinner(niveles.map { it.toString() }, workouts, adapter)
            },
            onFailure = { exception ->
                Log.e("loadWorkouts", "Error al obtener workouts: ${exception.message}")
            }
        )
    }

    private fun configurarSpinner(niveles: List<String>, workouts: List<Workout>, workoutAdapter: WorkoutsAdapter) {
        val spinner = findViewById<Spinner>(R.id.spinnerNiveles)

        // Añadir la opción por defecto, muestra todos los niveles
        val nivelesConTodos = mutableListOf(getString(R.string.todos_niveles)).apply {
            addAll(niveles)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nivelesConTodos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Configurar el listener para cambios en el Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedLevel = nivelesConTodos[position]
                if (selectedLevel == getString(R.string.todos_niveles)) {
                    // Si se selecciona "Todos los niveles", mostrar todos los workouts
                    workoutAdapter.updateData(workouts)
                } else {
                    // Filtrar por nivel seleccionado
                    val levelToFilter = selectedLevel.toInt()
                    val filteredWorkouts = workouts.filter { it.nivel == levelToFilter }
                    workoutAdapter.updateData(filteredWorkouts)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                workoutAdapter.updateData(workouts)
            }
        }
    }

    fun mostrarDialogoAniadirWorkout() {
        // Añadir un Workout
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Añadir Workout")

        val nombreInput = EditText(this)
        nombreInput.hint = "Nombre del Workout"
        val nivelInput = EditText(this)
        nivelInput.hint = "Nivel (número entero)"
        val tiempoInput = EditText(this)
        tiempoInput.hint = "Tiempo (en minutos)"
        val videoInput = EditText(this)
        videoInput.hint = "Enlace del video (opcional)"
        val tipoInput = EditText(this)
        tipoInput.hint = "Tipo de Workout"

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(nombreInput)
        layout.addView(nivelInput)
        layout.addView(tiempoInput)
        layout.addView(videoInput)
        layout.addView(tipoInput)
        builder.setView(layout)

        builder.setPositiveButton("Añadir") { dialog: DialogInterface, _: Int ->
            val nuevoWorkout = Workout(
                nombre = nombreInput.text.toString(),
                nivel = nivelInput.text.toString().toIntOrNull() ?: 0,
                tiempo = tiempoInput.text.toString().toIntOrNull() ?: 0,
                video = videoInput.text.toString(),
                tipo = tipoInput.text.toString())

            // Llamar a la función para subir a Firebase
            gdw.subirWorkout(this,nuevoWorkout)
            loadWorkouts()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        // Mostrar el diálogo
        builder.create().show()
    }
    }
