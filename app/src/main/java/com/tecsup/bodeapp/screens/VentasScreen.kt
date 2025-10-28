package com.tecsup.bodeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.model.Producto
import com.tecsup.bodeapp.viewmodel.VentasViewModel
import com.tecsup.bodeapp.viewmodel.VentasViewModelFactory

@Composable
fun VentasScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProductos: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToReportes: () -> Unit,
    productoDao: ProductoDao
) {
    val viewModel: VentasViewModel = viewModel(factory = VentasViewModelFactory(productoDao))
    val productos by viewModel.productos.collectAsState()
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var cantidad by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var alertaSinStock by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        //Encabezado
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 50.dp)
            ) {
                Text(
                    text = "Ventas",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Registra las ventas del día",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (productos.isEmpty()) {
                Text(
                    text = "No hay productos disponibles",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(productos.size) { index ->
                        val producto = productos[index]
                        ProductoCard(
                            producto = producto,
                            onAgregarClick = {
                                if (producto.stock == 0) {
                                    alertaSinStock = true
                                } else {
                                    productoSeleccionado = producto
                                    cantidad = ""
                                    mostrarConfirmacion = true
                                }
                            }
                        )
                    }
                }
            }
        }

        // sin stock
        if (alertaSinStock) {
            AlertDialog(
                onDismissRequest = { alertaSinStock = false },
                confirmButton = {
                    TextButton(onClick = { alertaSinStock = false }) {
                        Text("OK")
                    }
                },
                title = { Text("Sin stock") },
                text = { Text("No hay unidades disponibles para este producto.") }
            )
        }

        // confirmación de venta
        if (mostrarConfirmacion && productoSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { mostrarConfirmacion = false },
                confirmButton = {
                    TextButton(onClick = {
                        val cantidadInt = cantidad.toIntOrNull() ?: 0
                        val producto = productoSeleccionado!!
                        if (cantidadInt in 1..producto.stock) {
                            // Llamada compatible con la firma existente venderProducto(producto)
                            repeat(cantidadInt) { viewModel.venderProducto(producto) }
                            mostrarConfirmacion = false
                        } else {
                            alertaSinStock = true
                            mostrarConfirmacion = false
                        }
                    }) {
                        Text("Vender")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarConfirmacion = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Confirmar venta") },
                text = {
                    Column {
                        Text("¿Cuántas unidades deseas vender de '${productoSeleccionado!!.nombre}'?")
                        OutlinedTextField(
                            value = cantidad,
                            onValueChange = { cantidad = it },
                            label = { Text("Cantidad") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            )
        }
        BottomNavigationBar(
            selectedItem = 2,
            onNavigateToHome = onNavigateToHome,
            onNavigateToProductos = onNavigateToProductos,
            onNavigateToVentas = {},
            onNavigateToCompras = onNavigateToCompras,
            onNavigateToReportes = onNavigateToReportes
        )
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onAgregarClick: () -> Unit
) {
    val sinStock = producto.stock == 0
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Stock: ${producto.stock} unidades",
                    color = if (sinStock) Color.Red else Color.Gray,
                    fontSize = 13.sp
                )
                Text(
                    text = "S/ %.2f".format(producto.precio),
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            IconButton(
                onClick = onAgregarClick,
                enabled = !sinStock,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (sinStock) Color.LightGray else Color(0xFF2E7D32),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar producto",
                    tint = if (sinStock) Color.Gray else Color.White
                )
            }
        }
    }
}