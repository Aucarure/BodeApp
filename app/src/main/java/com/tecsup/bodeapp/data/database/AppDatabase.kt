package com.tecsup.bodeapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.bodeapp.data.dao.CompraDao
import com.tecsup.bodeapp.data.dao.ProductoDao
import com.tecsup.bodeapp.data.dao.VentaDao
import com.tecsup.bodeapp.model.Compra
import com.tecsup.bodeapp.model.Producto
import com.tecsup.bodeapp.model.Venta

@Database(
    entities = [Producto::class, Venta::class, Compra::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun ventaDao(): VentaDao
    abstract fun compraDao(): CompraDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bodeapp_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}