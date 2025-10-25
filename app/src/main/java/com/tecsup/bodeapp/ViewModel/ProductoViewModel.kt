package com.tecsup.bodeapp.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.model.Producto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductoViewModel(private val productoDao: ProductoDao) : ViewModel() {
    val productos: StateFlow<List<Producto>> =
        productoDao.obtenerTodos().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun insertarProducto(nombre: String, precio: Double, stock: Int) {
        viewModelScope.launch {
            val nuevo = Producto(nombre = nombre, precio = precio, stock = stock)
            productoDao.insertar(nuevo)
        }
    }
    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            productoDao.eliminar(producto)
        }
    }
}
