package com.tecsup.bodeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.bodeapp.data.dao.CompraDao
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.viewmodel.ComprasViewModel
import com.tecsup.bodeapp.viewmodel.ComprasViewModelFactory
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprasScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProductos: () -> Unit,
    onNavigateToVentas: () -> Unit,
    onNavigateToReportes: () -> Unit,
    compraDao: CompraDao,
    productoDao: ProductoDao
) {
    val viewModel: ComprasViewModel = viewModel(
        factory = ComprasViewModelFactory(compraDao, productoDao)
    )
    val compras by viewModel.comprasDelDia.collectAsStateWithLifecycle()
    val totalGastado by viewModel.totalGastadoHoy.collectAsStateWithLifecycle()
    var nombreProducto by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var costoTotal by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFFF0F4FF),
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 3,
                onNavigateToHome = onNavigateToHome,
                onNavigateToProductos = onNavigateToProductos,
                onNavigateToVentas = onNavigateToVentas,
                onNavigateToCompras = { },
                onNavigateToReportes = onNavigateToReportes
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF8B5CF6),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Compras",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Compras / Insumos",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Registra tus compras y gastos",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Nueva Compra",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            OutlinedTextField(
                                value = nombreProducto,
                                onValueChange = { nombreProducto = it },
                                label = { Text("Nombre del producto") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color(0xFF8B5CF6)
                                )
                            )
                            OutlinedTextField(
                                value = cantidad,
                                onValueChange = { cantidad = it },
                                label = { Text("Cantidad") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color(0xFF3B82F6)
                                )
                            )
                            OutlinedTextField(
                                value = costoTotal,
                                onValueChange = { costoTotal = it },
                                label = { Text("Costo Total (S/)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 20.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color(0xFF10B981)
                                )
                            )
                            Button(
                                onClick = {
                                    val cantidadInt = cantidad.toIntOrNull() ?: 0
                                    val costoTotalDouble = costoTotal.toDoubleOrNull() ?: 0.0

                                    if (nombreProducto.isNotBlank() && cantidadInt > 0 && costoTotalDouble > 0.0) {
                                        viewModel.agregarCompra(nombreProducto, cantidadInt, costoTotalDouble)
                                        nombreProducto = ""
                                        cantidad = ""
                                        costoTotal = ""
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Agregar Compra",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Total gastado hoy",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "S/ ${String.format("%.2f", totalGastado)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B5CF6),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Historial de compras
                item {
                    Text(
                        text = "Historial de Compras",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Lista de compras
                items(compras) { compra ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = compra.nombreProducto,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Cantidad: ${compra.cantidad}",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "S/ ${String.format("%.2f", compra.costoTotal)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8B5CF6)
                                )
                                Text(
                                    text = "S/ ${String.format("%.2f", compra.costoUnitario)} c/u",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
