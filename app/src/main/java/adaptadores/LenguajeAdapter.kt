package adaptadores


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class LenguajeAdapter (
    private val lenguajes: List<String>,
    private val onLenguajeSelected: (String) -> Unit
) : RecyclerView.Adapter<LenguajeAdapter.LenguajeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LenguajeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return LenguajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: LenguajeViewHolder, position: Int) {
        val lenguaje = lenguajes[position]
        holder.bind(lenguaje)
        holder.itemView.setOnClickListener {
            onLenguajeSelected(lenguaje)
        }
    }

    override fun getItemCount() = lenguajes.size

    class LenguajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(lenguaje: String) {
            (itemView as TextView).text = lenguaje
        }
    }
}