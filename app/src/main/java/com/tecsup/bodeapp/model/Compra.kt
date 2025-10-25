package com.tecsup.bodeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compras")
data class Compra(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreProducto: String,
    val cantidad: Int,
    val costoUnitario: Double,
    val costoTotal: Double,
    val fecha: Long = System.currentTimeMillis() // Timestamp
)