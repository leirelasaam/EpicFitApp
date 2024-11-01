package com.example.epicfitapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import modelo.pojos.Usuario
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

class RegistroActivity : BaseActivity() {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var fechaNacimiento: Timestamp? = null // Variable para almacenar la fecha de nacimiento


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        // Obtener referencias de los elementos en el layout
        val nombreEditText: EditText = findViewById(R.id.inputNombre)
        val apellidoEditText: EditText = findViewById(R.id.inputApellido)
        val correoEditText: EditText = findViewById(R.id.inputCorreo)
        val passEditText: EditText = findViewById(R.id.inputPass)
        val pass2EditText: EditText = findViewById(R.id.inputPass2)
        val usuarioEditText: EditText = findViewById(R.id.inputUsuario)
        val tipoUsuario: Spinner = findViewById(R.id.spinner)
        val botonRegistro: Button = findViewById(R.id.botonRegistro)
        val fechaNac: EditText = findViewById(R.id.inputFechaNac)
        val botonVolver: Button = findViewById(R.id.botonVolver)


        /*BOTÓN VOLVER (a login)*/
        botonVolver.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        fechaNac.setOnClickListener {
            showDatePickerDialog(fechaNac)
        }

        // Configurar botón de registro
        botonRegistro.setOnClickListener {
            // Obtener los valores ingresados
            val nombre = nombreEditText.text.toString()
            val apellido = apellidoEditText.text.toString()
            val correo = correoEditText.text.toString()
            val pass = passEditText.text.toString()
            val pass2 = pass2EditText.text.toString()
            val usuario = usuarioEditText.text.toString()
            var esEntrenador = false;
            if (tipoUsuario.selectedItem.toString() == "Entrenador") {
                esEntrenador = true
            }
            val fechaAlt = Timestamp.now() // Fecha de alta como timestamp actual
            val nivel = 0

            // Crear instancia de Usuario
            val nuevoUsuario = Usuario(
                apellido = apellido,
                correo = correo,
                esEntrenador = esEntrenador,
                fechaAlt = fechaAlt,
                fechaNac = fechaNacimiento,
                nivel = nivel,
                nombre = nombre,
                pass = pass,
                usuario = usuario
            )

            val comprobarValidaciones = validacionesCamposCorrectos(nuevoUsuario, pass, pass2);
            if (comprobarValidaciones == true) {
                // Guarda el usuario en Firestore
                guardarUsuario(nuevoUsuario)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

            }

        }
    }

    private fun guardarUsuario(nuevoUsuario: Usuario) {
        db.collection("Usuarios")
            .add(nuevoUsuario)
            .addOnSuccessListener { documentReference ->
                nuevoUsuario.id = documentReference.id // Asigna el ID generado por Firebase
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Usuario guardado con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al registrar usuario: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Firestore", "Error al guardar usuario", e)
            }
    }

    // Función para mostrar el DatePickerDialog
    private fun showDatePickerDialog(fechaNacEditText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

/*  val datePickerDialog = DatePickerDialog(
        this,
        R.style.CustomDatePickerTheme,*/
        val datePickerDialog =
            DatePickerDialog(this, R.style.CustomDatePickerTheme,{ _, selectedYear, selectedMonth, selectedDay ->
                //Crear la fecha en Timestamp
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                fechaNacimiento = Timestamp(selectedCalendar.time)

                // Formato de la fecha para mostrar en el EditText
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                fechaNacEditText.setText(sdf.format(selectedCalendar.time))
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun validarNombre(nombre: String?): Boolean {
        return nombre != null && nombre.isNotEmpty() && nombre.length <= 25
    }

    private fun validarApellido(apellido: String?): Boolean {
        return apellido != null && apellido.isNotEmpty() && apellido.length <= 25
    }

    private fun validarCorreo(correo: String?): Boolean {
        val regexCorreo = "^[\\w-.]+@[\\w-]+\\.[a-z]{2,6}$"
        return correo != null && Pattern.matches(regexCorreo, correo)
    }

    private fun validarUsername(user: String?): Boolean {
        val regexUser = "^[a-z]{2,15}$"
        return user != null && Pattern.matches(regexUser, user)
    }

    private fun validarPassword(pass: String?): Boolean {
        val regexPass = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!_]).{8,20}$"
        return pass != null && Pattern.matches(regexPass, pass)
    }

    fun comprobarSiExisteNombreUsuario(
        username: String
    ): Boolean {
        var siExisteUsuario = false;
        db.collection("usuarios")
            .whereEqualTo("usuario", username)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result: QuerySnapshot? = task.result
                    // Si la consulta devuelve algún documento, significa que el usuario ya existe
                    siExisteUsuario = true
                }
            }
        return siExisteUsuario;
    }

    fun validacionesCamposCorrectos(
        usuario: Usuario,
        pass1: String,
        pass2: String
    ): Boolean {
        var validar = true

        if (!validarApellido(usuario.apellido)) {
            Toast.makeText(
                this,
                "El apellido está vacío o es mayor de 50 caracteres",
                Toast.LENGTH_LONG
            ).show()
            validar = false
        } else if (!validarCorreo(usuario.correo)) {
            Toast.makeText(this, "Correo incorrecto, vuelva a insertarlo.", Toast.LENGTH_LONG)
                .show()
            validar = false
            /*} else if (!validarFechaNacimiento(usuario.fechaNac)) {
                Toast.makeText(this, "Fecha de nacimiento incorrecta. El usuario debe ser mayor de 14 años.", Toast.LENGTH_LONG).show()
                validar = false*/
        } else if (!validarNombre(usuario.nombre)) {
            Toast.makeText(
                this,
                "Nombre incorrecto, está vacío o es mayor de 25 caracteres.",
                Toast.LENGTH_LONG
            ).show()
            validar = false
        } else if (!validarPassword(usuario.pass)) {
            Toast.makeText(
                this,
                "Contraseña incorrecta, debe tener entre 8 y 20 caracteres, incluir al menos una letra minúscula, una mayúscula, un número y un carácter especial.",
                Toast.LENGTH_LONG
            ).show()
            validar = false
        } else if (pass1 != pass2) {
            Toast.makeText(
                this,
                "Las contraseñas no coinciden, vuelva a intentarlo.",
                Toast.LENGTH_LONG
            ).show()
            validar = false
        } else if (!validarUsername(usuario.usuario)) {
            Toast.makeText(
                this,
                "Usuario incorrecto, debe contener solo letras minúsculas.",
                Toast.LENGTH_LONG
            ).show()
            validar = false
        } else if (comprobarSiExisteNombreUsuario(usuario.usuario.toString())) {
            Toast.makeText(this, "El nombre de usuario ya existe.", Toast.LENGTH_LONG).show()
            validar = false
        }

        return validar
    }

}


