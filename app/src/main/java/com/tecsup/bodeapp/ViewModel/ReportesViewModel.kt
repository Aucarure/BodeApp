package com.tecsup.bodeapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.bodeapp.data.dao.CompraDao
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.ProductoVendido
import com.tecsup.bodeapp.data.dao.VentaDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ReportesUiState(
    val totalVentas: Double = 0.0,
    val totalCompras: Double = 0.0,
    val utilidadNeta: Double = 0.0,
    val cantidadVentas: Int = 0,
    val cantidadCompras: Int = 0,
    val promedioVenta: Double = 0.0,
    val productosMasVendidos: List<ProductoVendido> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null,
    val fechaInicio: Long = 0L,
    val fechaFin: Long = 0L
)

class ReportesViewModel(
    private val ventaDao: VentaDao,
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportesUiState())
    val uiState: StateFlow<ReportesUiState> = _uiState.asStateFlow()
    fun cargarReportesManual() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true)
            try {
                combine(
                    ventaDao.obtenerTotalVentasDelDia(inicioDelDia()),
                    compraDao.obtenerTotalComprasDelDia(inicioDelDia()),
                    ventaDao.obtenerProductosMasVendidos()
                ) { ventasHoy, comprasHoy, productosMasVendidos ->

                    val totalVentas = ventasHoy ?: 0.0
                    val totalCompras = comprasHoy ?: 0.0
                    val utilidad = totalVentas - totalCompras

                    Log.d("REPORTES_VM", "Ventas: $totalVentas, Compras: $totalCompras, Utilidad: $utilidad")

                    ReportesUiState(
                        totalVentas = totalVentas,
                        totalCompras = totalCompras,
                        utilidadNeta = utilidad,
                        productosMasVendidos = productosMasVendidos.map {
                            it.nombreProducto to it.totalVendido
                        },
                        cargando = false
                    )
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportesUiState())
                    .collect { estado -> _uiState.value = estado }

            } catch (e: Exception) {
                Log.e("REPORTES_VM", "Error cargando reportes: ${e.message}")
                _uiState.value = ReportesUiState(cargando = false)
            }
        }
        viewModelScope.launch {
            val totalGeneral = ventaDao.obtenerTotalVentas() ?: 0.0
            if (totalGeneral > 0 && _uiState.value.totalVentas == 0.0) {
                _uiState.update { it.copy(totalVentas = totalGeneral, cargando = false) }
                Log.d("REPORTES_VM", "Total general mostrado: $totalGeneral")
            }
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

// Factory para crear el ViewModel
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