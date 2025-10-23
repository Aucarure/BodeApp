package com.tecsup.bodeapp.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToVentas: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToReportes:() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Pantalla de inicio")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToVentas) { Text("Ir a Ventas") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToCompras) { Text("Ir a Compras") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToReportes) { Text("Ir a reportes") }

    }
}