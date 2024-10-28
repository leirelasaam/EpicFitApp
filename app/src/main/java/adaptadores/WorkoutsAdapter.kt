package adaptadores

import android.content.Context
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

class WorkoutsAdapter(private val context: Context?, private var workouts: List<Workout>) :
    RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.nombreTxt)
        val nivel: TextView = view.findViewById(R.id.nivelTxt)
        val tiempo: TextView = view.findViewById(R.id.tiempoTxt)
        val imagen: ImageView = view.findViewById(R.id.imagen)
        // Agrega aquí otros TextViews o elementos que quieras incluir
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.nombre.text = workout.nombre
        holder.nivel.text = context?.getString(R.string.nivel) + " " + workout.nivel.toString()
        holder.tiempo.text = workout.tiempo.toString() + " min"

        // Aquí puedes manejar la imagen si tienes algún tipo relacionado
        when (workout.tipo) {
            "brazo" -> holder.imagen.setImageResource(R.drawable.brazo) // Cambia por tu recurso
            "pecho" -> holder.imagen.setImageResource(R.drawable.pecho) // Cambia por tu recurso
            else -> holder.imagen.setImageResource(R.drawable.logo) // Cambia por tu recurso
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

    override fun getItemCount(): Int {
        return workouts.size
    }

    fun updateData(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged()
    }
}
