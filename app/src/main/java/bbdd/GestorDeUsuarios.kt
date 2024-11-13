package bbdd

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getString
import com.example.epicfitapp.LoginActivity
import com.example.epicfitapp.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import modelo.pojos.Usuario
import modelo.pojos.UsuarioLogueado

class GestorDeUsuarios {

    private val db = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    fun comprobarUsuario(username: String, password: String, callback: (Usuario?) -> Unit) {
        val usernameLowercase = username.lowercase() //user lo convertimos a minúsculas
        db.collection("Usuarios")
            .whereEqualTo("usuario", usernameLowercase)
            .whereEqualTo("pass", password)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d(LoginActivity.TAG, "Usuario no encontrado o contraseña incorrecta.")
                    //Toast.makeText(this, "Usuario no encontrado o contraseña incorrecta.", Toast.LENGTH_SHORT).show()
                    callback(null)
                } else {
                    result.documents.firstOrNull()?.let { document ->
                        val usuarioEncontrado = document.toObject(Usuario::class.java)
                        usuarioEncontrado?.id = document.id

                        // Asignar el usuario a UsuarioLogueado
                        UsuarioLogueado.usuario = usuarioEncontrado

                        Log.d(
                            LoginActivity.TAG,
                            "Usuario encontrado: ${usuarioEncontrado?.usuario}"
                        )
                        //Toast.makeText(this, "Bienvenido ${usuario.user}", Toast.LENGTH_SHORT).show()
                        callback(usuarioEncontrado)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(LoginActivity.TAG, "Error obteniendo documentos.", exception)
                //Toast.makeText(this, "Error al obtener usuarios.", Toast.LENGTH_SHORT).show()
                callback(null)
            }
    }

    fun recordarUsuario(context: Context, username: String, password: String) {
        val sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("username", username)
            putString("password", password)
            putBoolean("rememberMe", true)
            apply()
        }
    }

    fun cargarDatosInicio(
        context: Context,
        user: EditText,
        pass: EditText,
        rememberMeCheckBox: CheckBox
    ) {
        val sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)

        if (rememberMe) {
            val username = sharedPreferences.getString("username", "")
            val password = sharedPreferences.getString("password", "")

            // Si las credenciales están guardadas, las ponemos en los EditText
            user.setText(username)
            pass.setText(password)
            rememberMeCheckBox.isChecked = true
        }
    }

    fun borrarUsuarioRecordado(context: Context) {
        val sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("username")
            remove("password")
            //se setea a falso para que no cargue las credenciales el siguiente login
            putBoolean("rememberMe", false)
            apply()
        }
    }

    fun guardarUsuario(nuevoUsuario: Usuario, context: Context) {
        db.collection("Usuarios")
            .add(nuevoUsuario)
            .addOnSuccessListener { documentReference ->
                nuevoUsuario.id = documentReference.id // Asigna el ID generado por Firebase
                Toast.makeText(context, "@${nuevoUsuario.usuario} " + getString(context, R.string.registro_msg_exito), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText( context, getString(context, R.string.registro_err), Toast.LENGTH_SHORT ).show()
            }
    }

    fun comprobarSiExisteNombreUsuario(
        username: String
    ): Boolean {
        var siExisteUsuario = false
        db.collection("usuarios")
            .whereEqualTo("usuario", username)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Si la consulta devuelve algún documento, significa que el usuario ya existe
                    siExisteUsuario = true
                }
            }
        return siExisteUsuario
    }

}

