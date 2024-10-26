package com.example.epicfitapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import modelo.pojos.Usuario
import modelo.pojos.UsuarioLogueado
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class LoginActivity : AppCompatActivity() {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los EditText y el botón
        val user = findViewById<EditText>(R.id.inputUser)
        val pass = findViewById<EditText>(R.id.inputPass)
        val btnIniciarSesion = findViewById<Button>(R.id.btn_IniciarSesion)

        btnIniciarSesion.setOnClickListener {
            if (user.text.isNotEmpty() && pass.text.isNotEmpty()) {
                //Toast.makeText(this, "User y pass introducidos", Toast.LENGTH_SHORT).show()
                comprobarUsuario(user.text.toString(), pass.text.toString()) { usuario ->
                    if (usuario != null) {
                        // Asignar el usuario a UsuarioLogueado
                        UsuarioLogueado.usuario = usuario

                        // Aquí puedes realizar otras acciones con el usuario logueado
                        println("Usuario logueado: ${usuario.nombre}")
                        Toast.makeText(this, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()

                        // Pasar a histórico tras login correcto
                        val intent = Intent(this, HistoricoActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun comprobarUsuario(username: String, password: String, callback: (Usuario?) -> Unit) {
        db.collection("Usuarios")
            .whereEqualTo("usuario", username)
            .whereEqualTo("pass", password)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d(TAG, "Usuario no encontrado o contraseña incorrecta.")
                    //Toast.makeText(this, "Usuario no encontrado o contraseña incorrecta.", Toast.LENGTH_SHORT).show()
                    callback(null)
                } else {
                    result.documents.firstOrNull()?.let { document ->
                        val usuario = Usuario().apply {
                            id = document.id
                            apellido = document.getString("apellido")
                            correo = document.getString("correo")
                            esEntrenador = document.getBoolean("esEntrenador")
                            fechaAlt = document.getDate("fechaAlt")?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                            fechaNac = document.getDate("fechaNac")?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                            nivel = document.getDouble("nivel")?.toInt() ?: 0
                            nombre = document.getString("nombre")
                            pass = document.getString("pass")
                            user = document.getString("usuario")
                        }

                        // Asignar el usuario a UsuarioLogueado
                        UsuarioLogueado.usuario = usuario

                        Log.d(TAG, "Usuario encontrado: ${usuario.user}")
                        //Toast.makeText(this, "Bienvenido ${usuario.user}", Toast.LENGTH_SHORT).show()
                        callback(usuario)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos.", exception)
                //Toast.makeText(this, "Error al obtener usuarios.", Toast.LENGTH_SHORT).show()
                callback(null)
            }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

    //Así se accede al usuario logueado desde cualquier parte del proyecto
    /*
    val usuarioActual = UsuarioLogueado.usuario
    if (usuarioActual != null) {
        // Haz algo con el usuario logueado
        println("Usuario logueado: ${usuarioActual.nombre}")
    }
    */
}
