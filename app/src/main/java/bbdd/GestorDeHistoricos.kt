package bbdd

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import modelo.pojos.Historico
import modelo.pojos.Usuario
import modelo.pojos.Workout


class GestorDeHistoricos {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val coleccionHistoricos = "Historicos"
    private val coleccionUsuarios = "Usuarios"

    fun obtenerHistoricosPorUsuario(
        usuario: Usuario,
        onSuccess: (List<Historico>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(coleccionHistoricos).whereEqualTo(
            "usuario",
            usuario?.id?.let { db.collection(coleccionUsuarios).document(it) }
        )
            // ordenar por fecha de más reciente
            .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val historicos = mutableListOf<Historico>()

                for (document in result) {
                    val historico = document.toObject(Historico::class.java)
                    historico.id = document.id
                    historico.usuarioObj = usuario

                    val workoutRef = historico.workout
                    // Obtener workout
                    workoutRef?.get()?.addOnSuccessListener { workoutDocument ->
                        if (workoutDocument != null) {
                            val workout = workoutDocument.toObject(Workout::class.java)
                            workout?.id = document.id

                            historico.workoutObj = workoutDocument.toObject(Workout::class.java)

                            historicos.add(historico)
                            Log.d("HIS", "Historico objeto: ${historico.toString()}")

                            if (historicos.size == result.size()) {
                                onSuccess(historicos)
                            }
                        }

                    }?.addOnFailureListener { exception ->
                        Log.e("HIS", "Error al obtener el workout: ", exception)
                        onFailure(exception)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("HIS", "Error getting documents: ", exception)
                onFailure(exception)
            }
    }

}