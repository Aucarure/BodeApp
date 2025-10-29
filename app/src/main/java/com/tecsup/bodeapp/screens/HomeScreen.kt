package com.tecsup.bodeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.bodeapp.data.database.AppDatabase
import com.tecsup.bodeapp.viewmodel.HomeViewModel
import com.tecsup.bodeapp.viewmodel.HomeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProductos: () -> Unit,
    onNavigateToVentas: () -> Unit,
    onNavigateToCompras: () -> Unit,
    onNavigateToReportes: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            productoDao = database.productoDao(),
            ventaDao = database.ventaDao()
        )
    )

    val uiState by homeViewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF0F4FF),
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 0,
                onNavigateToHome = { },
                onNavigateToProductos = onNavigateToProductos,
                onNavigateToVentas = onNavigateToVentas,
                onNavigateToCompras = onNavigateToCompras,
                onNavigateToReportes = onNavigateToReportes
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ENCABEZADO AZUL (Fijo)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        color = Color(0xFF2563EB),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
            )

            // CONTENIDO CON SCROLL
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Espaciador para el header
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // TÍTULO Y SUBTÍTULO
                item {
                    Column(modifier = Modifier.padding(bottom = 20.dp)) {
                        Text(
                            text = "BodeApp",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Gestiona tu bodega fácilmente",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // TARJETAS DE ESTADÍSTICAS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            icon = Icons.Default.TrendingUp,
                            iconColor = Color(0xFF10B981),
                            label = "Ventas Hoy",
                            value = "S/ ${"%,.2f".format(uiState.totalVentasHoy)}",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.Inventory2,
                            iconColor = Color(0xFF3B82F6),
                            label = "Productos",
                            value = uiState.totalProductos.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.Error,
                            iconColor = Color(0xFFEF4444),
                            label = "Stock Bajo",
                            value = uiState.productosStockBajo.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // TÍTULO ACCESOS RÁPIDOS
                item {
                    Text(
                        text = "Accesos Rápidos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // FILA 1 DE ACCESOS RÁPIDOS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickAccessCard(
                            icon = Icons.Default.Category,
                            iconColor = Color(0xFF3B82F6),
                            iconBackgroundColor = Color(0xFFE3F2FD),
                            label = "Productos",
                            onClick = onNavigateToProductos,
                            modifier = Modifier.weight(1f)
                        )
                        QuickAccessCard(
                            icon = Icons.Default.ShoppingCart,
                            iconColor = Color(0xFF10B981),
                            iconBackgroundColor = Color(0xFFE8F5E9),
                            label = "Ventas",
                            onClick = onNavigateToVentas,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // FILA 2 DE ACCESOS RÁPIDOS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickAccessCard(
                            icon = Icons.Default.ShoppingBag,
                            iconColor = Color(0xFF8B5CF6),
                            iconBackgroundColor = Color(0xFFF3E5F5),
                            label = "Compras",
                            onClick = onNavigateToCompras,
                            modifier = Modifier.weight(1f)
                        )
                        QuickAccessCard(
                            icon = Icons.Default.BarChart,
                            iconColor = Color(0xFFF59E0B),
                            iconBackgroundColor = Color(0xFFFFF3E0),
                            label = "Reportes",
                            onClick = onNavigateToReportes,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // BOTÓN CIERRE DE CAJA
                item {
                    Button(
                        onClick = onNavigateToReportes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cierre de Caja",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                // Espaciador final
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun QuickAccessCard(
    icon: ImageVector,
    iconColor: Color,
    iconBackgroundColor: Color,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(110.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBackgroundColor, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}