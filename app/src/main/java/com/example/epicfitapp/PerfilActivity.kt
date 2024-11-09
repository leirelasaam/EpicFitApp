package com.example.epicfitapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import modelo.pojos.UsuarioLogueado

class PerfilActivity : BaseActivity() {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var user: EditText
    private lateinit var nombre: EditText
    private lateinit var apellidos: EditText
    private lateinit var email: EditText
    private lateinit var fechaNac: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.perfil)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar los EditText
        user = findViewById(R.id.inputUsuario)
        nombre = findViewById(R.id.inputNombre)
        apellidos = findViewById(R.id.inputApellidos)
        email = findViewById(R.id.inputEmail)
        fechaNac = findViewById(R.id.inputFechaNacimiento)

        // Asumimos que UsuarioLogueado.usuario contiene el usuario logueado
        db.collection("Usuarios")
            .whereEqualTo("usuario", UsuarioLogueado.usuario)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d(TAG, "Usuario no encontrado.")
                    //Toast.makeText(this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
                } else {
                    // Extraemos los datos del documento
                    result.documents.firstOrNull()?.let { document ->
                        user.setText(document.getString("usuario"))
                        nombre.setText(document.getString("nombre"))
                        apellidos.setText(document.getString("apellidos"))
                        email.setText(document.getString("email"))
                        fechaNac.setText(document.getString("fechaNacimiento"))
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos.", exception)
                //Toast.makeText(this, "Error al obtener usuarios.", Toast.LENGTH_SHORT).show()
            }

        val btnVolverWorkouts = findViewById<Button>(R.id.btn_volver)

        btnVolverWorkouts.setOnClickListener {
            //volver a workouts
            Intent(this, HistoricoActivity::class.java)
            startActivity(intent)
        }
    }
}
