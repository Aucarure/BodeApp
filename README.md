📦 BodeApp

Aplicación móvil desarrollada en Kotlin + Jetpack Compose, creada para ayudar a los dueños de pequeñas bodegas a llevar el control de sus ventas, compras e inventario.
El diseño sigue las guías de Material Design 3, priorizando la simplicidad, la claridad y la facilidad de uso.

🎨 Diseño y prototipo

Prototipo elaborado en Figma, con una interfaz limpia y moderna.
🔗 Ver prototipo:
https://deer-ocean-34446870.figma.site/


🚀 Funcionalidades principales

📦 Registro de productos con nombre, precio y stock inicial.

💰 Registro de ventas con cálculo automático del subtotal y actualización del inventario.

🛒 Registro de compras e insumos.

📊 Cierre de caja con resumen de ventas, compras y utilidad del día.

🔍 Filtros por producto y fecha.

✅ Validaciones que impiden vender sin stock.

🧠 Descripción técnica

  Lenguaje: Kotlin
  
  Framework: Jetpack Compose 
  
  Base de datos local: Room / SQLite
  
  IDE: Android Studio


Estructura del proyecto:

ui/          → Pantallas y componentes visuales  
model/       → Clases de datos (Producto, Venta, Compra)  
data/        → Base de datos local y DAOs  
navigation/  → Control de navegación entre pantallas  
util/        → Funciones auxiliares
