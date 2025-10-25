package com.tecsup.bodeapp.util

import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val DATABASE_NAME = "bodeapp_database"

    // Formato de fecha
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun Long.toFechaString(): String {
        return dateFormat.format(Date(this))
    }

    fun Long.toHoraString(): String {
        return timeFormat.format(Date(this))
    }

    // Obtener timestamp del inicio del día actual (00:00:00)
    fun obtenerInicioDelDia(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
