package com.example.controlefinanceiro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// DAO define as operações que o aplicativo pode executar no banco.
@Dao
interface LancamentoDao {

    // Observa a tabela e emite novamente a lista quando ela muda.
    @Query("SELECT * FROM lancamentos ORDER BY rowid DESC")
    fun observarTodos(): Flow<List<LancamentoEntity>>

    // Insere um lançamento. Se o ID já existir, substitui o registro.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(lancamento: LancamentoEntity)

    // Retorna a quantidade de registros efetivamente atualizados.
    @Update
    suspend fun atualizar(lancamento: LancamentoEntity): Int

    // Exclui um lançamento específico pelo ID.
    @Query("DELETE FROM lancamentos WHERE id = :id")
    suspend fun excluirPorId(id: String)
}
