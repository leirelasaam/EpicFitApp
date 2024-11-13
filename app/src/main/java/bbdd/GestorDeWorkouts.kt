package bbdd

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import modelo.pojos.Workout
import android.widget.Toast
import modelo.pojos.Ejercicio

class GestorDeWorkouts {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val coleccionWorkouts = "Workouts"

    fun obtenerWorkouts(onSuccess: (List<Workout>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(coleccionWorkouts)
            .get()
            .addOnSuccessListener { result ->
                val workouts = mutableListOf<Workout>()
                val subcoleccionEjercicios = "Ejercicios"
                if (result.isEmpty) {
                    onSuccess(workouts)
                    return@addOnSuccessListener
                }

                var consultasCompletadas = 0

                for (document in result) {
                    val workout = document.toObject(Workout::class.java)
                    workout.id = document.id

                    // -------------- OBTENER EJERCICIOS DEL WORKOUT --------------
                    document.reference.collection(subcoleccionEjercicios)
                        .get()
                        .addOnSuccessListener { ejerciciosSnapshot ->
                            val ejercicios = mutableListOf<Ejercicio>()
                            for (ej in ejerciciosSnapshot) {
                                val ejercicio = ej.toObject(Ejercicio::class.java)
                                ejercicio.id = ej.id
                                ejercicios.add(ejercicio)
                            }

                            workout.ejerciciosObj = ejercicios
                            val tiempoTotal = agregarTiempoEstimadoWorkout(ejercicios)
                            workout.tiempo = tiempoTotal
                            workouts.add(workout)

                            // Verificar si todas las consultas están completas
                            consultasCompletadas++
                            if (consultasCompletadas == result.size()) {
                                onSuccess(workouts)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("GestorDeWorkouts", "Error al obtener ejercicios: ", exception)
                            consultasCompletadas++
                            if (consultasCompletadas == result.size()) {
                                onSuccess(workouts)
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("GestorDeWorkouts", "Error al obtener workouts: ", exception)
                onFailure(exception)
            }
    }

    fun borrarWorkout(workoutId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(coleccionWorkouts)
            .document(workoutId)
            .delete()
            .addOnSuccessListener {
                Log.d("GestorDeWorkouts", "Workout borrado con éxito")
                onSuccess() // Llamar al callback de éxito
            }
            .addOnFailureListener { exception ->
                Log.e("GestorDeWorkouts", "Error al borrar workout: ", exception)
                onFailure(exception) // Llamar al callback de fallo
            }
    }

    fun subirWorkout(context: Context, workout: Workout) {
        val data = hashMapOf(
            "nombre" to workout.nombre,
            "nivel" to workout.nivel,
            "tiempo" to workout.tiempo,
            "video" to workout.video,
            "tipo" to workout.tipo,
        )
        db.collection(coleccionWorkouts)
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "Workout '${workout.nombre}' registrado con éxito", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Workout guardado con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al registrar workout: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error al guardar workout", e)
            }
    }

    fun actualizarWorkout(workout: Workout, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val data = hashMapOf(
            "nombre" to workout.nombre,
            "nivel" to workout.nivel,
            "tiempo" to workout.tiempo,
            "video" to workout.video,
            "tipo" to workout.tipo
        )

        workout.id?.let {
            db.collection(coleccionWorkouts)
                .document(it)
                .update(data as Map<String, Any>)
                .addOnSuccessListener {
                    //Toast.makeText(context, "Workout '${workout.nombre}' actualizado con éxito", Toast.LENGTH_SHORT).show()
                    Log.d("Firestore", "Workout actualizado con ID: $workout.nombre")
                }
                .addOnFailureListener { e ->
                    //Toast.makeText(context, "Error al actualizar workout: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Firestore", "Error al actualizar workout", e)
                }
        }
    }

    private fun agregarTiempoEstimadoWorkout(ejercicios: MutableList<Ejercicio>): Int {
        var tiempoTotal = 0
        for (ejercicio in ejercicios) {
            val tiempo = ejercicio.tiempoSerie!!
            val descanso = ejercicio.descanso!!
            val cuentaRegresiva = 5
            val series = ejercicio.series!!

            tiempoTotal += (tiempo * series) + (descanso * (series - 1) + (cuentaRegresiva * series))
        }
        return tiempoTotal
    }
}
