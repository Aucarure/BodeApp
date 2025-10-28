package com.tecsup.bodeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.model.Producto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VentasViewModel(
    private val productoDao: ProductoDao
) : ViewModel() {
    val productos: StateFlow<List<Producto>> = productoDao.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun venderProducto(producto: Producto) {
        viewModelScope.launch {
            if (producto.stock > 0) {
                productoDao.reducirStock(producto.id, 1)
            }
        }
    }
}
class VentasViewModelFactory(
    private val productoDao: ProductoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VentasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VentasViewModel(productoDao) as T
        }
        throw IllegalArgumentException("error")
    }
}
