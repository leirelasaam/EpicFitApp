package com.example.epicfitapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import modelo.pojos.Usuario
import java.text.SimpleDateFormat
import java.util.Locale

class RegistroActivity : AppCompatActivity() {
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
            if(tipoUsuario.selectedItem.toString() == "Entrenador"){
                esEntrenador = true
            }
            val fechaAlt = Timestamp.now() // Fecha de alta como timestamp actual
            val nivel = 0 // Puedes cambiar el nivel por defecto según tu lógica

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

            // Guarda el usuario en Firestore
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
    }

    // Función para mostrar el DatePickerDialog
    private fun showDatePickerDialog(fechaNacEditText: EditText){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Crear la fecha en Timestamp a partir de la fecha seleccionada
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
            fechaNacimiento = Timestamp(selectedCalendar.time)

            // Formato de la fecha para mostrar en el EditText
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            fechaNacEditText.setText(sdf.format(selectedCalendar.time))
        }, year, month, day)

        datePickerDialog.show()
    }
}


