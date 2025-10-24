package com.tecsup.bodeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.bodeapp.screens.*


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Productos : Screen("productos")
    object Ventas : Screen("ventas")
    object Compras : Screen("compras")
    object Reportes : Screen("reportes")
    object CierreCaja : Screen("cierrecaja")
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
                onNavigateToProductos = { navController.navigate(Screen.Productos.route) },
                onNavigateToVentas = { navController.navigate(Screen.Ventas.route) },
                onNavigateToCompras = { navController.navigate(Screen.Compras.route) },
                onNavigateToReportes = { navController.navigate(Screen.Reportes.route) }
            )
        }

        composable(Screen.Productos.route) {
            ProductosScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToVentas = { navController.navigate(Screen.Ventas.route) },
                onNavigateToCompras = { navController.navigate(Screen.Compras.route) },
                onNavigateToReportes = { navController.navigate(Screen.Reportes.route) }
            )
        }

        composable(Screen.Ventas.route) {
            VentasScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToProductos = { navController.navigate(Screen.Productos.route) },
                onNavigateToCompras = { navController.navigate(Screen.Compras.route) },
                onNavigateToReportes = { navController.navigate(Screen.Reportes.route) }
            )
        }

        composable(Screen.Compras.route) {
            ComprasScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToProductos = { navController.navigate(Screen.Productos.route) },
                onNavigateToVentas = { navController.navigate(Screen.Ventas.route) },
                onNavigateToReportes = { navController.navigate(Screen.Reportes.route) }
            )
        }

        composable(Screen.Reportes.route) {
            ReportesScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToProductos = { navController.navigate(Screen.Productos.route) },
                onNavigateToVentas = { navController.navigate(Screen.Ventas.route) },
                onNavigateToCompras = { navController.navigate(Screen.Compras.route) }
            )
        }

        composable(Screen.CierreCaja.route) {
            CierreCajaScreen()
        }
    }
}