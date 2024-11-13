package adaptadores

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import bbdd.GestorDeWorkouts
import com.example.epicfitapp.EntrenadorActivity
import utils.DateUtils

class WorkoutsAdapter(
    private val context: EntrenadorActivity,
    private var workouts: List<Workout>
) :
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

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]

        holder.nombre.text = workout.nombre
        holder.nivel.text = context.getString(R.string.nivel) + " " + workout.nivel.toString()
        val tiempoPrevisto = workout.tiempo?.let { DateUtils.formatearTiempo(it) }
        holder.tiempoPrev.text = "${context.getString(R.string.tiempo_previsto)}: ${
            tiempoPrevisto ?: context.getString(R.string.desconocido)
        } min"
        holder.descripcion.text = "${context.getString(R.string.descripcion_workout)} ${
            workout.tipo ?: context.getString(R.string.desconocido)
        }"

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

        holder.layoutTexto.setOnClickListener {
            mostrarDialogEjercicios(workout)
        }

        val videoUrl = workout.workoutObj?.video
        if (!videoUrl.isNullOrEmpty()) {
            holder.btnPlay.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                context.startActivity(intent)
            }
            holder.btnPlay.isVisible = true
        } else {
            holder.btnPlay.isVisible = false
        }

        holder.btnModificar.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(context.getString(R.string.workout_pregunta_modificar))
                .setPositiveButton(context.getString(R.string.si)) { _, _ ->
                    workout.id?.let {
                        mostrarDialogoModificarWorkout(workout)
                        notifyDataSetChanged()
                    }
                }
                .setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()
        }

        holder.btnBorrar.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(context.getString(R.string.workout_pregunta_borrar))
                .setPositiveButton(context.getString(R.string.si)) { _, _ ->
                    val gdw = GestorDeWorkouts()
                    workout.id?.let { it1 ->
                        gdw.borrarWorkout(it1,
                            onSuccess = {
                                workouts = workouts.filter { it.id != workout.id }
                                notifyDataSetChanged()
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.workout_borrar_exito),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFailure = {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.workout_borrar_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
                .setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()
        }

        holder.imagen.setOnClickListener {
            if (!videoUrl.isNullOrEmpty()) {
                // Abrir el enlace del video
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                context.startActivity(intent)
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

    private fun mostrarDialogoModificarWorkout(workout: Workout) {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        val nombreInput = EditText(context).apply {
            hint = getString(context, R.string.nombre_hint)
            setText(workout.nombre)
        }
        val nivelInput = EditText(context).apply {
            hint = getString(context, R.string.nivel_hint)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(workout.nivel.toString())
        }
        val tiempoInput = EditText(context).apply {
            hint = getString(context, R.string.tiempo_hint)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(workout.tiempo.toString())
        }
        val videoInput = EditText(context).apply {
            hint = getString(context, R.string.video_hint)
            setText(workout.video)
        }
        val tipoInput = EditText(context).apply {
            hint = getString(context, R.string.tipo_hint)
            setText(workout.tipo)
        }
        layout.addView(nombreInput)
        layout.addView(nivelInput)
        layout.addView(tiempoInput)
        layout.addView(videoInput)
        layout.addView(tipoInput)

        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle(getString(context, R.string.modificar_workout_titulo))
            .setView(layout)
            .setPositiveButton(getString(context, R.string.guardar)) { dialog, _ ->
                val workoutModificado = Workout(
                    id = workout.id,
                    nombre = nombreInput.text.toString(),
                    nivel = nivelInput.text.toString().toIntOrNull() ?: 0,
                    tiempo = tiempoInput.text.toString().toIntOrNull() ?: 0,
                    video = videoInput.text.toString(),
                    tipo = tipoInput.text.toString()
                )
                val gestorDeWorkouts = GestorDeWorkouts()
                workout.id?.let { id ->
                    gestorDeWorkouts.actualizarWorkout(
                        workoutModificado,
                        onSuccess = {
                            workouts = workouts.map { if (it.id == id) workoutModificado else it }
                            Toast.makeText(
                                context,
                                getString(context, R.string.workout_actualizado),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onFailure = {
                            Toast.makeText(
                                context,
                                getString(context, R.string.error_actualizar_workout),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(context, R.string.cancelar)) { dialog, _ ->
                dialog.dismiss()

            }
        val dialog = dialogBuilder.create()
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