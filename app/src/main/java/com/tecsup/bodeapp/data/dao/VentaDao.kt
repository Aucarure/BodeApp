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

    // ====== AGREGAR ESTAS CONSULTAS PARA FILTROS POR RANGO ======

    @Query("SELECT * FROM ventas WHERE fecha >= :inicio AND fecha <= :fin ORDER BY fecha DESC")
    fun obtenerVentasPorRango(inicio: Long, fin: Long): Flow<List<Venta>>

    @Query("SELECT SUM(total) FROM ventas WHERE fecha >= :inicio AND fecha <= :fin")
    fun obtenerTotalVentasPorRango(inicio: Long, fin: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM ventas WHERE fecha >= :inicio AND fecha <= :fin")
    fun contarVentasPorRango(inicio: Long, fin: Long): Flow<Int>

    @Query("""
        SELECT nombreProducto, SUM(cantidad) as totalVendido 
        FROM ventas 
        WHERE fecha >= :inicio AND fecha <= :fin 
        GROUP BY nombreProducto 
        ORDER BY totalVendido DESC 
        LIMIT 10
    """)
    fun obtenerProductosMasVendidosPorRango(inicio: Long, fin: Long): Flow<List<ProductoVendido>>

    @Query("SELECT AVG(total) FROM ventas WHERE fecha >= :inicio AND fecha <= :fin")
    fun obtenerPromedioVentasPorRango(inicio: Long, fin: Long): Flow<Double?>
}

data class ProductoVendido(
    val nombreProducto: String,
    val totalVendido: Int
)