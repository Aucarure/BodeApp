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

/**
 * Representa un item en el carrito de ventas
 */
data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int = 1
) {
    val subtotal: Double
        get() = producto.precio * cantidad
}

/**
 * Estado del carrito de ventas
 */
data class CarritoState(
    val items: List<ItemCarrito> = emptyList(),
    val total: Double = 0.0
)

class VentasViewModel(
    private val productoDao: ProductoDao,
    private val ventaDao: VentaDao
) : ViewModel() {

    val productos: StateFlow<List<Producto>> = productoDao.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del carrito
    private val _carritoState = MutableStateFlow(CarritoState())
    val carritoState: StateFlow<CarritoState> = _carritoState

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    private val _ventaExitosa = MutableStateFlow(false)
    val ventaExitosa: StateFlow<Boolean> = _ventaExitosa

    /**
     * Agregar un producto al carrito o aumentar su cantidad
     */
    fun agregarAlCarrito(producto: Producto) {
        viewModelScope.launch {
            try {
                val stockActual = productoDao.obtenerStockPorId(producto.id)

                if (stockActual == null) {
                    _mensajeError.value = "Producto no encontrado"
                    return@launch
                }

                if (stockActual == 0) {
                    _mensajeError.value = "Producto agotado"
                    return@launch
                }

                val itemsActuales = _carritoState.value.items.toMutableList()
                val itemExistente = itemsActuales.find { it.producto.id == producto.id }

                if (itemExistente != null) {
                    // Verificar que no exceda el stock
                    val nuevaCantidad = itemExistente.cantidad + 1
                    if (nuevaCantidad > stockActual) {
                        _mensajeError.value = "Stock insuficiente. Disponible: $stockActual"
                        return@launch
                    }

                    // Actualizar cantidad del item existente
                    val index = itemsActuales.indexOf(itemExistente)
                    itemsActuales[index] = itemExistente.copy(cantidad = nuevaCantidad)
                } else {
                    // Agregar nuevo item
                    itemsActuales.add(ItemCarrito(producto, 1))
                }

                actualizarCarrito(itemsActuales)

            } catch (e: Exception) {
                _mensajeError.value = "Error al agregar al carrito: ${e.message}"
            }
        }
    }

    /**
     * Reducir la cantidad de un producto en el carrito
     */
    fun reducirCantidad(productoId: Int) {
        val itemsActuales = _carritoState.value.items.toMutableList()
        val itemExistente = itemsActuales.find { it.producto.id == productoId }

        if (itemExistente != null) {
            val index = itemsActuales.indexOf(itemExistente)
            if (itemExistente.cantidad > 1) {
                // Reducir cantidad
                itemsActuales[index] = itemExistente.copy(cantidad = itemExistente.cantidad - 1)
            } else {
                // Eliminar del carrito si la cantidad es 1
                itemsActuales.removeAt(index)
            }
            actualizarCarrito(itemsActuales)
        }
    }

    /**
     * Eliminar un producto del carrito completamente
     */
    fun eliminarDelCarrito(productoId: Int) {
        val itemsActuales = _carritoState.value.items.toMutableList()
        itemsActuales.removeAll { it.producto.id == productoId }
        actualizarCarrito(itemsActuales)
    }

    /**
     * Actualizar el estado del carrito y calcular el total
     */
    private fun actualizarCarrito(items: List<ItemCarrito>) {
        val total = items.sumOf { it.subtotal }
        _carritoState.value = CarritoState(items = items, total = total)
    }

    /**
     * Registrar toda la venta (todos los items del carrito)
     */
    fun registrarVenta() {
        viewModelScope.launch {
            try {
                val items = _carritoState.value.items

                if (items.isEmpty()) {
                    _mensajeError.value = "El carrito está vacío"
                    return@launch
                }

                // Validar stock de todos los productos antes de procesar
                for (item in items) {
                    val stockActual = productoDao.obtenerStockPorId(item.producto.id)
                    if (stockActual == null || stockActual < item.cantidad) {
                        _mensajeError.value = "Stock insuficiente para ${item.producto.nombre}"
                        return@launch
                    }
                }

                // Registrar cada item como una venta
                for (item in items) {
                    val venta = Venta(
                        productoId = item.producto.id,
                        nombreProducto = item.producto.nombre,
                        cantidad = item.cantidad,
                        precioUnitario = item.producto.precio,
                        total = item.subtotal,
                        fecha = System.currentTimeMillis()
                    )
                    ventaDao.insertar(venta)
                    productoDao.reducirStock(item.producto.id, item.cantidad)
                }

                // Limpiar el carrito
                _carritoState.value = CarritoState()
                _ventaExitosa.value = true
                _mensajeError.value = null

            } catch (e: Exception) {
                _mensajeError.value = "Error al registrar venta: ${e.message}"
            }
        }
    }

    /**
     * Limpiar el carrito
     */
    fun limpiarCarrito() {
        _carritoState.value = CarritoState()
    }

    /**
     * Limpiar mensajes de error/éxito
     */
    fun limpiarMensajes() {
        _mensajeError.value = null
        _ventaExitosa.value = false
    }

    // ========== MÉTODOS ANTIGUOS (para compatibilidad) ==========

    /**
     * @deprecated Usar agregarAlCarrito() en su lugar
     */
    @Deprecated("Usar agregarAlCarrito() para agregar productos al carrito")
    fun venderProducto(producto: Producto, cantidad: Int = 1) {
        // Este método antiguo ahora simplemente agrega al carrito
        repeat(cantidad) {
            agregarAlCarrito(producto)
        }
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