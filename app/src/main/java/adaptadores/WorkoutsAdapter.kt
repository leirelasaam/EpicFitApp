package adaptadores

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.epicfitapp.R
import modelo.pojos.Workout
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import bbdd.GestorDeWorkouts

class WorkoutsAdapter(private val context: Context?, private var workouts: List<Workout>) :
    RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.nombreTxt)
        val nivel: TextView = view.findViewById(R.id.nivelTxt)
        val tiempoPrev: TextView = view.findViewById(R.id.tiempoPrevTxt)
        val imagen: ImageView = view.findViewById(R.id.imagen)
        val layoutTexto: LinearLayout = view.findViewById(R.id.layoutVerticalWorkout)
        val btnPlay: ImageView = view.findViewById(R.id.btnPlay)
        val btnModificar: ImageView = view.findViewById(R.id.btnModificar)
        val btnBorrar: ImageView = view.findViewById(R.id.btnBorrar)
        val descripcion: TextView = view.findViewById(R.id.descripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_entrenador, parent, false)
        return WorkoutViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]

        holder.nombre.text = workout.nombre
        holder.nivel.text = context?.getString(R.string.nivel) + " " + workout.nivel.toString()
        holder.tiempoPrev.text = workout.tiempo.toString() + " min"
        holder.descripcion.text = "Descripcion workout para: ${workout.tipo ?: "desconocido"}"

        when (workout.tipo) {
            "triceps" -> holder.imagen.setImageResource(R.drawable.triceps)
            "biceps" -> holder.imagen.setImageResource(R.drawable.biceps)
            "espalda" -> holder.imagen.setImageResource(R.drawable.espalda)
            "gluteos" -> holder.imagen.setImageResource(R.drawable.gluteos)
            "piernas" -> holder.imagen.setImageResource(R.drawable.piernas)
            "abdominales" -> holder.imagen.setImageResource(R.drawable.abs)
            "pecho" -> holder.imagen.setImageResource(R.drawable.pecho)
            else -> holder.imagen.setImageResource(R.drawable.ejercicio)
        }

        holder.layoutTexto.setOnClickListener{
            mostrarDialogEjercicios(workout)
        }
        holder.btnModificar.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("¿Estás seguro de que deseas modificar este workout?")
                .setPositiveButton("Sí") { dialog, id ->
                    workout.id?.let { workoutId ->
                        // No puedo llamar a la funcion para modificar los workouts
                        //context.mostrarDialogoModificarWorkout(workoutId)
                    }
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            builder.create().show()
        }

        holder.btnBorrar.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("¿Estás seguro de que deseas borrar este workout?")
                // Dialogo de confirmacion
                .setPositiveButton("Sí") { dialog, id ->
                    val gdw = GestorDeWorkouts()
                    workout.id?.let { it1 ->
                        gdw.borrarWorkout(it1,
                            onSuccess = {
                                // Elimina el workout de la lista
                                workouts = workouts.filter { it.id != workout.id }
                                notifyDataSetChanged() // Actualizar el RecyclerView
                                Toast.makeText(context, "Workout borrado con éxito", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { exception ->
                                Log.e("WorkoutsAdapter", "Error al borrar workout: ${exception.message}")
                                Toast.makeText(context, "Error al borrar el workout", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            builder.create().show() // Mostrar el dialogo
        }

        holder.imagen.setOnClickListener {
            val videoUrl = workout.video
            if (!videoUrl.isNullOrEmpty()) {
                // Abrir el enlace del video
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                context?.startActivity(intent)
            }
        }
    }

    private fun mostrarDialogEjercicios(workout: Workout) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_ejercicios, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)

        val tituloWorkout = dialogView.findViewById<TextView>(R.id.tituloWorkout)
        tituloWorkout.text = workout.nombre

        val recyclerEjercicios = dialogView.findViewById<RecyclerView>(R.id.recyclerEjercicios)
        val ejercicios = workout.ejerciciosObj ?: emptyList()
        recyclerEjercicios.adapter = EjercicioAdapter(context, ejercicios)
        recyclerEjercicios.layoutManager = LinearLayoutManager(context)

        val dialog = dialogBuilder.create()

        val btnCerrar = dialogView.findViewById<Button>(R.id.btnAceptar)
        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    override fun getItemCount(): Int {
        return workouts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged()
    }
}
