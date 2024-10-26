package bbdd

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import modelo.pojos.Historico
import modelo.pojos.Usuario
import modelo.pojos.Workout


class UsuariosNotFoundException(message: String) : Exception(message)
class GestorDeUsuarios {

    val db = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun obtenerUsuarios() { //: List<Usuario> Esto sirve para devolver una lista de users

        db.collection("Usuarios")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }



    }

}