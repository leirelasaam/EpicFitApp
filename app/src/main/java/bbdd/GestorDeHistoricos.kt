package bbdd

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import modelo.pojos.Historico
import modelo.pojos.Usuario
import modelo.pojos.Workout


class HistoricosNotFoundException(message: String) : Exception(message)
class GestorDeHistoricos {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionName = "Historicos"
    private val gestorDeWorkouts = GestorDeWorkouts()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun obtenerHistoricos(usuario: Usuario): List<Historico> {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val collectionName = "Historicos"
        val historicos = mutableListOf<Historico>()

        try {
            val documents = db.collection(collectionName).whereEqualTo(
                "usuario",
                db.collection("Usuarios").document(usuario.id)
            ).get().await()

            if (documents.isEmpty) {
                throw HistoricosNotFoundException("No se encontraron historicos para el usuario con ID: ${usuario.user}")
            } else {
                for (document in documents) {
                    val tiempo = document.getDouble("tiempo")?.toInt()
                    val porcentaje = document.getDouble("porcentaje")?.toInt()
                    val fecha = document.getDate("fecha")?.toInstant()
                        ?.atZone(java.time.ZoneId.systemDefault())
                        ?.toLocalDateTime()

                    val workoutRef: DocumentReference? = document.getDocumentReference("workout")
                    val workout: Workout? = if (workoutRef != null) {
                        gestorDeWorkouts.obtenerWorkoutPorId(workoutRef.id)
                    } else {
                        null
                    }

                    val historico = Historico(
                        fecha = fecha,
                        porcentaje = porcentaje,
                        tiempo = tiempo,
                        usuario = Usuario(usuario.id),
                        workout = workout
                    )
                    historicos.add(historico)
                }
            }
        } catch (e: HistoricosNotFoundException) {
            Log.d("FirestoreService", "Error: ${e.message}")
            throw e
        } catch (e: Exception) {
            Log.d("FirestoreService", "Error: $e")
            throw Exception("Error al obtener los historicos")
        }

        return historicos
    }

}