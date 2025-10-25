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
    fun agregarCompra(nombre: String, cantidad: Int, costoTotal: Double) {
        viewModelScope.launch {
            val costoUnitario = if (cantidad > 0) costoTotal / cantidad else 0.0

            val compra = Compra(
                id = 0,
                nombreProducto = nombre,
                cantidad = cantidad,
                costoUnitario = costoUnitario,
                costoTotal = costoTotal,
                fecha = System.currentTimeMillis()
            )
            compraDao.insertar(compra)
            val producto = productoDao.obtenerTodos().firstOrNull()?.find {
                it.nombre.equals(nombre, ignoreCase = true)
            }
            producto?.let {
                productoDao.aumentarStock(it.id, cantidad)
            }
        }
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
        throw IllegalArgumentException("error")
    }
}
