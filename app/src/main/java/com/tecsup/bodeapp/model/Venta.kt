package com.tecsup.bodeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ventas")
data class Venta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val total: Double,
    val fecha: Long = System.currentTimeMillis() // Timestamp
)