package com.tecsup.bodeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.bodeapp.data.dao.CompraDao
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.ProductoVendido
import com.tecsup.bodeapp.data.dao.VentaDao
import com.tecsup.bodeapp.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Enum para los períodos de filtro
enum class PeriodoReporte {
    HOY,
    SEMANA,
    MES
}

// Estado de la UI de reportes
data class ReporteUiState(
    val periodoActual: PeriodoReporte = PeriodoReporte.HOY,
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

    private val _uiState = MutableStateFlow(ReporteUiState())
    val uiState: StateFlow<ReporteUiState> = _uiState.asStateFlow()

    init {
        cambiarPeriodo(PeriodoReporte.HOY)
    }

    // Cambiar el período del reporte
    fun cambiarPeriodo(periodo: PeriodoReporte) {
        _uiState.value = _uiState.value.copy(
            periodoActual = periodo,
            cargando = true,
            error = null
        )
        cargarDatos(periodo)
    }

    // Cargar datos según el período seleccionado
    private fun cargarDatos(periodo: PeriodoReporte) {
        viewModelScope.launch {
            try {
                val (inicio, fin) = obtenerRangoFechas(periodo)

                _uiState.value = _uiState.value.copy(
                    fechaInicio = inicio,
                    fechaFin = fin
                )

                // Obtener datos de forma secuencial
                ventaDao.obtenerTotalVentasPorRango(inicio, fin).collect { totalVentas ->
                    compraDao.obtenerTotalComprasPorRango(inicio, fin).collect { totalCompras ->
                        ventaDao.contarVentasPorRango(inicio, fin).collect { cantVentas ->
                            compraDao.contarComprasPorRango(inicio, fin).collect { cantCompras ->
                                ventaDao.obtenerPromedioVentasPorRango(inicio, fin).collect { promVenta ->
                                    ventaDao.obtenerProductosMasVendidosPorRango(inicio, fin).collect { topProductos ->

                                        val ventas = totalVentas ?: 0.0
                                        val compras = totalCompras ?: 0.0
                                        val utilidad = ventas - compras

                                        _uiState.value = ReporteUiState(
                                            periodoActual = periodo,
                                            totalVentas = ventas,
                                            totalCompras = compras,
                                            utilidadNeta = utilidad,
                                            cantidadVentas = cantVentas,
                                            cantidadCompras = cantCompras,
                                            promedioVenta = promVenta ?: 0.0,
                                            productosMasVendidos = topProductos,
                                            cargando = false,
                                            error = null,
                                            fechaInicio = inicio,
                                            fechaFin = fin
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = "Error al cargar datos: ${e.message}"
                )
            }
        }
    }

    // Obtener rango de fechas según el período
    private fun obtenerRangoFechas(periodo: PeriodoReporte): Pair<Long, Long> {
        return when (periodo) {
            PeriodoReporte.HOY -> {
                DateUtils.obtenerInicioDelDia() to DateUtils.obtenerFinDelDia()
            }
            PeriodoReporte.SEMANA -> {
                DateUtils.obtenerInicioSemana() to DateUtils.obtenerFinSemana()
            }
            PeriodoReporte.MES -> {
                DateUtils.obtenerInicioMes() to DateUtils.obtenerFinMes()
            }
        }
    }

    // Refrescar datos manualmente
    fun refrescarDatos() {
        cargarDatos(_uiState.value.periodoActual)
    }

    // Obtener texto descriptivo del período actual
    fun obtenerDescripcionPeriodo(): String {
        return when (_uiState.value.periodoActual) {
            PeriodoReporte.HOY -> "Hoy"
            PeriodoReporte.SEMANA -> "Esta semana"
            PeriodoReporte.MES -> DateUtils.obtenerNombreMesActual()
        }
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