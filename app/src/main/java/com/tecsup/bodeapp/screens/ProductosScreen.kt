package com.tecsup.bodeapp.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ProductosScreen(
    onNavigateToVentas: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToReportes: () -> Unit
) {
    Text("PANTALLA DE PRODUCTOS")
}