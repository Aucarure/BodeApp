package com.tecsup.bodeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.bodeapp.navigation.Screen

@Preview(showBackground = true, name = "Vista previa VentasScreen")
@Composable
fun VentasScreenPreview() {
    VentasScreen(
        onNavigateToHome = {},
        onNavigateToProductos = {},
        onNavigateToCompras = {},
        onNavigateToReportes = {}
    )
}

@Composable
fun VentasScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProductos: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToReportes: () -> Unit
) {
    val productos = emptyList<Productos>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // 🔹 Encabezado verde con bordes redondeados
        Card(
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 50.dp)
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

        //  Contenido principal
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
                        ProductoCard(
                            producto = productos[index],
                            onAgregarClick = { /* Acción al agregar */ }
                        )
                    }
                }
            }
        }

        //Barra de navegación inferior
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

// Tarjeta del producto
@Composable
fun ProductoCard(
    producto: Productos,
    onAgregarClick: () -> Unit
) {
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
                    color = Color.Gray,
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
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF2E7D32), shape = RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar producto",
                    tint = Color.White
                )
            }
        }
    }
}

// Modelo de datos
data class Productos(
    val nombre: String = "",
    val stock: Int = 0,
    val precio: Double = 0.0
)
