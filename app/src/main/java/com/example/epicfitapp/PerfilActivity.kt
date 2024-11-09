package com.example.epicfitapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import modelo.pojos.UsuarioLogueado
import utils.DateUtils
import utils.DateUtils.Companion.formatearTimestamp

class PerfilActivity : BaseActivity() {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

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

        val usuarioActual = UsuarioLogueado.usuario

        if (usuarioActual != null) {
            (findViewById<EditText>(R.id.inputUsuario)).setText(usuarioActual.usuario)
            (findViewById<EditText>(R.id.inputNombre)).setText(usuarioActual.nombre)
            (findViewById<EditText>(R.id.inputApellidos)).setText(usuarioActual.apellido.toString())
            (findViewById<EditText>(R.id.inputEmail)).setText(usuarioActual.correo.toString())
            (findViewById<EditText>(R.id.inputFechaNacimiento)).setText(
                usuarioActual.fechaNac?.let { formatearTimestamp(it) })
        }

        val btnVolverWorkouts = findViewById<Button>(R.id.btn_volver)

        btnVolverWorkouts.setOnClickListener {
            //volver a historico
            startActivity(Intent(this, HistoricoActivity::class.java))
            finish()
        }
    }
}
