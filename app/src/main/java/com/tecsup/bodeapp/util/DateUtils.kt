package com.tecsup.bodeapp.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "PE"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("es", "PE"))
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "PE"))

    // Formatear timestamp a fecha legible
    fun Long.toFechaString(): String {
        return dateFormat.format(Date(this))
    }

    // Formatear timestamp a hora legible
    fun Long.toHoraString(): String {
        return timeFormat.format(Date(this))
    }

    // Formatear timestamp a fecha y hora
    fun Long.toFechaHoraString(): String {
        return dateTimeFormat.format(Date(this))
    }

    // Formatear monto a moneda peruana
    fun Double.toMoneda(): String {
        val formato = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
        return formato.format(this)
    }

    // Obtener inicio del día actual (00:00:00)
    fun obtenerInicioDelDia(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Obtener fin del día actual (23:59:59)
    fun obtenerFinDelDia(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    // Obtener inicio de la semana actual (Lunes 00:00:00)
    fun obtenerInicioSemana(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Obtener fin de la semana actual (Domingo 23:59:59)
    fun obtenerFinSemana(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    // Obtener inicio del mes actual (día 1, 00:00:00)
    fun obtenerInicioMes(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Obtener fin del mes actual (último día, 23:59:59)
    fun obtenerFinMes(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    // Obtener nombre del mes actual
    fun obtenerNombreMesActual(): String {
        val formatoMes = SimpleDateFormat("MMMM yyyy", Locale("es", "PE"))
        return formatoMes.format(Date()).replaceFirstChar { it.uppercase() }
    }

    // Verificar si una fecha es hoy
    fun Long.esHoy(): Boolean {
        val hoy = Calendar.getInstance()
        val fecha = Calendar.getInstance().apply { timeInMillis = this@esHoy }
        return hoy.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
                hoy.get(Calendar.DAY_OF_YEAR) == fecha.get(Calendar.DAY_OF_YEAR)
    }
}