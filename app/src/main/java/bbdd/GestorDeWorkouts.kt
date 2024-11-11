package bbdd

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import modelo.pojos.Workout
import android.widget.Toast

class GestorDeWorkouts {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val coleccionWorkouts = "Workouts"

    fun obtenerWorkouts(onSuccess: (List<Workout>) -> Unit, onFailure: (Exception) -> Unit
    ) {
        db.collection(coleccionWorkouts)
            .get()
            .addOnSuccessListener { result ->
                val workouts = mutableListOf<Workout>()
                for (document in result) {
                    val workout = document.toObject(Workout::class.java)
                    workout.id = document.id // Asignar el ID del documento al objeto Workout
                    workouts.add(workout)
                }
                onSuccess(workouts) // Llamar al callback de éxito
            }
            .addOnFailureListener { exception ->
                Log.e("GestorDeWorkouts", "Error al obtener workouts: ", exception)
                onFailure(exception) // Llamar al callback de fallo
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

    fun actualizarWorkout(context: Context, workout: Workout, idWorkout: String) {
        val data = hashMapOf(
            "nombre" to workout.nombre,
            "nivel" to workout.nivel,
            "tiempo" to workout.tiempo,
            "video" to workout.video,
            "tipo" to workout.tipo
        )
        db.collection(coleccionWorkouts)
            .document(idWorkout)
            .update(data as Map<String, Any>) // update() solo actualiza los campos donde se han introducido datos
            .addOnSuccessListener {
                Toast.makeText(context, "Workout '${workout.nombre}' actualizado con éxito", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Workout actualizado con ID: $idWorkout")
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al actualizar workout: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error al actualizar workout", e)
            }
    }
}
