package adaptadores

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.epicfitapp.R
import modelo.pojos.Historico
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.RequiresApi
import utils.DateUtils

class HistoricoAdapter(private val context: Context?, private val historicos: List<Historico>) :
    RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    class HistoricoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.nombreTxt)
        val nivel: TextView = view.findViewById(R.id.nivelTxt)
        val tiempo: TextView = view.findViewById(R.id.tiempoTxt)
        val tiempoPrev: TextView = view.findViewById(R.id.tiempoPrevTxt)
        val fecha: TextView = view.findViewById(R.id.fechaTxt)
        val porcentaje: TextView = view.findViewById(R.id.porcentajeTxt)
        val imagen: ImageView = view.findViewById(R.id.imagen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false)
        return HistoricoViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val historico = historicos[position]
        holder.nombre.text = historico.workout?.nombre
        holder.nivel.text = "Nivel: " + historico.workout?.nivel.toString()
        holder.tiempoPrev.text = historico.workout?.tiempo?.let { DateUtils.formatearTiempo(it) }
        holder.fecha.text = historico.fecha?.let { DateUtils.formatearFecha(it) }
        holder.tiempo.text = historico.tiempo?.let { DateUtils.formatearTiempo(it) }
        holder.porcentaje.text = historico.porcentaje.toString() + "%"

        if (historico.workout?.tipo == "brazo"){
            holder.imagen.setImageResource(R.drawable.brazo)
        } else if(historico.workout?.tipo == "pecho"){
            holder.imagen.setImageResource(R.drawable.pecho)
        } else {
            holder.imagen.setImageResource(R.drawable.logo)
        }

    }

    override fun getItemCount(): Int {
        return historicos.size
    }

}