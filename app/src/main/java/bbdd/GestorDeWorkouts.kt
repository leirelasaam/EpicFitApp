package bbdd

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import modelo.pojos.Ejercicio
import modelo.pojos.Workout

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

    fun subirWorkout(
        workout: Workout,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(coleccionWorkouts)
            .add(workout)
            .addOnSuccessListener {
                onSuccess() // Llamar al callback de éxito
            }
            .addOnFailureListener { exception ->
                Log.e("GestorDeWorkouts", "Error al subir workout: ", exception)
                onFailure(exception) // Llamar al callback de fallo
            }
    }

    fun subirEjercicio(workout: Workout, ejercicio: Ejercicio, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Obtén la referencia de la base de datos de Firebase
        val database = FirebaseFirestore.getInstance()

        // Navega a la colección de Workouts
        val workoutRef = workout.id?.let { database.collection("workouts").document(it) }

        // Añadir el ejercicio directamente al campo "ejercicios" en el documento
        if (workoutRef != null) {
            workoutRef.update("ejercicios", FieldValue.arrayUnion(ejercicio))
                .addOnSuccessListener {
                    onSuccess() // Llamada de éxito
                }
                .addOnFailureListener { exception ->
                    onFailure(exception) // Llamada de fallo
                }
        }
    }








}
