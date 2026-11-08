package com.example.controlefinanceiro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Informa ao Room quais entidades fazem parte do banco
// e qual é a versão atual do esquema.
@Database(
    entities = [LancamentoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ControleDespesasDatabase : RoomDatabase() {

    // O Room gera a implementação concreta deste DAO.
    abstract fun lancamentoDao(): LancamentoDao

    companion object {

        // Guarda a instância compartilhada do banco.
        @Volatile
        private var INSTANCE: ControleDespesasDatabase? = null

        // Retorna o banco existente ou cria a primeira instância.
        fun getDatabase(context: Context): ControleDespesasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ControleDespesasDatabase::class.java,
                    "controle_despesas_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
