package adaptadores

import adaptadores.HistoricoAdapter.HistoricoViewHolder
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.epicfitapp.R
import modelo.pojos.Ejercicio
import utils.DateUtils

class EjercicioAdapter (private val context: Context?, private var ejercicios: List<Ejercicio>) :
        RecyclerView.Adapter<EjercicioAdapter.EjercicioViewHolder>() {

        class EjercicioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nombre: TextView = view.findViewById(R.id.nombreEjTxt)
            val series: TextView = view.findViewById(R.id.seriesEjTxt)
            val tiempo: TextView = view.findViewById(R.id.tiempoEjTxt)
            val repeticiones: TextView = view.findViewById(R.id.repeticionesEjTxt)
            val descanso: TextView = view.findViewById(R.id.descansoEjTxt)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EjercicioViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_ejercicio, parent, false)
        return EjercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EjercicioViewHolder, position: Int) {
        val ejercicio = ejercicios[position]
        holder.nombre.text = ejercicio.nombre
        holder.series.text = ejercicio.series.toString()
        holder.tiempo.text = ejercicio.tiempoSerie?.let { DateUtils.formatearTiempo(it) }
        holder.repeticiones.text = ejercicio.repeticiones.toString()
        holder.descanso.text = ejercicio.descanso?.let { DateUtils.formatearTiempo(it) }
    }

    override fun getItemCount(): Int {
        return ejercicios.size
    }
}