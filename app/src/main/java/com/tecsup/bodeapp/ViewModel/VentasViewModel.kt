package com.tecsup.bodeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.VentaDao
import com.tecsup.bodeapp.model.Producto
import com.tecsup.bodeapp.model.Venta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VentasViewModel(
    private val productoDao: ProductoDao,
    private val ventaDao: VentaDao
) : ViewModel() {

    val productos: StateFlow<List<Producto>> = productoDao.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    private val _ventaExitosa = MutableStateFlow(false)
    val ventaExitosa: StateFlow<Boolean> = _ventaExitosa

    fun venderProducto(producto: Producto, cantidad: Int = 1) {
        viewModelScope.launch {
            try {
                // Validar stock
                val stockActual = productoDao.obtenerStockPorId(producto.id)

                if (stockActual == null) {
                    _mensajeError.value = "Producto no encontrado"
                    return@launch
                }

                if (stockActual < cantidad) {
                    _mensajeError.value = "Stock insuficiente. Disponible: $stockActual"
                    return@launch
                }

                if (stockActual == 0) {
                    _mensajeError.value = "Producto agotado"
                    return@launch
                }

                // Registrar la venta
                val venta = Venta(
                    productoId = producto.id,
                    nombreProducto = producto.nombre,
                    cantidad = cantidad,
                    precioUnitario = producto.precio,
                    total = producto.precio * cantidad,
                    fecha = System.currentTimeMillis()
                )
                ventaDao.insertar(venta)

                // Reducir stock
                productoDao.reducirStock(producto.id, cantidad)

                _ventaExitosa.value = true
                _mensajeError.value = null

            } catch (e: Exception) {
                _mensajeError.value = "Error al registrar venta: ${e.message}"
            }
        }
    }

    fun limpiarMensajes() {
        _mensajeError.value = null
        _ventaExitosa.value = false
    }
}

class VentasViewModelFactory(
    private val productoDao: ProductoDao,
    private val ventaDao: VentaDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VentasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VentasViewModel(productoDao, ventaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}