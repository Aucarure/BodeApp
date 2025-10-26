package com.tecsup.bodeapp.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.bodeapp.data.dao.CompraDao
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.VentaDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

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
        uri?.let { generarPDF(context, it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE4080A)),
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 50.dp),
                horizontalAlignment = Alignment.Start
            ) {
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
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE4080A)),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Utilidad Neta", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            text = "—",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sin datos disponibles",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoCard(
                        titulo = "Ventas",
                        monto = "22",
                        detalle = "No registradas",
                        colorMonto = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        titulo = "Compras",
                        monto = "3",
                        detalle = "No registradas",
                        colorMonto = Color(0xFFE53935),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
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
                        val productosMasVendidos = emptyList<Pair<String, String>>()
                        if (productosMasVendidos.isEmpty()) {
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
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {},
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
fun generarPDF(context: Context, uri: Uri) {
    CoroutineScope(Dispatchers.IO).launch {
        val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
        outputStream?.use {
            val contenido = """
                REPORTE DE CIERRE DE CAJA
                Fecha: ${SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale("es")).format(Date())}
                Sin datos disponibles para mostrar.
            """.trimIndent()
            it.write(contenido.toByteArray())
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ReportesScreenPreview() {
    ReportesScreen()
}
