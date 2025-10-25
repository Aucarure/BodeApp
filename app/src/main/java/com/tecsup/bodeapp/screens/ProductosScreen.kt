package com.tecsup.bodeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProductosScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToReportes: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToVentas: () -> Unit
) {
    var nombreProducto by remember { mutableStateOf(TextFieldValue("")) }
    var precio by remember { mutableStateOf(TextFieldValue("")) }
    var stock by remember { mutableStateOf(TextFieldValue("")) }

    // Fondo general
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // 🔹 Encabezado verde con bordes redondeados
        Card(
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C13F8)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
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

        // 🔸 Aquí iría el contenido principal, formulario, etc.



    // 🔸 Cuerpo principal
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🧾 Card de formulario
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
                    // Nombre del producto
                    OutlinedTextField(
                        value = nombreProducto,
                        onValueChange = { nombreProducto = it },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Inventory2,
                                contentDescription = "Producto",
                                tint = Color(0xFF2E7D32) ,


                            )
                        },
                        label = { Text("Nombre del Producto") },
                        placeholder = { Text("Ej: Coca Cola 1.5L") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    )

                    // Precio
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

                    // Stock inicial
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

            // 🔵 Botón guardar producto
            Button(
                onClick = { /* TODO: acción guardar */ },
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

            // 💡 Consejo
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 Consejo:", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mantén actualizado el inventario para evitar problemas de stock.",
                        color = Color(0xFF1565C0)
                    )
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
@Composable
fun ProductosScreenPreview() {
    ProductosScreen(
        onNavigateToHome = {},
        onNavigateToReportes = {},
        onNavigateToCompras = {},
        onNavigateToVentas = {}
    )
}
