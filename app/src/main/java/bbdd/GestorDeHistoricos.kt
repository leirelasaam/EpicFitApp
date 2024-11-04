import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import modelo.pojos.Ejercicio
import modelo.pojos.Historico
import modelo.pojos.Usuario
import modelo.pojos.Workout


class GestorDeHistoricos {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val coleccionUsuarios = "Usuarios"
    private val subcoleccionHistoricos = "Historicos"
    private val subcoleccionEjercicios = "Ejercicios"

    fun obtenerHistoricosPorUsuario(
        usuario: Usuario,
        onSuccess: (List<Historico>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(coleccionUsuarios).document(usuario.id!!)
            .collection(subcoleccionHistoricos)
            //.orderBy("fecha", Query.Direction.DESCENDING)  --- NO FUNCIONA
            .get()
            .addOnSuccessListener { historicosSnapshot ->
                val historicos = mutableListOf<Historico>()

                for (document in historicosSnapshot) {
                    val historico = document.toObject(Historico::class.java)
                    historico.id = document.id

                    val workoutRef = historico.workout

                    // -------------- OBTENER WORKOUT DEL HISTÓRICO --------------
                    workoutRef?.get()?.addOnSuccessListener { workoutDocument ->
                        if (workoutDocument != null) {
                            val workout = workoutDocument.toObject(Workout::class.java)
                            workout?.id = workoutDocument.id

                            if (workout != null) {
                                // -------------- OBTENER EJERCICIOS DEL WORKOUT --------------
                                workoutRef.collection(subcoleccionEjercicios)
                                    .get()
                                    .addOnSuccessListener { ejerciciosSnapshot ->
                                        val ejercicios = mutableListOf<Ejercicio>()
                                        for (ej in ejerciciosSnapshot) {
                                            val ejercicio = ej.toObject(Ejercicio::class.java)
                                            ejercicio.id = ej.id
                                            ejercicios.add(ejercicio)
                                        }

                                        workout.ejerciciosObj = ejercicios
                                        var tiempoTotal = agregarTiempoEstimadoWorkout(ejercicios)
                                        workout.tiempo = tiempoTotal
                                        historico.workoutObj = workout
                                        historicos.add(historico)

                                        if (historicos.size == historicosSnapshot.size()) {
                                            // Ordenar los historicos por fecha antes de devolver
                                            val sortedHistoricos = historicos.sortedByDescending { it.fecha }
                                            onSuccess(sortedHistoricos)
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("HIS", "Error al obtener ejercicios: ", exception)
                                        onFailure(exception)
                                    }
                            }

                        }
                    }?.addOnFailureListener { exception ->
                        Log.e("HIS", "Error al obtener el workout: ", exception)
                        onFailure(exception)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HIS", "Error al obtener historicos: ", exception)
                onFailure(exception)
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
