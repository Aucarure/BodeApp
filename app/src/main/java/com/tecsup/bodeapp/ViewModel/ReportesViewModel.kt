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
    val margenUtilidad: Double = 0.0, // Porcentaje de utilidad
    val cantidadVentas: Int = 0,
    val cantidadCompras: Int = 0,
    val promedioVenta: Double = 0.0,
    val promedioCompra: Double = 0.0,
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

    fun cambiarPeriodo(periodo: PeriodoReporte) {
        _uiState.value = _uiState.value.copy(
            periodoActual = periodo,
            cargando = true,
            error = null
        )
        cargarDatos(periodo)
    }

    private fun cargarDatos(periodo: PeriodoReporte) {
        viewModelScope.launch {
            try {
                val (inicio, fin) = obtenerRangoFechas(periodo)

                _uiState.value = _uiState.value.copy(
                    fechaInicio = inicio,
                    fechaFin = fin
                )

                combine(
                    ventaDao.obtenerTotalVentasPorRango(inicio, fin),
                    compraDao.obtenerTotalComprasPorRango(inicio, fin),
                    ventaDao.contarVentasPorRango(inicio, fin),
                    compraDao.contarComprasPorRango(inicio, fin),
                    ventaDao.obtenerPromedioVentasPorRango(inicio, fin),
                    compraDao.obtenerPromedioComprasPorRango(inicio, fin),
                    ventaDao.obtenerProductosMasVendidosPorRango(inicio, fin)
                ) { valores ->
                    val totalVentas = valores[0] as? Double ?: 0.0
                    val totalCompras = valores[1] as? Double ?: 0.0
                    val cantVentas = valores[2] as? Int ?: 0
                    val cantCompras = valores[3] as? Int ?: 0
                    val promVenta = valores[4] as? Double ?: 0.0
                    val promCompra = valores[5] as? Double ?: 0.0
                    @Suppress("UNCHECKED_CAST")
                    val topProductos = valores[6] as? List<ProductoVendido> ?: emptyList()

                    // Calcular utilidad neta
                    val utilidad = totalVentas - totalCompras

                    // Calcular margen de utilidad (porcentaje)
                    val margen = if (totalVentas > 0) {
                        (utilidad / totalVentas) * 100
                    } else {
                        0.0
                    }

                    ReporteUiState(
                        periodoActual = periodo,
                        totalVentas = totalVentas,
                        totalCompras = totalCompras,
                        utilidadNeta = utilidad,
                        margenUtilidad = margen,
                        cantidadVentas = cantVentas,
                        cantidadCompras = cantCompras,
                        promedioVenta = promVenta,
                        promedioCompra = promCompra,
                        productosMasVendidos = topProductos,
                        cargando = false,
                        error = null,
                        fechaInicio = inicio,
                        fechaFin = fin
                    )
                }.catch { e ->
                    _uiState.value = _uiState.value.copy(
                        cargando = false,
                        error = "Error al cargar datos: ${e.message}"
                    )
                }.collect { nuevoEstado ->
                    _uiState.value = nuevoEstado
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = "Error al cargar datos: ${e.message}"
                )
            }
        }
    }

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

    fun refrescarDatos() {
        cargarDatos(_uiState.value.periodoActual)
    }

    fun obtenerDescripcionPeriodo(): String {
        return when (_uiState.value.periodoActual) {
            PeriodoReporte.HOY -> "Hoy"
            PeriodoReporte.SEMANA -> "Esta semana"
            PeriodoReporte.MES -> DateUtils.obtenerNombreMesActual()
        }
    }

    /**
     * Obtener resumen en texto para compartir o exportar
     */
    fun obtenerResumenTexto(): String {
        val estado = _uiState.value
        return buildString {
            appendLine("=== REPORTE DE CIERRE DE CAJA ===")
            appendLine()
            appendLine("Período: ${obtenerDescripcionPeriodo()}")
            appendLine()
            appendLine("RESUMEN FINANCIERO:")
            appendLine("- Total Ventas: S/ ${"%.2f".format(estado.totalVentas)}")
            appendLine("- Total Compras: S/ ${"%.2f".format(estado.totalCompras)}")
            appendLine("- Utilidad Neta: S/ ${"%.2f".format(estado.utilidadNeta)}")
            appendLine("- Margen de Utilidad: ${"%.1f".format(estado.margenUtilidad)}%")
            appendLine()
            appendLine("ESTADÍSTICAS:")
            appendLine("- Transacciones de Venta: ${estado.cantidadVentas}")
            appendLine("- Registros de Compra: ${estado.cantidadCompras}")
            if (estado.promedioVenta > 0) {
                appendLine("- Promedio por Venta: S/ ${"%.2f".format(estado.promedioVenta)}")
            }
            if (estado.promedioCompra > 0) {
                appendLine("- Promedio por Compra: S/ ${"%.2f".format(estado.promedioCompra)}")
            }
            appendLine()
            appendLine("PRODUCTOS MÁS VENDIDOS:")
            if (estado.productosMasVendidos.isEmpty()) {
                appendLine("- No hay productos vendidos en este período")
            } else {
                estado.productosMasVendidos.forEachIndexed { index, producto ->
                    appendLine("${index + 1}. ${producto.nombreProducto} - ${producto.totalVendido} unidades")
                }
            }
        }
    }

    /**
     * Validar si hay datos suficientes para generar reporte
     */
    fun hayDatosSuficientes(): Boolean {
        return _uiState.value.cantidadVentas > 0 || _uiState.value.cantidadCompras > 0
    }
}

class ReportesViewModelFactory(
    private val ventaDao: VentaDao,
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportesViewModel::class.java)) {
            return ReportesViewModel(ventaDao, compraDao, productoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}