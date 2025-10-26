package com.tecsup.bodeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import com.tecsup.bodeapp.data.dao.CompraDao
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.model.Compra
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ComprasViewModel(
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao
) : ViewModel() {

    private val hoy = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val comprasDelDia: StateFlow<List<Compra>> = compraDao.obtenerComprasDelDia(hoy)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalGastadoHoy: StateFlow<Double> = compraDao.obtenerTotalComprasDelDia(hoy)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Estados para mensajes de error/éxito
    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    private val _compraExitosa = MutableStateFlow(false)
    val compraExitosa: StateFlow<Boolean> = _compraExitosa

    /**
     * Agregar una compra y actualizar el stock del producto si existe
     * @param nombre Nombre del producto comprado
     * @param cantidad Cantidad comprada
     * @param costoTotal Costo total de la compra
     */
    fun agregarCompra(nombre: String, cantidad: Int, costoTotal: Double) {
        viewModelScope.launch {
            try {
                // Validaciones
                if (nombre.isBlank()) {
                    _mensajeError.value = "El nombre del producto no puede estar vacío"
                    return@launch
                }

                if (cantidad <= 0) {
                    _mensajeError.value = "La cantidad debe ser mayor a 0"
                    return@launch
                }

                if (costoTotal <= 0.0) {
                    _mensajeError.value = "El costo total debe ser mayor a 0"
                    return@launch
                }

                val costoUnitario = costoTotal / cantidad

                // Registrar la compra
                val compra = Compra(
                    id = 0,
                    nombreProducto = nombre.trim(),
                    cantidad = cantidad,
                    costoUnitario = costoUnitario,
                    costoTotal = costoTotal,
                    fecha = System.currentTimeMillis()
                )
                compraDao.insertar(compra)

                // Intentar actualizar el stock si el producto existe
                val productos = productoDao.obtenerTodos().firstOrNull() ?: emptyList()
                val producto = productos.find {
                    it.nombre.trim().equals(nombre.trim(), ignoreCase = true)
                }

                if (producto != null) {
                    // Si el producto existe, aumentar su stock
                    productoDao.aumentarStock(producto.id, cantidad)
                } else {
                    // Si no existe, informar al usuario (opcional)
                    _mensajeError.value = "Compra registrada. El producto no existe en inventario."
                }

                _compraExitosa.value = true

            } catch (e: Exception) {
                _mensajeError.value = "Error al registrar compra: ${e.message}"
            }
        }
    }

    /**
     * Limpiar mensajes de error/éxito
     */
    fun limpiarMensajes() {
        _mensajeError.value = null
        _compraExitosa.value = false
    }
}

class ComprasViewModelFactory(
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComprasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComprasViewModel(compraDao, productoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}