package com.tecsup.bodeapp.data.dao

import androidx.room.*
import com.tecsup.bodeapp.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto)

    @Update
    suspend fun actualizar(producto: Producto)

    @Delete
    suspend fun eliminar(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Producto?

    @Query("SELECT * FROM productos WHERE stock <= 10 ORDER BY stock ASC")
    fun obtenerStockBajo(): Flow<List<Producto>>

    @Query("UPDATE productos SET stock = stock - :cantidad WHERE id = :productoId")
    suspend fun reducirStock(productoId: Int, cantidad: Int)

    @Query("UPDATE productos SET stock = stock + :cantidad WHERE id = :productoId")
    suspend fun aumentarStock(productoId: Int, cantidad: Int)

    @Query("SELECT COUNT(*) FROM productos")
    fun contarProductos(): Flow<Int>

    // ====== AGREGAR ESTAS CONSULTAS ======

    @Query("SELECT stock FROM productos WHERE id = :productoId")
    suspend fun obtenerStockPorId(productoId: Int): Int?

    @Query("SELECT * FROM productos WHERE stock > 0 ORDER BY nombre ASC")
    fun obtenerProductosConStock(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE stock = 0 ORDER BY nombre ASC")
    fun obtenerProductosSinStock(): Flow<List<Producto>>
}