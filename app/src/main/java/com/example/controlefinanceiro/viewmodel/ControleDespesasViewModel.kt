
package com.example.controlefinanceiro.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.controlefinanceiro.model.Lancamento
import com.example.controlefinanceiro.model.TipoLancamento
import com.example.controlefinanceiro.repository.LancamentoRepository

data class ControleDespesasUiState(
    val lancamentos: List<Lancamento> = emptyList(),
    val saldo: Double = 0.0
)

class ControleDespesasViewModel(
    private val repository: LancamentoRepository = LancamentoRepository()
) : ViewModel() {

    private val _uiState = mutableStateOf(
        ControleDespesasUiState(
            lancamentos = repository.listar()
        )
    )

    val uiState: State<ControleDespesasUiState> = _uiState

    fun adicionarLancamento(lancamento: Lancamento) {
        // O Repository passa a gerenciar a inclusão do lançamento.
        val lancamentosAtualizados = repository.adicionar(lancamento)

        // A ViewModel atualiza o estado que a interface observa.
        _uiState.value = ControleDespesasUiState(
            lancamentos = lancamentosAtualizados,
            saldo = calcularSaldo(lancamentosAtualizados)
        )
    }

    private fun calcularSaldo(lancamentos: List<Lancamento>): Double {
        return lancamentos.sumOf { lancamento ->
            if (lancamento.tipo == TipoLancamento.ENTRADA) {
                lancamento.valor
            } else {
                -lancamento.valor
            }
        }
    }
}
