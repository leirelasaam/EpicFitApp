package modelo.pojos

import com.google.firebase.Timestamp

class Usuario(
    var id: String? = "",
    var apellido: String? = null,
    var correo: String? = null,
    var esEntrenador: Boolean? = null,
    var fechaAlt: Timestamp? = null,
    var fechaNac: Timestamp? = null,
    var nivel: Int? = 0,
    var nombre: String? = null,
    var pass: String? = null,
    var usuario: String? = null,
){
    override fun toString(): String {
        return "Usuario(id='$id', nombre=$nombre, apellido=$apellido, correo=$correo, " +
                "esEntrenador=$esEntrenador, fechaAlt=$fechaAlt, fechaNac=$fechaNac, " +
                "nivel=$nivel, usuario=$usuario)"
    }
}