package modelo.pojos

class Workout(
    var id: String? = null,
    var nombre: String? = null,
    var nivel: Int? = 0,
    var tiempo: Int? = 0,
    var video: String? = null,
    var tipo: String? = null,
) {

    var workoutObj: Workout? = null // Workout como objeto

    override fun toString(): String {
        return "Workout(id='$id', nombre='$nombre', nivel=$nivel, tiempo=$tiempo, video='$video', tipo='$tipo')"
    }
}
