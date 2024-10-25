package modelo.pojos

import java.time.LocalDateTime

class Historico (
    var fecha: LocalDateTime? = null,
    var porcentaje: Int? = 0,
    var tiempo: Int? = 0,
    var usuario: Usuario? = null,
    var workout: Workout? = null
) {

}