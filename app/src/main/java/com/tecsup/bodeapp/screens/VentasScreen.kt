package com.tecsup.bodeapp.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.VentaDao
import com.tecsup.bodeapp.model.Producto
import com.tecsup.bodeapp.viewmodel.VentasViewModel
import com.tecsup.bodeapp.viewmodel.VentasViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProductos: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToReportes: () -> Unit,
    productoDao: ProductoDao,
    ventaDao: VentaDao
) {
    val viewModel: VentasViewModel = viewModel(
        factory = VentasViewModelFactory(productoDao, ventaDao)
    )
    val productos by viewModel.productos.collectAsState()
    val carritoState by viewModel.carritoState.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val ventaExitosa by viewModel.ventaExitosa.collectAsState()

    var busqueda by remember { mutableStateOf("") }
    var mostrarCarritoDetalle by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Manejar mensajes
    LaunchedEffect(mensajeError) {
        mensajeError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajes()
        }
    }

    LaunchedEffect(ventaExitosa) {
        if (ventaExitosa) {
            snackbarHostState.showSnackbar("Venta registrada correctamente")
            viewModel.limpiarMensajes()
        }
    }

    // Filtrar productos según búsqueda
    val productosFiltrados = remember(productos, busqueda) {
        if (busqueda.isBlank()) {
            productos
        } else {
            productos.filter {
                it.nombre.contains(busqueda, ignoreCase = true)
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F7F7),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column {
                // Carrito flotante
                AnimatedVisibility(
                    visible = carritoState.items.isNotEmpty(),
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    CarritoFlotante(
                        carritoState = carritoState,
                        onVerDetalle = { mostrarCarritoDetalle = true },
                        onRegistrarVenta = {
                            scope.launch {
                                viewModel.registrarVenta()
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
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Encabezado
                Card(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
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

                // Barra de búsqueda
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Buscar producto...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { busqueda = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                // Lista de productos
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = if (carritoState.items.isNotEmpty()) 80.dp else 16.dp)
                ) {
                    if (productosFiltrados.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.SearchOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (busqueda.isBlank())
                                            "No hay productos disponibles"
                                        else
                                            "No se encontraron productos",
                                        color = Color.Gray,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(productosFiltrados) { producto ->
                            ProductoVentaCard(
                                producto = producto,
                                cantidadEnCarrito = carritoState.items.find { it.producto.id == producto.id }?.cantidad ?: 0,
                                onAgregar = { viewModel.agregarAlCarrito(producto) },
                                onReducir = { viewModel.reducirCantidad(producto.id) }
                            )
                        }
                    }
                }
            }

            // Diálogo detalle del carrito
            if (mostrarCarritoDetalle) {
                CarritoDetalleDialog(
                    carritoState = carritoState,
                    onDismiss = { mostrarCarritoDetalle = false },
                    onEliminarItem = { productoId -> viewModel.eliminarDelCarrito(productoId) },
                    onLimpiarCarrito = {
                        viewModel.limpiarCarrito()
                        mostrarCarritoDetalle = false
                    }
                )
            }
        }
    }
}

@Composable
fun ProductoVentaCard(
    producto: Producto,
    cantidadEnCarrito: Int,
    onAgregar: () -> Unit,
    onReducir: () -> Unit
) {
    val sinStock = producto.stock == 0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (cantidadEnCarrito > 0) Color(0xFFE8F5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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

            // Controles de cantidad
            if (cantidadEnCarrito > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onReducir,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFE53935), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Reducir",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = cantidadEnCarrito.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )

                    IconButton(
                        onClick = onAgregar,
                        enabled = cantidadEnCarrito < producto.stock,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (cantidadEnCarrito < producto.stock) Color(0xFF2E7D32) else Color.LightGray,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                IconButton(
                    onClick = onAgregar,
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
}

@Composable
fun CarritoFlotante(
    carritoState: com.tecsup.bodeapp.viewmodel.CarritoState,
    onVerDetalle: () -> Unit,
    onRegistrarVenta: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Resumen del carrito
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Carrito",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    if (carritoState.items.isNotEmpty()) {
                        Text(
                            carritoState.items.first().producto.nombre +
                                    if (carritoState.items.size > 1) " + ${carritoState.items.size - 1} más" else "",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Total:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            "S/ %.2f".format(carritoState.total),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onVerDetalle,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE8F5E9), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Ver detalle",
                            tint = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón registrar venta
            Button(
                onClick = onRegistrarVenta,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Registrar Venta",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoDetalleDialog(
    carritoState: com.tecsup.bodeapp.viewmodel.CarritoState,
    onDismiss: () -> Unit,
    onEliminarItem: (Int) -> Unit,
    onLimpiarCarrito: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Detalle del Carrito",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Lista de items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(carritoState.items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.producto.nombre,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "S/ ${item.producto.precio} x ${item.cantidad}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "S/ %.2f".format(item.subtotal),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                IconButton(
                                    onClick = { onEliminarItem(item.producto.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        if (item != carritoState.items.last()) {
                            Divider()
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "S/ %.2f".format(carritoState.total),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón limpiar carrito
                OutlinedButton(
                    onClick = onLimpiarCarrito,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE53935)
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Vaciar Carrito")
                }
            }
        }
    }
}