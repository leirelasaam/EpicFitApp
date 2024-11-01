package modelo.pojos

class Ejercicio(
    var id: String? = null,
    var nombre: String? = null,
    var repeticiones: Int? = 0,
    var series: Int? = 0,
    var descanso: Int? = 0,
    var tiempoSerie: Int? = 0
) {
    override fun toString(): String {
        return "Ejercicio(id='$id', nombre=$nombre, repeticiones=$repeticiones, series=$series, descanso=$descanso, tiempoSerie=$tiempoSerie)"
    }
}