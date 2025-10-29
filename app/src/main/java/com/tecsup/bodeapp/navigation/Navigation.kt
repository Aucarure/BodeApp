package com.tecsup.bodeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.tecsup.bodeapp.data.database.AppDatabase
import com.tecsup.bodeapp.screens.*
import com.tecsup.bodeapp.viewmodel.ReportesViewModel
import com.tecsup.bodeapp.viewmodel.ReportesViewModelFactory

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
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val productoDao = db.productoDao()
    val compraDao = db.compraDao()
    val ventaDao = db.ventaDao()


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
                onNavigateToReportes = { navController.navigate(Screen.Reportes.route) },
                productoDao = productoDao

            )
        }
        composable(Screen.Compras.route) {
            ComprasScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToProductos = { navController.navigate(Screen.Productos.route) },
                onNavigateToVentas = { navController.navigate(Screen.Ventas.route) },
                onNavigateToReportes = { navController.navigate(Screen.Reportes.route) },
                compraDao = compraDao,
                productoDao = productoDao
            )
        }
        composable(Screen.Reportes.route) {
            val reportesViewModel: ReportesViewModel = viewModel(
                factory = ReportesViewModelFactory(
                    ventaDao = ventaDao,
                    compraDao = compraDao,
                    productoDao = productoDao
                )
            )

            ReportesScreen(
                viewModel = reportesViewModel,
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
