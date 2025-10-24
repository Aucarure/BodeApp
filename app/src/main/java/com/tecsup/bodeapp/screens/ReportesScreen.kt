package com.tecsup.bodeapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController







@Composable
fun ReportesScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProductos: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToVentas: () -> Unit
) {
    // Contenedor vertical para el contenido y la barra inferior
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Pantalla de Ventas")
        }

        // Barra de navegación inferior
        BottomNavigationBar(
            selectedItem = 4,
            onNavigateToHome = onNavigateToHome,
            onNavigateToProductos = onNavigateToProductos,
            onNavigateToVentas = onNavigateToVentas,
            onNavigateToCompras = onNavigateToCompras,
            onNavigateToReportes = {}
        )
    }
}
