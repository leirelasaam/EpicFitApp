package adaptadores

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epicfitapp.R
import modelo.pojos.Historico
import utils.DateUtils


class HistoricoAdapter(private val context: Context?, private var historicos: List<Historico>) :
    RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    class HistoricoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.nombreTxt)
        val nivel: TextView = view.findViewById(R.id.nivelTxt)
        val tiempo: TextView = view.findViewById(R.id.tiempoTxt)
        val tiempoPrev: TextView = view.findViewById(R.id.tiempoPrevTxt)
        val fecha: TextView = view.findViewById(R.id.fechaTxt)
        val porcentaje: TextView = view.findViewById(R.id.porcentajeTxt)
        val imagen: ImageView = view.findViewById(R.id.imagen)
        val layoutTexto: LinearLayout = view.findViewById(R.id.layoutVerticalWorkout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false)
        return HistoricoViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val historico = historicos[position]
        holder.nombre.text = historico.workoutObj?.nombre
        holder.nivel.text = context?.getString(R.string.nivel) + " " + historico.workoutObj?.nivel.toString()
        holder.tiempoPrev.text = "(" + historico.workoutObj?.tiempo?.let { DateUtils.formatearTiempo(it) } + ")"
        holder.fecha.text = historico.fecha?.let { DateUtils.formatearTimestamp(it) }
        holder.tiempo.text = historico.tiempo?.let { DateUtils.formatearTiempo(it) }
        holder.porcentaje.text = historico.porcentaje.toString() + "%"

        when (historico.workoutObj?.tipo) {
            "brazo" -> holder.imagen.setImageResource(if (historico.workoutObj?.video != null) R.drawable.brazo else R.drawable.brazo_off)
            "pecho" -> holder.imagen.setImageResource(if (historico.workoutObj?.video != null) R.drawable.pecho else R.drawable.pecho_off)
            else -> holder.imagen.setImageResource(R.drawable.logo)
        }

        Log.d("ADAPTER", "Fecha formateada: ${historico.fecha?.let { DateUtils.formatearTimestamp(it) }}")
        Log.d("ADAPTER", "Historico: ${historico.toString()}")

        holder.layoutTexto.setOnClickListener{
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_ejercicios, null)
            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)

            val tituloWorkout = dialogView.findViewById<TextView>(R.id.tituloWorkout)
            tituloWorkout.text = historico.workoutObj?.nombre

            val recyclerEjercicios = dialogView.findViewById<RecyclerView>(R.id.recyclerEjercicios)
            val ejercicios = historico.workoutObj?.ejerciciosObj ?: emptyList()
            recyclerEjercicios.adapter = EjercicioAdapter(context, ejercicios)
            recyclerEjercicios.layoutManager = LinearLayoutManager(context)

            val dialog = dialogBuilder.create()

            val btnCerrar = dialogView.findViewById<Button>(R.id.btnAceptar)
            btnCerrar.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        holder.imagen.setOnClickListener {
            val videoUrl = historico.workoutObj?.video
            if (!videoUrl.isNullOrEmpty()) {
                // Abrir el enlace del video
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                context?.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return historicos.size
    }

    fun updateData(newHistoricos: List<Historico>) {
        historicos = newHistoricos
        notifyDataSetChanged()
    }

}