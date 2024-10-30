package bbdd

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import modelo.pojos.Ejercicio
import modelo.pojos.Historico
import modelo.pojos.Workout


class GestorDeEjercicios {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val coleccionEjercicios = "Ejercicios"

    fun obtenerEjerciciosPorWorkout(
        workout: Workout,
        onSuccess: (List<Ejercicio>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("EJER", "Workout objeto: ${workout.toString()}")
        val ejercicios = mutableListOf<Ejercicio>()
        val ejerciciosRefs: List<DocumentReference>? = workout.ejercicios
        if (ejerciciosRefs != null) {
            for (ejercicioRef in ejerciciosRefs) {
                ejercicioRef.get().addOnSuccessListener { ejercicioDocument ->
                    if (ejercicioDocument != null) {
                        val ejercicio = ejercicioDocument.toObject(Ejercicio::class.java)
                        ejercicio?.id = ejercicioDocument.id
                        if (ejercicio != null) {
                            ejercicios.add(ejercicio)
                            Log.d("EJER", "Ejercicio objeto: ${ejercicio.toString()}")
                        }


                        if (ejercicios.size == ejerciciosRefs.size) {
                            onSuccess(ejercicios)
                        }
                    }

                }.addOnFailureListener { exception ->
                    Log.e("EJER", "Error al obtener ejercicios: ", exception)
                    onFailure(exception)
                }
            }
        }
    }

}