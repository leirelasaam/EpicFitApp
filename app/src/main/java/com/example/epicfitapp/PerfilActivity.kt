package com.example.epicfitapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import modelo.pojos.UsuarioLogueado
import utils.DateUtils.Companion.formatearTimestamp
import java.util.Locale

@Suppress("DEPRECATION")
class PerfilActivity : BaseActivity() {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.perfil)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cargar y mostrar los datos del usuario
        val usuarioActual = UsuarioLogueado.usuario
        if (usuarioActual != null) {
            findViewById<EditText>(R.id.inputUsuario).setText(usuarioActual.usuario)
            findViewById<EditText>(R.id.inputNombre).setText(usuarioActual.nombre)
            findViewById<EditText>(R.id.inputApellidos).setText(usuarioActual.apellido.toString())
            findViewById<EditText>(R.id.inputEmail).setText(usuarioActual.correo.toString())
            findViewById<EditText>(R.id.inputFechaNacimiento).setText(
                usuarioActual.fechaNac?.let { formatearTimestamp(it) }
            )
        }

        //SPINNER IDIOMAS
        configurarSpinnerIdiomas()
    }

    private fun configurarSpinnerIdiomas() {
        val languageSpinner: Spinner = findViewById(R.id.spinnerIdiomas)
        val languages = arrayOf("Español", "English")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        val currentLanguage = getLocale()
        languageSpinner.setSelection(if (currentLanguage == "es") 0 else 1)


        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val newLanguageCode = if (position == 0) "es" else "en"
                if (newLanguageCode != getLocale()) {
                    setLocale(newLanguageCode)
                    actualizarTexto() // Actualizar textos en tiempo real
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        // Guardar el idioma en SharedPreferences
        val editor: SharedPreferences.Editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("idioma_app", languageCode)
        editor.apply()
    }

    private fun loadLocale() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("idioma_app", "en") ?: "en"
        setLocale(languageCode)
    }

    private fun getLocale(): String {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("idioma_app", "en") ?: "en"
    }

    // Método para actualizar los textos visibles en la pantalla sin reiniciar la actividad
    private fun actualizarTexto() {
        findViewById<EditText>(R.id.inputUsuario).hint = getString(R.string.usuario)
        findViewById<EditText>(R.id.inputNombre).hint = getString(R.string.nombre)
        findViewById<EditText>(R.id.inputApellidos).hint = getString(R.string.apellidos)
        findViewById<EditText>(R.id.inputEmail).hint = getString(R.string.email)
        findViewById<EditText>(R.id.inputFechaNacimiento).hint = getString(R.string.fecha_nacimiento)

    }
}

