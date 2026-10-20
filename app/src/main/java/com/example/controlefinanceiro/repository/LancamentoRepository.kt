
package com.example.controlefinanceiro.repository

import com.example.controlefinanceiro.model.Lancamento

class LancamentoRepository {

    // Armazena os lançamentos temporariamente em memória.
    private val lancamentos = mutableListOf<Lancamento>()

    // Adiciona um lançamento e devolve uma cópia da lista.
    fun adicionar(lancamento: Lancamento): List<Lancamento> {
        lancamentos.add(lancamento)
        return lancamentos.toList()
    }

    // Retorna os lançamentos cadastrados.
    fun listar(): List<Lancamento> {
        return lancamentos.toList()
    }

    // Atualiza o lançamento que possui o mesmo identificador.
    // Retorna null se o lançamento não for encontrado.
    fun atualizar(lancamentoAtualizado: Lancamento): List<Lancamento>? {
        val indice = lancamentos.indexOfFirst {
            it.id == lancamentoAtualizado.id
        }

        if (indice == -1) {
            return null
        }

        lancamentos[indice] = lancamentoAtualizado

        return lancamentos.toList()
    }
}
