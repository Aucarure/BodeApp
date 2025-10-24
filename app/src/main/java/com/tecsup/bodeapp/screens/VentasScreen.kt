package com.tecsup.bodeapp.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun VentasScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProductos: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToReportes: () -> Unit
) {
    Text("pantalla de Ventas")
}