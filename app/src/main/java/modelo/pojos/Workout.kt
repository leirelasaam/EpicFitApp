package modelo.pojos

import com.google.firebase.firestore.DocumentReference

class Workout(
    var id: String? = null,
    var nombre: String? = "",
    var nivel: Int? = 0,
    var tiempo: Int? = 0,
    var video: String? = null,
    var tipo: String? = ""
) {
    var ejerciciosObj: List<Ejercicio>? = null

    var workoutObj: Workout? = null

    override fun toString(): String {
        return "Workout(id='$id', nombre='$nombre', nivel=$nivel, tiempo=$tiempo, video='$video', tipo='$tipo', ejerciciosObj=${ejerciciosObj.toString()})"
    }
}
