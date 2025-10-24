package com.tecsup.bodeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tecsup.bodeapp.navigation.Screen


@Preview(showBackground = true, name = "Vista previa ")
@Composable
fun VentasScreenPreview() {
    HomeScreen(
        onNavigateToProductos = {},
        onNavigateToVentas = {},
        onNavigateToCompras = {},
        onNavigateToReportes = {}
    )
}




// barra inferior
@Composable
fun VentasScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProductos: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToReportes: () -> Unit
) {


    val productos = emptyList<Screen.Productos>()
    // Contenedor vertical para el contenido y la barra inferior
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.SpaceBetween

    ){
        // encabeasdo
        Column (
            modifier = Modifier
                .background(Color(0xFF2E7D32))
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Text(
                text = "Vnetas",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " registar ventas del dia",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (productos.isEmpty()){
                Text(
                    text =  " no hay productos ",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            } else{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ){
                    items(productos.size){index ->
                        //  card de producto

                    }
                }
            }
        }

        // Barra de navegación inferior
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
    producto: Productos,
    onAgregarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                    .background(Color(0xFF2E7D32), shape = MaterialTheme.shapes.medium)
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

data class Productos(
    val nombre: String = "",
    val stock: Int = 0,
    val precio: Double = 0.0
)