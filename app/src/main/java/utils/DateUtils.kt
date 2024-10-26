package utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateUtils {
    companion object {
        @JvmStatic
        fun formatearTiempo(tiempoEnSegundos: Int): String {
            val horas = tiempoEnSegundos / 3600
            val minutos = (tiempoEnSegundos % 3600) / 60
            val segundos = tiempoEnSegundos % 60

            val partes = mutableListOf<String>()
            if (horas > 0) {
                partes.add("${horas}h")
            }
            if (minutos > 0) {
                partes.add("${minutos}min")
            }
            if (segundos > 0 || partes.isEmpty()) {
                partes.add("${segundos}s")
            }

            return partes.joinToString(" ")
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun formatearTimestamp(timestamp: Timestamp): String {
            val instant = timestamp.toDate().toInstant()
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

            val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            return localDateTime.format(formatoFecha)
        }
    }
}