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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.bodeapp.data.database.AppDatabase
import com.tecsup.bodeapp.navigation.Screen
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        color = Color(0xFF2563EB),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    ))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-100).dp)
                    .padding(horizontal = 24.dp)
            ) {
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    Text(
                        text = "BodeApp",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Gestiona tu bodega fácilmente",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                    ) }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Accesos Rápidos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    } }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToReportes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
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
                    ) }
                Spacer(modifier = Modifier.height(24.dp))
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
            .height(100.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = value,
                    fontSize = 18.sp,
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
) { Card(
        onClick = onClick,
        modifier = modifier
            .height(120.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) { Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) { Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBackgroundColor, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                ) }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onNavigateToProductos = {},
        onNavigateToVentas = {},
        onNavigateToCompras = {},
        onNavigateToReportes = {}
    )
}
