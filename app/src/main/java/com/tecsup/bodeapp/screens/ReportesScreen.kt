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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.bodeapp.viewmodel.ReportesViewModel
import com.tecsup.bodeapp.viewmodel.ReportesViewModelFactory
import com.tecsup.bodeapp.data.database.AppDatabase
import kotlinx.coroutines.*
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    viewModel: ReportesViewModel,
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

    // Actualiza la hora cada minuto
    LaunchedEffect(Unit) {
        while (true) {
            fechaHoraActual = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale("es")).format(Date())
            delay(60_000)
        }
    }

    // Cargar reportes automáticamente al entrar
    // Cargar solo una vez al entrar (sin duplicar corrutinas)
    LaunchedEffect(true) {
        viewModel.cargarReportesManual()
    }

    val uiState by viewModel.uiState.collectAsState()

    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        uri?.let { generarPDF(context, it, uiState) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // CABECERA
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
                Text("Cierre de Caja", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Fecha y hora: $fechaHoraActual", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
            }
        }

        // CONTENIDO
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // UTILIDAD NETA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "S/ ${"%.2f".format(uiState.utilidadNeta)}",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiState.utilidadNeta == 0.0) "Sin datos disponibles" else "Datos actualizados",
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

            // VENTAS Y COMPRAS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoCard(
                        titulo = "Ventas",
                        monto = "S/ ${"%.2f".format(uiState.totalVentas)}",
                        detalle = if (uiState.totalVentas == 0.0) "No registradas" else "Ventas del día",
                        colorMonto = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        titulo = "Compras",
                        monto = "S/ ${"%.2f".format(uiState.totalCompras)}",
                        detalle = if (uiState.totalCompras == 0.0) "No registradas" else "Compras del día",
                        colorMonto = Color(0xFFE53935),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // PRODUCTOS MÁS VENDIDOS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Productos Más Vendidos", fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.productosMasVendidos.isEmpty()) {
                            Text("No hay productos vendidos", color = Color.Gray, fontSize = 14.sp)
                        } else {
                            uiState.productosMasVendidos.forEachIndexed { index, (nombre, cantidad) ->
                                Text("${index + 1}. $nombre — $cantidad und.", color = Color.Black, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // BOTONES
            item {
                Button(
                    onClick = { viewModel.cargarReportesManual() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4080A)),
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Actualizar Datos", color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { createPdfLauncher.launch("reporte_cierre_caja.pdf") },
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFE53935))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Descargar PDF")
                }
            }

            // RESUMEN
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("💡 Resumen del día", fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                        Text(
                            "Los reportes se generan automáticamente al final del día y se pueden descargar en formato PDF.",
                            color = Color(0xFF1E88E5),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // BARRA INFERIOR
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

// GENERAR PDF REAL
fun generarPDF(context: Context, uri: Uri, uiState: com.tecsup.bodeapp.viewmodel.ReportesUiState) {
    CoroutineScope(Dispatchers.IO).launch {
        val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
        outputStream?.use {
            val contenido = buildString {
                appendLine("REPORTE DE CIERRE DE CAJA")
                appendLine("Fecha: ${SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale("es")).format(Date())}")
                appendLine("-----------------------------------")
                appendLine("Total Ventas: S/ ${"%.2f".format(uiState.totalVentas)}")
                appendLine("Total Compras: S/ ${"%.2f".format(uiState.totalCompras)}")
                appendLine("Utilidad Neta: S/ ${"%.2f".format(uiState.utilidadNeta)}")
                appendLine("")
                appendLine("Productos más vendidos:")
                if (uiState.productosMasVendidos.isEmpty()) {
                    appendLine(" - No hay productos vendidos.")
                } else {
                    uiState.productosMasVendidos.forEach { (nombre, cantidad) ->
                        appendLine(" - $nombre: $cantidad unidades")
                    }
                }
            }
            it.write(contenido.toByteArray())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportesScreenPreview() {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val viewModel: ReportesViewModel = viewModel(
        factory = ReportesViewModelFactory(db.ventaDao(), db.compraDao(), db.productoDao())
    )
    ReportesScreen(viewModel)
}
