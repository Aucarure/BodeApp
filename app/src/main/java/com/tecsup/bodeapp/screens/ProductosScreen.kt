package com.tecsup.bodeapp.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.bodeapp.data.database.AppDatabase
import com.tecsup.bodeapp.model.Producto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToReportes: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToVentas: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getInstance(context) }
    val productoDao = db.productoDao()

    var nombreProducto by remember { mutableStateOf(TextFieldValue("")) }
    var precio by remember { mutableStateOf(TextFieldValue("")) }
    var stock by remember { mutableStateOf(TextFieldValue("")) }
    val productos by productoDao.obtenerTodos().collectAsState(initial = emptyList())

    var productoParaAumentarStock by remember { mutableStateOf<Producto?>(null) }
    var cantidadAumentar by remember { mutableStateOf(TextFieldValue("")) }
    var mostrarDialogoStock by remember { mutableStateOf(false) }

    // Validación de producto duplicado
    fun existeProducto(nombre: String): Boolean {
        return productos.any { it.nombre.trim().equals(nombre.trim(), ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // ENCABEZADO
        Card(
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C13F8)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 50.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Registro de Producto",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Agrega nuevos productos al inventario",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }

        // CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // FORMULARIO DE NUEVO PRODUCTO
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = nombreProducto,
                        onValueChange = { nombreProducto = it },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Inventory2,
                                contentDescription = "Producto",
                                tint = Color(0xFF0C13F8)
                            )
                        },
                        label = { Text("Nombre del Producto") },
                        placeholder = { Text("Ej: Coca Cola 1.5L") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        isError = nombreProducto.text.isNotBlank() && existeProducto(nombreProducto.text),
                        supportingText = {
                            if (nombreProducto.text.isNotBlank() && existeProducto(nombreProducto.text)) {
                                Text(
                                    "⚠️ Este producto ya existe. Usa el botón '+ Stock' para aumentar cantidad.",
                                    color = Color(0xFFE53935),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.AttachMoney,
                                contentDescription = "Precio",
                                tint = Color(0xFF0C13F8)
                            )
                        },
                        label = { Text("Precio (S/.)") },
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    )

                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Numbers,
                                contentDescription = "Stock",
                                tint = Color(0xFF0C13F8)
                            )
                        },
                        label = { Text("Stock Inicial") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            // BOTÓN GUARDAR
            Button(
                onClick = {
                    val nombre = nombreProducto.text.trim()
                    val precioDouble = precio.text.toDoubleOrNull()
                    val stockInt = stock.text.toIntOrNull()

                    when {
                        nombre.isEmpty() || precioDouble == null || stockInt == null -> {
                            Toast.makeText(
                                context,
                                "Complete todos los campos correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        existeProducto(nombre) -> {
                            Toast.makeText(
                                context,
                                "Este producto ya existe. Usa el botón '+ Stock' para aumentar cantidad.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        precioDouble <= 0 -> {
                            Toast.makeText(
                                context,
                                "El precio debe ser mayor a 0",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        stockInt < 0 -> {
                            Toast.makeText(
                                context,
                                "El stock no puede ser negativo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            scope.launch {
                                val nuevoProducto = Producto(
                                    nombre = nombre,
                                    precio = precioDouble,
                                    stock = stockInt
                                )
                                productoDao.insertar(nuevoProducto)
                                Toast.makeText(
                                    context,
                                    "Producto guardado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Limpiar los campos
                                nombreProducto = TextFieldValue("")
                                precio = TextFieldValue("")
                                stock = TextFieldValue("")
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C13F8)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guardar Producto",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // LISTA DE PRODUCTOS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Productos registrados:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "${productos.size} productos",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxHeight(0.5f)
            ) {
                items(productos) { producto ->
                    ProductoCard(
                        producto = producto,
                        onAumentarStock = {
                            productoParaAumentarStock = producto
                            cantidadAumentar = TextFieldValue("")
                            mostrarDialogoStock = true
                        },
                        onEliminar = {
                            scope.launch {
                                productoDao.eliminar(producto)
                                Toast.makeText(
                                    context,
                                    "Producto eliminado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }
        }

        // BARRA DE NAVEGACIÓN
        BottomNavigationBar(
            selectedItem = 1,
            onNavigateToHome = onNavigateToHome,
            onNavigateToProductos = {},
            onNavigateToVentas = onNavigateToVentas,
            onNavigateToCompras = onNavigateToCompras,
            onNavigateToReportes = onNavigateToReportes
        )
    }

    // DIÁLOGO PARA AUMENTAR STOCK
    if (mostrarDialogoStock && productoParaAumentarStock != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoStock = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cantidad = cantidadAumentar.text.toIntOrNull()
                        if (cantidad != null && cantidad > 0) {
                            scope.launch {
                                productoDao.aumentarStock(productoParaAumentarStock!!.id, cantidad)
                                Toast.makeText(
                                    context,
                                    "Stock aumentado en $cantidad unidades",
                                    Toast.LENGTH_SHORT
                                ).show()
                                mostrarDialogoStock = false
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Ingresa una cantidad válida",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoStock = false }) {
                    Text("Cancelar")
                }
            },
            icon = {
                Icon(
                    Icons.Default.AddBox,
                    contentDescription = null,
                    tint = Color(0xFF0C13F8),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text("Aumentar Stock")
            },
            text = {
                Column {
                    Text(
                        "Producto: ${productoParaAumentarStock!!.nombre}",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Stock actual: ${productoParaAumentarStock!!.stock} unidades",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = cantidadAumentar,
                        onValueChange = { cantidadAumentar = it },
                        label = { Text("Cantidad a agregar") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Ej: 10") }
                    )
                }
            }
        )
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onAumentarStock: () -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarMenuOpciones by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    producto.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "S/. ${producto.precio}",
                        fontSize = 14.sp,
                        color = Color(0xFF0C13F8),
                        fontWeight = FontWeight.Medium
                    )
                    Text("•", color = Color.Gray)
                    Text(
                        "Stock: ${producto.stock}",
                        fontWeight = FontWeight.Medium,
                        color = when {
                            producto.stock == 0 -> Color(0xFFE53935)
                            producto.stock < 10 -> Color(0xFFFF9800)
                            else -> Color(0xFF2E7D32)
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Botón aumentar stock
                IconButton(
                    onClick = onAumentarStock,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF0C13F8), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Aumentar stock",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Menú de opciones (eliminar)
                Box {
                    IconButton(
                        onClick = { mostrarMenuOpciones = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Más opciones",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = mostrarMenuOpciones,
                        onDismissRequest = { mostrarMenuOpciones = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color(0xFFE53935)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Eliminar", color = Color(0xFFE53935))
                                }
                            },
                            onClick = {
                                mostrarMenuOpciones = false
                                onEliminar()
                            }
                        )
                    }
                }
            }
        }
    }
}