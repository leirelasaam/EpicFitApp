package bbdd

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.regex.Pattern


class GestorDeUsuarios {

    val db = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerUsuarios() { //: List<Usuario> Esto sirve para devolver una lista de users

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

