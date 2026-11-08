package com.example.controlefinanceiro.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlefinanceiro.data.ControleDespesasDatabase
import com.example.controlefinanceiro.model.Lancamento
import com.example.controlefinanceiro.model.TipoLancamento
import com.example.controlefinanceiro.repository.LancamentoRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class ControleDespesasUiState(
    val lancamentos: List<Lancamento> = emptyList(),
    val saldo: Double = 0.0,
    val erro: String? = null
)

class ControleDespesasViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Obtém o DAO a partir da instância compartilhada do banco Room.
    private val lancamentoDao =
        ControleDespesasDatabase
            .getDatabase(application)
            .lancamentoDao()

    // O Repository é responsável pelo acesso aos dados.
    private val repository = LancamentoRepository(lancamentoDao)

    private val _uiState = mutableStateOf(ControleDespesasUiState())
    val uiState: State<ControleDespesasUiState> = _uiState

    init {
        // Observa as mudanças do banco e mantém a tela atualizada.
        viewModelScope.launch {
            repository.observarTodos()
                .catch { erro ->
                    _uiState.value = _uiState.value.copy(
                        erro = erro.message ?: "Erro ao carregar lançamentos."
                    )
                }
                .collect { lancamentos ->
                    _uiState.value = ControleDespesasUiState(
                        lancamentos = lancamentos,
                        saldo = calcularSaldo(lancamentos)
                    )
                }
        }
    }

    fun adicionarLancamento(lancamento: Lancamento) {
        viewModelScope.launch {
            try {
                repository.adicionar(lancamento)
            } catch (erro: Exception) {
                _uiState.value = _uiState.value.copy(
                    erro = erro.message ?: "Erro ao adicionar lançamento."
                )
            }
        }
    }

    fun atualizarLancamento(lancamento: Lancamento) {
        viewModelScope.launch {
            try {
                val atualizado = repository.atualizar(lancamento)

                if (!atualizado) {
                    _uiState.value = _uiState.value.copy(
                        erro = "Lançamento não encontrado."
                    )
                }
            } catch (erro: Exception) {
                _uiState.value = _uiState.value.copy(
                    erro = erro.message ?: "Erro ao atualizar lançamento."
                )
            }
        }
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
