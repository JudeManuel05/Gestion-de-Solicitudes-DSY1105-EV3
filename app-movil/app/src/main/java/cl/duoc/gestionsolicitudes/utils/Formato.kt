package cl.duoc.gestionsolicitudes.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatearFecha(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(millis))

/** 41405.45 -> "$41.405,45" (formato chileno). */
fun formatearPesos(valor: Double): String {
    val formato = NumberFormat.getNumberInstance(Locale("es", "CL")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "$" + formato.format(valor)
}
