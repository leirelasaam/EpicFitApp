package bbdd

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import modelo.pojos.Usuario

class GestorDeHistoricos {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionName = "Historicos"

    fun obtenerHistoricos(usuario: Usuario, onResult: (QuerySnapshot?) -> Unit) {
        db.collection(collectionName).whereEqualTo("usuario", usuario.id).get()
            .addOnSuccessListener { result ->
                onResult(result)

                // Verificar si hay documentos en la consulta
                if (result != null && !result.isEmpty) {
                    // Iterar sobre los documentos obtenidos
                    for (document in result.documents) {
                        val usuarioRef = document.getDocumentReference("usuario")
                        val workoutRef = document.getDocumentReference("workout")
                        val tiempo = document.getDouble("tiempo")?.toInt()
                        val porcentaje = document.getDouble("porcentaje")?.toInt()
                        val fecha = document.getDate("fecha")

                        Log.d("FirestoreService", "Historico: \n$usuarioRef\n $workoutRef\n $tiempo\n $porcentaje\n $fecha\n")
                    }
                } else {
                    Log.d("FirestoreService", "No se ha encontrado el histórico.")
                }
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreService", "Error recogiendo los documentos.", e)
                onResult(null)
            }
    }

}