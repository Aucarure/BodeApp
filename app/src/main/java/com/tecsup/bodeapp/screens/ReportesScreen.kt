package com.tecsup.bodeapp.screens

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.bodeapp.data.dao.CompraDao
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.VentaDao
import com.tecsup.bodeapp.viewmodel.ReportesViewModel
import com.tecsup.bodeapp.viewmodel.ReportesViewModelFactory
import com.tecsup.bodeapp.viewmodel.PeriodoReporte
import kotlinx.coroutines.*
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
    var generandoPdf by remember { mutableStateOf(false) }

    // Actualiza la hora cada minuto
    LaunchedEffect(Unit) {
        while (true) {
            fechaHoraActual = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale("es")).format(Date())
            delay(60_000)
        }
    }

    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        uri?.let {
            generandoPdf = true
            generarPDFReal(context, it, uiState) { success ->
                generandoPdf = false
                if (success) {
                    Toast.makeText(context, "PDF generado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al generar PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F7F7),
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 4,
                onNavigateToHome = onNavigateToHome,
                onNavigateToProductos = onNavigateToProductos,
                onNavigateToVentas = onNavigateToVentas,
                onNavigateToCompras = onNavigateToCompras,
                onNavigateToReportes = onNavigateToReportes
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    Text(
                        "Cierre de Caja",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Fecha y hora: $fechaHoraActual",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            // Filtros de período
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.utilidadNeta >= 0) Color(0xFF2E7D32) else Color(0xFFE4080A)
                        ),
                        elevation = CardDefaults.cardElevation(6.dp),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Utilidad Neta",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "S/ ${"%.2f".format(uiState.utilidadNeta)}",
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (uiState.utilidadNeta >= 0) "Ganancia" else "Pérdida",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp
                                )
                            }
                            IconButton(
                                onClick = { viewModel.refrescarDatos() },
                                modifier = Modifier.background(
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
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

                // VENTAS Y COMPRAS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoCard(
                            titulo = "Ventas",
                            monto = "S/ ${"%.2f".format(uiState.totalVentas)}",
                            detalle = "${uiState.cantidadVentas} transacciones",
                            colorMonto = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            titulo = "Compras",
                            monto = "S/ ${"%.2f".format(uiState.totalCompras)}",
                            detalle = "${uiState.cantidadCompras} registros",
                            colorMonto = Color(0xFFE53935),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // PROMEDIO DE VENTA
                if (uiState.promedioVenta > 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Promedio por Venta",
                                        fontSize = 14.sp,
                                        color = Color(0xFF1565C0),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "S/ ${"%.2f".format(uiState.promedioVenta)}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1976D2)
                                    )
                                }
                            }
                        }
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
                            Text(
                                "Productos Más Vendidos",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (uiState.productosMasVendidos.isEmpty()) {
                                Text(
                                    "No hay productos vendidos en este período",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                uiState.productosMasVendidos.forEachIndexed { index, producto ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(
                                                        when (index) {
                                                            0 -> Color(0xFFFFD700)
                                                            1 -> Color(0xFFC0C0C0)
                                                            2 -> Color(0xFFCD7F32)
                                                            else -> Color(0xFFE0E0E0)
                                                        },
                                                        RoundedCornerShape(12.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "${index + 1}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                producto.nombreProducto,
                                                color = Color.Black,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Text(
                                            "${producto.totalVendido} und.",
                                            color = Color(0xFF2E7D32),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (index < uiState.productosMasVendidos.size - 1) {
                                        Divider(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // BOTONES
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.refrescarDatos() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4080A)),
                            shape = RoundedCornerShape(15.dp),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !generandoPdf
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizar Datos", color = Color.White)
                        }

                        Button(
                            onClick = {
                                val timestamp = System.currentTimeMillis()
                                val periodo = when (uiState.periodoActual) {
                                    PeriodoReporte.HOY -> "hoy"
                                    PeriodoReporte.SEMANA -> "semana"
                                    PeriodoReporte.MES -> "mes"
                                }
                                createPdfLauncher.launch("reporte_${periodo}_$timestamp.pdf")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2)
                            ),
                            shape = RoundedCornerShape(15.dp),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !generandoPdf
                        ) {
                            if (generandoPdf) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (generandoPdf) "Generando PDF..." else "Descargar PDF",
                                color = Color.White
                            )
                        }
                    }
                }

            }
        }
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
            Text(titulo, color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(monto, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorMonto)
            Text(detalle, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

// GENERAR PDF REAL CON PdfDocument
fun generarPDFReal(
    context: Context,
    uri: Uri,
    uiState: com.tecsup.bodeapp.viewmodel.ReporteUiState,
    onComplete: (Boolean) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Configurar paints
            val titlePaint = Paint().apply {
                color = android.graphics.Color.parseColor("#E4080A")
                textSize = 24f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            val headerPaint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 16f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            val normalPaint = Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 12f
            }
            val smallPaint = Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 10f
            }

            var yPos = 60f

            // Título
            canvas.drawText("REPORTE DE CIERRE DE CAJA", 50f, yPos, titlePaint)
            yPos += 30f

            // Fecha
            val fecha = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale("es")).format(Date())
            canvas.drawText("Fecha: $fecha", 50f, yPos, normalPaint)
            yPos += 40f

            // Línea separadora
            canvas.drawLine(50f, yPos, 545f, yPos, normalPaint)
            yPos += 30f

            // RESUMEN FINANCIERO
            canvas.drawText("RESUMEN FINANCIERO", 50f, yPos, headerPaint)
            yPos += 25f

            canvas.drawText("Total Ventas:", 80f, yPos, normalPaint)
            canvas.drawText("S/ ${"%.2f".format(uiState.totalVentas)}", 400f, yPos, normalPaint)
            yPos += 20f

            canvas.drawText("Total Compras:", 80f, yPos, normalPaint)
            canvas.drawText("S/ ${"%.2f".format(uiState.totalCompras)}", 400f, yPos, normalPaint)
            yPos += 20f

            // Línea para utilidad
            canvas.drawLine(80f, yPos, 545f, yPos, smallPaint)
            yPos += 20f

            val utilidadPaint = Paint().apply {
                color = if (uiState.utilidadNeta >= 0)
                    android.graphics.Color.parseColor("#2E7D32")
                else
                    android.graphics.Color.parseColor("#E4080A")
                textSize = 16f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            canvas.drawText("Utilidad Neta:", 80f, yPos, headerPaint)
            canvas.drawText("S/ ${"%.2f".format(uiState.utilidadNeta)}", 400f, yPos, utilidadPaint)
            yPos += 40f

            // ESTADÍSTICAS
            canvas.drawText("ESTADÍSTICAS", 50f, yPos, headerPaint)
            yPos += 25f

            canvas.drawText("Cantidad de Ventas: ${uiState.cantidadVentas}", 80f, yPos, normalPaint)
            yPos += 20f

            canvas.drawText("Cantidad de Compras: ${uiState.cantidadCompras}", 80f, yPos, normalPaint)
            yPos += 20f

            if (uiState.promedioVenta > 0) {
                canvas.drawText("Promedio por Venta: S/ ${"%.2f".format(uiState.promedioVenta)}", 80f, yPos, normalPaint)
                yPos += 30f
            }

            // PRODUCTOS MÁS VENDIDOS
            canvas.drawLine(50f, yPos, 545f, yPos, normalPaint)
            yPos += 30f

            canvas.drawText("PRODUCTOS MÁS VENDIDOS", 50f, yPos, headerPaint)
            yPos += 25f

            if (uiState.productosMasVendidos.isEmpty()) {
                canvas.drawText("→ No hay productos vendidos en este período", 80f, yPos, normalPaint)
                yPos += 20f
            } else {
                uiState.productosMasVendidos.forEachIndexed { index, producto ->
                    val ranking = when (index) {
                        0 -> "🥇"
                        1 -> "🥈"
                        2 -> "🥉"
                        else -> "${index + 1}."
                    }
                    canvas.drawText("$ranking ${producto.nombreProducto}", 80f, yPos, normalPaint)
                    canvas.drawText("${producto.totalVendido} unidades", 400f, yPos, normalPaint)
                    yPos += 20f
                }
            }

            yPos += 20f

            // Pie de página
            canvas.drawLine(50f, yPos, 545f, yPos, smallPaint)
            yPos += 20f
            canvas.drawText("Generado por BodeApp", 50f, yPos, smallPaint)
            canvas.drawText("www.bodeapp.com", 450f, yPos, smallPaint)

            pdfDocument.finishPage(page)

            // Guardar el PDF
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            outputStream?.use {
                pdfDocument.writeTo(it)
            }
            pdfDocument.close()

            withContext(Dispatchers.Main) {
                onComplete(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onComplete(false)
            }
        }
    }
}