package com.example.epicfitapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bbdd.GestorDeUsuarios
import modelo.pojos.UsuarioLogueado

class LoginActivity : BaseActivity() {

    //private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
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

        rememberMeCheckBox =
            findViewById(R.id.inputMantenerSesion) // Ajusta el ID según corresponda
        val gdu = GestorDeUsuarios()

        // Obtener los datos pasados desde RegistroActivity
        val userReg = intent.getStringExtra("user")
        val passReg = intent.getStringExtra("pass")

        user = findViewById(R.id.inputUser)
        pass = findViewById(R.id.inputPass)

        if (userReg != null && passReg != null) {
            user.setText(userReg)
            pass.setText(passReg)
        }

        // Cargar datos de inicio si la casilla de rememberMe está marcada y ha habido antes un login exitoso
        gdu.cargarDatosInicio(this, user, pass, rememberMeCheckBox)

        // Referencias a los EditText y el botón
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        // Referencia al botón de registro
        val btnRegistrarme: Button = findViewById(R.id.btnRegistrarme)


        btnIniciarSesion.setOnClickListener {
            if (user.text.isNotEmpty() && pass.text.isNotEmpty()) {
                //Toast.makeText(this, "User y pass introducidos", Toast.LENGTH_SHORT).show()
                gdu.comprobarUsuario(user.text.toString(), pass.text.toString()) { usuario ->
                    if (usuario != null) {
                        // Asignar el usuario a UsuarioLogueado
                        UsuarioLogueado.usuario = usuario

                        // Aquí puedes realizar otras acciones con el usuario logueado
                        println("Usuario logueado: ${usuario.nombre}")
                        Toast.makeText(this, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT)
                            .show()

                        // Guarda credenciales de inicio si rememberMe seleccionado
                        if (rememberMeCheckBox.isChecked) {
                            gdu.recordarUsuario(this, user.text.toString(), pass.text.toString())
                        } else {
                            //Si rememberMe no está seleccionado se borra las credenciales de inicio
                            gdu.borrarUsuarioRecordado(this)
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
        // Configurar el intent para ir a RegistroActivity
        btnRegistrarme.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)

        }
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}
