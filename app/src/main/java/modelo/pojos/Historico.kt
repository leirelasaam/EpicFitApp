package modelo.pojos

import java.time.LocalDateTime

class Historico(var fecha: LocalDateTime, var porcentaje: Int, var tiempo: Int, var usuario: Usuario, var workout: Workout) {

}