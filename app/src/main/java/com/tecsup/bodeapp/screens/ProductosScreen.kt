package com.tecsup.bodeapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.bodeapp.data.database.AppDatabase
import com.tecsup.bodeapp.model.Producto
import kotlinx.coroutines.launch
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                                tint = Color(0xFF2E7D32)
                            )
                        },
                        label = { Text("Nombre del Producto") },
                        placeholder = { Text("Ej: Coca Cola 1.5L") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    )
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.AttachMoney,
                                contentDescription = "Precio",
                                tint = Color(0xFF2E7D32)
                            )
                        },
                        label = { Text("Precio (S/.)") },
                        placeholder = { Text("0.00") },
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
                                tint = Color(0xFF2E7D32)
                            )
                        },
                        label = { Text("Stock Inicial") },
                        placeholder = { Text("0") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
            Button(
                onClick = {
                    val nombre = nombreProducto.text.trim()
                    val precioDouble = precio.text.toDoubleOrNull()
                    val stockInt = stock.text.toIntOrNull()

                    if (nombre.isEmpty() || precioDouble == null || stockInt == null) {
                        Toast.makeText(context, "Complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        scope.launch {
                            val nuevoProducto = Producto(
                                nombre = nombre,
                                precio = precioDouble,
                                stock = stockInt
                            )
                            productoDao.insertar(nuevoProducto)
                            Toast.makeText(context, "Producto guardado correctamente", Toast.LENGTH_SHORT).show()

                            // Limpiar los campos
                            nombreProducto = TextFieldValue("")
                            precio = TextFieldValue("")
                            stock = TextFieldValue("")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C13F8)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(
                    text = "+ Guardar Producto",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            // Lista de productos
            Text(" Productos registrados:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxHeight(0.5f)
            ) {
                items(productos) { producto ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(producto.nombre, fontWeight = FontWeight.Bold)
                                Text("S/. ${producto.precio}")
                            }
                            Text("Stock: ${producto.stock}", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
        // Barra de navegación inferior
        BottomNavigationBar(
            selectedItem = 1,
            onNavigateToHome = onNavigateToHome,
            onNavigateToProductos = {},
            onNavigateToVentas = onNavigateToVentas,
            onNavigateToCompras = onNavigateToCompras,
            onNavigateToReportes = onNavigateToReportes
        )
    }
}
@Preview(showBackground = true, name = "Vista previa - Registro Producto")
@Composable fun ProductosScreenPreview() { ProductosScreen( onNavigateToHome =
    {}, onNavigateToReportes = {},
    onNavigateToCompras = {},
    onNavigateToVentas = {} ) }