
package com.example.controlefinanceiro.repository

import com.example.controlefinanceiro.model.Lancamento

class LancamentoRepository {

    // Armazena os lançamentos temporariamente em memória.
    private val lancamentos = mutableListOf<Lancamento>()

    // Adiciona um lançamento e devolve uma cópia da lista atualizada.
    fun adicionar(lancamento: Lancamento): List<Lancamento> {
        lancamentos.add(lancamento)
        return lancamentos.toList()
    }

    // Retorna os lançamentos cadastrados.
    fun listar(): List<Lancamento> {
        return lancamentos.toList()
    }
}
