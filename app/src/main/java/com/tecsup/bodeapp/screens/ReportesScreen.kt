package com.tecsup.bodeapp.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.bodeapp.data.dao.CompraDao
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.VentaDao
import com.tecsup.bodeapp.util.DateUtils.toMoneda
import com.tecsup.bodeapp.viewmodel.PeriodoReporte
import com.tecsup.bodeapp.viewmodel.ReportesViewModel
import com.tecsup.bodeapp.viewmodel.ReportesViewModelFactory
import com.tecsup.bodeapp.viewmodel.ReporteUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToProductos: () -> Unit = {},
    onNavigateToVentas: () -> Unit = {},
    onNavigateToCompras: () -> Unit = {},
    onNavigateToReportes: () -> Unit = {},
    ventaDao: VentaDao,
    compraDao: CompraDao,
    productoDao: ProductoDao
) {
    val context = LocalContext.current
    val viewModel: ReportesViewModel = viewModel(
        factory = ReportesViewModelFactory(ventaDao, compraDao, productoDao)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var fechaHoraActual by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val formato = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale("es"))
            fechaHoraActual = formato.format(Date())
            delay(60_000)
        }
    }

    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        uri?.let { generarPDF(context, it, uiState, viewModel.obtenerDescripcionPeriodo()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Encabezado
        Card(
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE4080A)),
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 50.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Cierre de Caja",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Fecha y hora: $fechaHoraActual",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                    IconButton(
                        onClick = { viewModel.refrescarDatos() },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refrescar",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Filtros de período
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.periodoActual == PeriodoReporte.HOY,
                onClick = { viewModel.cambiarPeriodo(PeriodoReporte.HOY) },
                label = { Text("Hoy") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = uiState.periodoActual == PeriodoReporte.SEMANA,
                onClick = { viewModel.cambiarPeriodo(PeriodoReporte.SEMANA) },
                label = { Text("Semana") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = uiState.periodoActual == PeriodoReporte.MES,
                onClick = { viewModel.cambiarPeriodo(PeriodoReporte.MES) },
                label = { Text("Mes") },
                modifier = Modifier.weight(1f)
            )
        }

        // Contenido
        if (uiState.cargando) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE4080A))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Utilidad Neta
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.utilidadNeta >= 0) Color(0xFF10B981) else Color(0xFFE4080A)
                        ),
                        elevation = CardDefaults.cardElevation(6.dp),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Utilidad Neta",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.utilidadNeta.toMoneda(),
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = viewModel.obtenerDescripcionPeriodo(),
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Tarjetas de Ventas y Compras
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoCard(
                            titulo = "Ventas",
                            monto = uiState.totalVentas.toMoneda(),
                            detalle = "${uiState.cantidadVentas} transacciones",
                            colorMonto = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            titulo = "Compras",
                            monto = uiState.totalCompras.toMoneda(),
                            detalle = "${uiState.cantidadCompras} registradas",
                            colorMonto = Color(0xFFE53935),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Promedio de Venta
                if (uiState.promedioVenta > 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Promedio por Venta",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = uiState.promedioVenta.toMoneda(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Productos Más Vendidos
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Productos Más Vendidos",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (uiState.productosMasVendidos.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No hay productos vendidos",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            } else {
                                uiState.productosMasVendidos.forEach { producto ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = producto.nombreProducto,
                                            fontSize = 14.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${producto.totalVendido} unidades",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2563EB)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Botones de acción
                item {
                    Button(
                        onClick = { viewModel.refrescarDatos() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4080A)),
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generar Reporte", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { createPdfLauncher.launch("reporte_cierre_caja_${System.currentTimeMillis()}.pdf") },
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFE53935))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Descargar PDF")
                    }
                }

                // Mensaje informativo
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "💡 Resumen del día",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E88E5)
                            )
                            Text(
                                "Los reportes se generan automáticamente y se pueden descargar en formato PDF.",
                                color = Color(0xFF1E88E5),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Barra inferior
        BottomNavigationBar(
            selectedItem = 4,
            onNavigateToHome = onNavigateToHome,
            onNavigateToProductos = onNavigateToProductos,
            onNavigateToVentas = onNavigateToVentas,
            onNavigateToCompras = onNavigateToCompras,
            onNavigateToReportes = onNavigateToReportes
        )
    }
}

@Composable
fun InfoCard(
    titulo: String,
    monto: String,
    detalle: String,
    colorMonto: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(titulo, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(monto, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorMonto)
            Text(detalle, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

fun generarPDF(context: Context, uri: Uri, uiState: ReporteUiState, periodo: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            outputStream?.use {
                val contenido = """
                    ═══════════════════════════════════════════
                    REPORTE DE CIERRE DE CAJA - BODEAPP
                    ═══════════════════════════════════════════
                    
                    Fecha: ${SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale("es")).format(Date())}
                    Período: $periodo
                    
                    ───────────────────────────────────────────
                    RESUMEN FINANCIERO
                    ───────────────────────────────────────────
                    
                    Total Ventas:      ${uiState.totalVentas.toMoneda()}
                    Total Compras:     ${uiState.totalCompras.toMoneda()}
                    ═══════════════════════════════════════════
                    UTILIDAD NETA:     ${uiState.utilidadNeta.toMoneda()}
                    ═══════════════════════════════════════════
                    
                    ───────────────────────────────────────────
                    DETALLES
                    ───────────────────────────────────────────
                    
                    Número de Ventas:    ${uiState.cantidadVentas}
                    Número de Compras:   ${uiState.cantidadCompras}
                    Promedio por Venta:  ${uiState.promedioVenta.toMoneda()}
                    
                    ───────────────────────────────────────────
                    PRODUCTOS MÁS VENDIDOS
                    ───────────────────────────────────────────
                    
                    ${if (uiState.productosMasVendidos.isEmpty()) {
                    "No hay productos vendidos en este período"
                } else {
                    uiState.productosMasVendidos.joinToString("\n") {
                        "${it.nombreProducto}: ${it.totalVendido} unidades"
                    }
                }}
                    
                    ═══════════════════════════════════════════
                    Generado por BodeApp - Sistema de Gestión
                    ═══════════════════════════════════════════
                """.trimIndent()

                it.write(contenido.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}