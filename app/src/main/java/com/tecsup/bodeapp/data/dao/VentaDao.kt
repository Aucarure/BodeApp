package com.tecsup.bodeapp.data.dao

import androidx.room.*
import com.tecsup.bodeapp.model.Venta
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {
    @Insert
    suspend fun insertar(venta: Venta)
    @Delete
    suspend fun eliminar(venta: Venta)
    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<Venta>>
    @Query("SELECT * FROM ventas WHERE fecha >= :inicioDelDia ORDER BY fecha DESC")
    fun obtenerVentasDelDia(inicioDelDia: Long): Flow<List<Venta>>
    @Query("SELECT SUM(total) FROM ventas WHERE fecha >= :inicioDelDia")
    fun obtenerTotalVentasDelDia(inicioDelDia: Long): Flow<Double?>
    @Query("SELECT * FROM ventas WHERE productoId = :productoId ORDER BY fecha DESC")
    fun obtenerVentasPorProducto(productoId: Int): Flow<List<Venta>>
    @Query("""
        SELECT nombreProducto AS nombreProducto, SUM(cantidad) AS totalVendido
        FROM ventas
        GROUP BY nombreProducto
        ORDER BY totalVendido DESC
        LIMIT 5
    """)
    fun obtenerProductosMasVendidos(): Flow<List<ProductoVendido>>
    @Query("SELECT SUM(total) FROM ventas")
    suspend fun obtenerTotalVentas(): Double?
}
data class ProductoVendido(
    val nombreProducto: String,
    val totalVendido: Int
)
