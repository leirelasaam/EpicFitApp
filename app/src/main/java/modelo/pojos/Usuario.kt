package modelo.pojos

import java.time.LocalDateTime

class Usuario(
    var id: String = "",
    var apellido: String? = null,
    var correo: String? = null,
    var esEntrenador: Boolean? = null,
    var fechaAlt: LocalDateTime? = null,
    var fechaNac: LocalDateTime? = null,
    var nivel: Int? = 0,
    var nombre: String? = null,
    var pass: String? = null,
    var user: String? = null,
)