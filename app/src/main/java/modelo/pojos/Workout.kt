package modelo.pojos

import com.google.firebase.firestore.DocumentReference

class Workout(
    var id: String? = null,
    var nombre: String? = null,
    var nivel: Int? = 0,
    var tiempo: Int? = 0,
    var video: String? = null,
    var tipo: String? = null,
    var ejercicios: List<DocumentReference>? = null
) {
    var ejerciciosObj: List<Ejercicio>? = null // Lista de ejercicios

    override fun toString(): String {
        return "Workout(id='$id', nombre='$nombre', nivel=$nivel, tiempo=$tiempo, video='$video', tipo='$tipo', ejercicios=$ejercicios, ejerciciosObj=${ejerciciosObj.toString()})"
    }
}
