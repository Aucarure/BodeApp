package com.tecsup.bodeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.bodeapp.data.dao.CompraDao
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.VentaDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

data class ReporteUiState(
    val totalVentas: Double = 0.0,
    val totalCompras: Double = 0.0,
    val utilidadNeta: Double = 0.0,
    val productosMasVendidos: List<Pair<String, Int>> = emptyList(),
    val cargando: Boolean = true
)

class ReportesViewModel(
    private val ventaDao: VentaDao,
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReporteUiState())
    val uiState: StateFlow<ReporteUiState> = _uiState

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true)

            try {
                // Obtener totales
                val totalVentas = ventaDao.obtenerTotalVentas() ?: 0.0

                // Usamos la fecha del inicio del día para compras
                val inicioDelDia = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val totalComprasFlow = compraDao.obtenerTotalComprasDelDia(inicioDelDia)
                var totalCompras = 0.0
                totalComprasFlow.collect { total ->
                    totalCompras = total ?: 0.0
                }

                // Productos más vendidos
                val productosMasVendidosFlow = ventaDao.obtenerProductosMasVendidos()
                val productosMasVendidos = mutableListOf<Pair<String, Int>>()
                productosMasVendidosFlow.collect { lista ->
                    lista.forEach { p ->
                        productosMasVendidos.add(p.nombreProducto to p.totalVendido)
                    }
                }

                _uiState.value = ReporteUiState(
                    totalVentas = totalVentas,
                    totalCompras = totalCompras,
                    utilidadNeta = totalVentas - totalCompras,
                    productosMasVendidos = productosMasVendidos,
                    cargando = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(cargando = false)
            }
        }
    }
}
