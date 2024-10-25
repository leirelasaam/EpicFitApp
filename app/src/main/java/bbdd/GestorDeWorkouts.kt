package bbdd

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import modelo.pojos.Historico
import modelo.pojos.Usuario
import modelo.pojos.Workout

class WorkoutNotFoundException(message: String) : Exception(message)
class GestorDeWorkouts {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionName = "Workouts"

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun obtenerWorkoutPorId(workoutId: String): Workout {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val collectionName = "Workouts"
        val workout: Workout = Workout()

        try {
            val document = db.collection(collectionName).document(workoutId)
                .get().await()

            if (!document.exists()) {
                throw WorkoutNotFoundException("No se encontró el workout con ID: $workoutId")
            } else {
                val nombre = document.getString("nombre")
                val nivel = document.getDouble("nivel")?.toInt()
                val tiempo = document.getDouble("tiempo")?.toInt()
                val video = document.getString("video")
                val tipo = document.getString("tipo")

                workout.nombre = nombre
                workout.nivel = nivel
                workout.tiempo = tiempo
                workout.video = video
                workout.tipo = tipo
            }
        } catch (e: WorkoutNotFoundException) {
            Log.d("FirestoreService", "Error: ${e.message}")
            throw e
        } catch (e: Exception) {
            Log.d("FirestoreService", "Error: $e")
            throw Exception("Error al obtener el workout")
        }

        return workout
    }

}