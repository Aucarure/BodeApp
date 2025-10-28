package com.tecsup.bodeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.bodeapp.data.dao.CompraDao
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.VentaDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class ReportesUiState(
    val totalVentas: Double = 0.0,
    val totalCompras: Double = 0.0,
    val utilidadNeta: Double = 0.0,
    val productosMasVendidos: List<Pair<String, Int>> = emptyList()
)
class ReportesViewModel(
    private val ventaDao: VentaDao,
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportesUiState())
    val uiState: StateFlow<ReportesUiState> = _uiState.asStateFlow()

    init {
        cargarReportes()
    }
    private fun cargarReportes() {
        viewModelScope.launch {
            combine(
                ventaDao.obtenerTotalVentasDelDia(inicioDelDia()),
                compraDao.obtenerTotalComprasDelDia(inicioDelDia()),
                ventaDao.obtenerProductosMasVendidos()
            ) { ventasHoy, comprasHoy, productosMasVendidos ->
                val totalVentas = ventasHoy ?: 0.0
                val totalCompras = comprasHoy ?: 0.0
                val utilidad = totalVentas - totalCompras

                ReportesUiState(
                    totalVentas = totalVentas,
                    totalCompras = totalCompras,
                    utilidadNeta = utilidad,
                    productosMasVendidos = productosMasVendidos.map {
                        it.nombreProducto to it.totalVendido
                    }
                )
            }.collect { estado ->
                _uiState.value = estado
            }
        }
    }
    fun cargarReportesManual() {
        viewModelScope.launch {
            cargarReportes()
        }
    }

    private fun inicioDelDia(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}

class ReportesViewModelFactory(
    private val ventaDao: VentaDao,
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportesViewModel(ventaDao, compraDao, productoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

