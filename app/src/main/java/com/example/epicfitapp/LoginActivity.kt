package com.example.epicfitapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import modelo.pojos.Usuario
import java.time.LocalDateTime

class LoginActivity : AppCompatActivity() {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

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
                Toast.makeText(this, "User y pass introducidos", Toast.LENGTH_SHORT).show()
                comprobarUsuario(user.text.toString(), pass.text.toString())
            } else {
                Toast.makeText(this, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun comprobarUsuario(username: String, password: String) {
        val usuario = Usuario()
        db.collection("Usuarios")
            .whereEqualTo("usuario", username) // Filtrar por el campo "usuario"
            .whereEqualTo("pass", password) // Filtrar por el campo "pass"
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d(TAG, "Usuario no encontrado o contraseña incorrecta.")
                    Toast.makeText(this, "Usuario no encontrado o contraseña incorrecta.", Toast.LENGTH_SHORT).show()
                } else {
                    for (document in result.documents) {
                        // Aquí puedes devolver el usuario encontrado o realizar otra acción

                        val id = document.getId()
                        val apellido = document.getString("apellido")
                        val correo = document.getString("correo")
                        val esEntrenador = document.getBoolean("esEntrenador")
                        val fechaAlt = document.getDate("fechaAlt")
                        val fechaNac = document.getDate("fechaNac")
                        val nivel = document.getDouble("nivel")
                        val nombre = document.getString("nombre")
                        val pass = document.getString("pass")
                        val user = document.getString("usuario")

                            Toast.makeText(this, "Login correcto, $usuario, $pass", Toast.LENGTH_SHORT).show()

                        usuario.id = id
                        usuario.apellido = apellido
                        usuario.correo = correo
                        usuario.esEntrenador = esEntrenador
                        usuario.fechaAlt  = fechaAlt
                        usuario.fechaNac = fechaNac
                        usuario.nivel = nivel
                        usuario.nombre = nombre
                        usuario.pass = pass
                        usuario.user = user


                        Log.d(TAG, "Usuario encontrado: ${document.id} => ${document.data}")
                        Toast.makeText(this, "Bienvenido ${document.data?.get("usuario")}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos.", exception)
                Toast.makeText(this, "Error al obtener usuarios.", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
