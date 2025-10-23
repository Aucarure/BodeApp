package com.tecsup.bodeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.bodeapp.screens.HomeScreen
import com.tecsup.bodeapp.screens.VentasScreen
import com.tecsup.bodeapp.screens.ComprasScreen
import com.tecsup.bodeapp.screens.ProductosScreen
import com.tecsup.bodeapp.screens.CierreCajaScreen
import com.tecsup.bodeapp.screens.ReportesScreen




sealed class Screen(val route: String){
    object Home:Screen("home")
    object Productos:Screen("productos")
    object Ventas:Screen("ventas")
    object Compras:Screen("compras")
    object Reportes:Screen("reportes")
    object CierreCaja:Screen("cierrecaja")
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToVentas = { navController.navigate(Screen.Ventas.route) },
                onNavigateToCompras = { navController.navigate(Screen.Compras.route) },
                onNavigateToReportes = {navController.navigate(Screen.Reportes.route)},
            )
        }
        composable(Screen.Ventas.route){VentasScreen()}
        composable(Screen.Compras.route){ComprasScreen()}

    }
}
