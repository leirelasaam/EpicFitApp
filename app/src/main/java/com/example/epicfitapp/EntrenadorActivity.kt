package com.example.epicfitapp

import adaptadores.WorkoutsAdapter
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bbdd.GestorDeWorkouts
import modelo.pojos.Workout

class EntrenadorActivity : AppCompatActivity() {

    private lateinit var workoutsRecyclerView: RecyclerView
    private lateinit var workoutsAdapter: WorkoutsAdapter
    private var workoutsList: List<Workout> = listOf() // Lista de workouts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrenador)

        // Configura el RecyclerView
        workoutsRecyclerView = findViewById(R.id.workoutsRecyclerView)
        workoutsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Carga los datos de los workouts
        loadWorkouts()

        // Configura el adaptador con los datos
        workoutsAdapter = WorkoutsAdapter(this, workoutsList)
        workoutsRecyclerView.adapter = workoutsAdapter
    }

    private fun loadWorkouts() {
        val recycler = findViewById<RecyclerView>(R.id.workoutsRecyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        val gdw = GestorDeWorkouts()


        gdw.obtenerWorkouts(
            onSuccess = { workouts ->
                // Aquí se recibe la lista de workouts
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
                // Aquí puedes manejar el error, como mostrar un mensaje al usuario
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
                    val filteredWorkouts = workouts.filter { it.workoutObj?.nivel == levelToFilter }
                    workoutAdapter.updateData(filteredWorkouts)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Aquí puedes decidir qué hacer si no se selecciona nada
                // Por ejemplo, podrías mostrar todos los workouts nuevamente
                workoutAdapter.updateData(workouts)
            }
        }
    }


}
