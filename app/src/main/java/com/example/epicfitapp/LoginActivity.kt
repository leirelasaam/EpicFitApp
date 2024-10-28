package com.example.epicfitapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
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

class LoginActivity : BaseActivity() {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var user: EditText
    private lateinit var pass: EditText

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = findViewById<EditText>(R.id.inputUser)
        pass = findViewById<EditText>(R.id.inputPass)

        // Cargar datos de inicio si la casilla de rememberMe está marcada y ha habido antes un login exitoso
        cargarDatosInicio()

        // Referencias a los EditText y el botón
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
                        Toast.makeText(this, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT)
                            .show()

                        // Guarda credenciales de inicio si rememberMe seleccionado
                        if (rememberMeCheckBox.isChecked) {
                            recordarUsuario(user.text.toString(), pass.text.toString())
                        }
                        else{
                            //Si rememberMe no está seleccionado se borra las credenciales de inicio
                            borrarUsuarioRecordado()
                        }

                        // Redirige a la actividad dependiendo si es entrenador o no
                        val intent = if (true == usuario.esEntrenador) {
                            Intent(this, EntrenadorActivity::class.java)
                        } else {
                            Intent(this, HistoricoActivity::class.java)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnIniciarSesion.setOnClickListener {
            //Comentado porque todavia no está el RegisterActivity
            //Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun comprobarUsuario(username: String, password: String, callback: (Usuario?) -> Unit) {
        val usernameLowercase = username.lowercase() //user lo convertimos a minúsculas
        db.collection("Usuarios")
            .whereEqualTo("usuario", usernameLowercase)
            .whereEqualTo("pass", password)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d(TAG, "Usuario no encontrado o contraseña incorrecta.")
                    //Toast.makeText(this, "Usuario no encontrado o contraseña incorrecta.", Toast.LENGTH_SHORT).show()
                    callback(null)
                } else {
                    result.documents.firstOrNull()?.let { document ->
                        val usuarioEncontrado = document.toObject(Usuario::class.java)
                        usuarioEncontrado?.id = document.id

                        // Asignar el usuario a UsuarioLogueado
                        UsuarioLogueado.usuario = usuarioEncontrado

                        Log.d(TAG, "Usuario encontrado: ${usuarioEncontrado?.usuario}")
                        //Toast.makeText(this, "Bienvenido ${usuario.user}", Toast.LENGTH_SHORT).show()
                        callback(usuarioEncontrado)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos.", exception)
                //Toast.makeText(this, "Error al obtener usuarios.", Toast.LENGTH_SHORT).show()
                callback(null)
            }
    }

    private fun recordarUsuario(username: String, password: String) {
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("username", username)
            putString("password", password)
            putBoolean("rememberMe", true)
            apply()
        }
    }

    private fun cargarDatosInicio() {
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
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

    private fun borrarUsuarioRecordado() {
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("username")
            remove("password")
            //se setea a falso para que no cargue las credenciales el siguiente login
            putBoolean("rememberMe", false)
            apply()
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
