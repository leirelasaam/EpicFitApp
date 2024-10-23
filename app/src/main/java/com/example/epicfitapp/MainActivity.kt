package com.example.epicfitapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import modelo.pojos.Usuario

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        obtenerHistoricos(Usuario("zoVUUYKznIh8KDXOxjUc"))
    }

    private fun obtenerHistoricos(usuario: Usuario) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val collectionName = "Historicos"

        Log.d("MainActivity", "ID de usuario: ${usuario.id}")

        db.collection(collectionName).whereEqualTo("usuario", db.collection("Usuarios").document(usuario.id)) // Filtrar por referencia de usuario
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("FirestoreService", "No se encontraron historicos para el usuario.")
                } else {
                    for (document in documents) {
                        val workoutRef = document.getDocumentReference("workout")
                        val tiempo = document.getDouble("tiempo")?.toInt()
                        val porcentaje = document.getDouble("porcentaje")?.toInt()
                        val fecha = document.getDate("fecha")

                        Log.d(
                            "FirestoreService",
                            "Historico: $workoutRef\n $tiempo\n $porcentaje\n $fecha\n"
                        )
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.d("FirestoreService", "Error: " + e)
            }
    }
}


/*
private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
private val collectionName = "Usuarios"

private fun getAllUsers(onResult: (QuerySnapshot?) -> Unit) {
    db.collection(collectionName)
        .get()
        .addOnSuccessListener { result ->
            onResult(result)

            // Verificar si hay documentos en la consulta
            if (result != null && !result.isEmpty) {
                // Iterar sobre los documentos obtenidos
                for (document in result.documents) {
                    val userId = document.id
                    val userData = document.data

                    Log.d("FirestoreService", "User ID: $userId, Data: $userData")
                }
            } else {
                Log.d("FirestoreService", "No users found.")
            }
        }
        .addOnFailureListener { e ->
            Log.w("FirestoreService", "Error getting documents.", e)
            onResult(null)
        }
}

*/