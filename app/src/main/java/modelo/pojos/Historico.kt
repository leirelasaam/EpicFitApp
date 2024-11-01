package modelo.pojos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

class Historico(
    var id: String? = null,
    var fecha: Timestamp? = null,
    var porcentaje: Int? = 0,
    var tiempo: Int? = 0,
    var workout: DocumentReference? = null
) {
    var workoutObj: Workout? = null // Workout como objeto

    override fun toString(): String {
        return "Historico(id='$id', fecha=${fecha?.toDate()}, porcentaje=$porcentaje, tiempo=$tiempo, workout=$workout, workoutObj=$workoutObj)"
    }
}