package com.tecsup.bodeapp.data.dao

import androidx.room.*
import com.tecsup.bodeapp.model.Compra
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    @Insert
    suspend fun insertar(compra: Compra)

    @Delete
    suspend fun eliminar(compra: Compra)

    @Query("SELECT * FROM compras ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<Compra>>

    @Query("SELECT * FROM compras WHERE fecha >= :inicioDelDia ORDER BY fecha DESC")
    fun obtenerComprasDelDia(inicioDelDia: Long): Flow<List<Compra>>

    @Query("SELECT SUM(costoTotal) FROM compras WHERE fecha >= :inicioDelDia")
    fun obtenerTotalComprasDelDia(inicioDelDia: Long): Flow<Double?>

    @Query("SELECT * FROM compras WHERE fecha >= :inicio AND fecha <= :fin ORDER BY fecha DESC")
    fun obtenerComprasPorRango(inicio: Long, fin: Long): Flow<List<Compra>>

    // ====== NUEVAS CONSULTAS PARA FILTROS POR RANGO ======

    @Query("SELECT SUM(costoTotal) FROM compras WHERE fecha >= :inicio AND fecha <= :fin")
    fun obtenerTotalComprasPorRango(inicio: Long, fin: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM compras WHERE fecha >= :inicio AND fecha <= :fin")
    fun contarComprasPorRango(inicio: Long, fin: Long): Flow<Int>

    @Query("SELECT AVG(costoTotal) FROM compras WHERE fecha >= :inicio AND fecha <= :fin")
    fun obtenerPromedioComprasPorRango(inicio: Long, fin: Long): Flow<Double?>

    @Query("SELECT * FROM compras WHERE fecha >= :inicio AND fecha <= :fin ORDER BY costoTotal DESC LIMIT 10")
    fun obtenerComprasMayoresPorRango(inicio: Long, fin: Long): Flow<List<Compra>>
}