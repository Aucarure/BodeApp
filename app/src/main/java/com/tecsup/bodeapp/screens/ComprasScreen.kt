package com.tecsup.bodeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
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
import kotlinx.coroutines.launch

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
    val viewModel: ComprasViewModel = viewModel(factory = ComprasViewModelFactory(compraDao, productoDao))
    val compras by viewModel.comprasDelDia.collectAsStateWithLifecycle()
    val totalGastado by viewModel.totalGastadoHoy.collectAsStateWithLifecycle()
    var nombreProducto by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var costoTotal by remember { mutableStateOf("") }
    var errorNombre by remember { mutableStateOf(false) }
    var errorCantidad by remember { mutableStateOf(false) }
    var errorCosto by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFF0F4FF),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            //Encabezado
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

            // Contenido principal
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //Formulario
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
                                onValueChange = {
                                    nombreProducto = it
                                    errorNombre = false
                                },
                                isError = errorNombre,
                                label = { Text("Nombre del producto") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                            )
                            if (errorNombre) {
                                Text("El nombre no puede estar vacío", color = Color.Red, fontSize = 12.sp)
                            }

                            OutlinedTextField(
                                value = cantidad,
                                onValueChange = {
                                    cantidad = it
                                    errorCantidad = false
                                },
                                isError = errorCantidad,
                                label = { Text("Cantidad") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                            )
                            if (errorCantidad) {
                                Text(" La cantidad debe ser un número positivo", color = Color.Red, fontSize = 12.sp)
                            }

                            OutlinedTextField(
                                value = costoTotal,
                                onValueChange = {
                                    costoTotal = it
                                    errorCosto = false
                                },
                                isError = errorCosto,
                                label = { Text("Costo Total (S/)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                            )
                            if (errorCosto) {
                                Text("El costo debe ser un número positivo", color = Color.Red, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val cantidadInt = cantidad.toIntOrNull() ?: -1
                                    val costoTotalDouble = costoTotal.toDoubleOrNull() ?: -1.0
                                    var valid = true

                                    if (nombreProducto.isBlank()) {
                                        errorNombre = true
                                        valid = false
                                    }
                                    if (cantidadInt <= 0) {
                                        errorCantidad = true
                                        valid = false
                                    }
                                    if (costoTotalDouble <= 0.0) {
                                        errorCosto = true
                                        valid = false
                                    }

                                    if (valid) {
                                        viewModel.agregarCompra(nombreProducto, cantidadInt, costoTotalDouble)
                                        nombreProducto = ""
                                        cantidad = ""
                                        costoTotal = ""
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Compra registrada correctamente")
                                        }
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Verifica los datos ingresados")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Agregar Compra", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Total gastado
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
                            Text("Total gastado hoy", fontSize = 14.sp, color = Color.Gray)
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

                //Historial
                item {
                    Text(
                        text = "Historial de Compras",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                //Listado de compras
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
                                Text(compra.nombreProducto, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    "Cantidad: ${compra.cantidad}",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "S/ ${String.format("%.2f", compra.costoTotal)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8B5CF6)
                                )
                                Text(
                                    "S/ ${String.format("%.2f", compra.costoUnitario)} c/u",
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
