package com.tecsup.bodeapp.viewmodel
import androidx.lifecycle.viewModelScope
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.VentaDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
data class HomeUiState(
    val totalVentasHoy: Double = 0.0,
    val totalProductos: Int = 0,
    val productosStockBajo: Int = 0
)

class HomeViewModel(
    private val productoDao: ProductoDao,
    private val ventaDao: VentaDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val inicioDelDia = calendar.timeInMillis

            combine(
                ventaDao.obtenerTotalVentasDelDia(inicioDelDia),
                productoDao.contarProductos(),
                productoDao.obtenerStockBajo()
            ) { totalVentasHoy, totalProductos, productosStockBajo ->
                HomeUiState(
                    totalVentasHoy = totalVentasHoy ?: 0.0,
                    totalProductos = totalProductos,
                    productosStockBajo = productosStockBajo.size
                )
            }.collect { estado ->
                _uiState.value = estado
            }
        }
    }
}




class HomeViewModelFactory(
    private val productoDao: ProductoDao,
    private val ventaDao: VentaDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(productoDao, ventaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
