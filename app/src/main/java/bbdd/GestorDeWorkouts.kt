package bbdd

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import modelo.pojos.Workout

class GestorDeWorkouts {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val coleccionWorkouts = "Workouts"

    fun obtenerWorkouts(
        onSuccess: (List<Workout>) -> Unit,
        onFailure: (Exception) -> Unit
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
}
