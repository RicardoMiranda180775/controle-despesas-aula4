package com.example.controlefinanceiro.repository

import com.example.controlefinanceiro.data.LancamentoDao
import com.example.controlefinanceiro.data.LancamentoEntity
import com.example.controlefinanceiro.model.Lancamento
import com.example.controlefinanceiro.model.TipoLancamento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LancamentoRepository(
    private val lancamentoDao: LancamentoDao
) {
    // Observa os dados do banco e converte as entidades para o modelo
    // utilizado pela aplicação.
    fun observarTodos(): Flow<List<Lancamento>> {
        return lancamentoDao.observarTodos().map { entidades ->
            entidades.map { entidade -> entidade.toModel() }
        }
    }

    // Persiste um novo lançamento no banco.
    suspend fun adicionar(lancamento: Lancamento) {
        lancamentoDao.inserir(lancamento.toEntity())
    }

    // Atualiza o registro identificado pelo ID.
    // Retorna false se nenhum registro tiver sido atualizado.
    suspend fun atualizar(lancamento: Lancamento): Boolean {
        val quantidadeAtualizada =
            lancamentoDao.atualizar(lancamento.toEntity())

        return quantidadeAtualizada > 0
    }

    // Converte o modelo da aplicação para a entidade do Room.
    private fun Lancamento.toEntity(): LancamentoEntity {
        return LancamentoEntity(
            id = id,
            data = data,
            valor = valor,
            tipo = tipo.name
        )
    }

    // Converte a entidade do banco para o modelo da aplicação.
    private fun LancamentoEntity.toModel(): Lancamento {
        return Lancamento(
            id = id,
            data = data,
            valor = valor,
            tipo = TipoLancamento.valueOf(tipo)
        )
    }
}
