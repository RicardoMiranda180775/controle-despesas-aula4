
package com.example.controlefinanceiro.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlefinanceiro.model.Lancamento
import com.example.controlefinanceiro.model.TipoLancamento
import com.example.controlefinanceiro.viewmodel.ControleDespesasViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControleDespesasScreen(
    viewModel: ControleDespesasViewModel = viewModel()
) {
    var data by remember { mutableStateOf("") }
    var valorTexto by remember { mutableStateOf("") }
    var tipoSelecionado by remember {
        mutableStateOf(TipoLancamento.SAIDA)
    }

    // Guarda o lançamento que está sendo editado.
    var lancamentoEmEdicao by remember {
        mutableStateOf<Lancamento?>(null)
    }

    // A tela observa o estado publicado pela ViewModel.
    val estado by viewModel.uiState
    val lancamentos = estado.lancamentos
    val saldo = estado.saldo

    // Prepara o formulário para editar um lançamento existente.
    fun iniciarEdicao(lancamento: Lancamento) {
        lancamentoEmEdicao = lancamento
        data = lancamento.data
        valorTexto = lancamento.valor.toString()
        tipoSelecionado = lancamento.tipo
    }

    // Limpa o formulário e sai do modo de edição.
    fun cancelarEdicao() {
        lancamentoEmEdicao = null
        data = ""
        valorTexto = ""
        tipoSelecionado = TipoLancamento.SAIDA
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Controle de despesas") }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = if (lancamentoEmEdicao == null) {
                        "Novo lançamento"
                    } else {
                        "Editar lançamento"
                    },
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it },
                    label = { Text("Data") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = valorTexto,
                    onValueChange = { valorTexto = it },
                    label = { Text("Valor") },
                    prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Text(
                    text = "Tipo",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    TipoButton(
                        texto = "Entrada",
                        selecionado =
                            tipoSelecionado == TipoLancamento.ENTRADA,
                        aoClicar = {
                            tipoSelecionado = TipoLancamento.ENTRADA
                        }
                    )

                    Spacer(Modifier.width(8.dp))

                    TipoButton(
                        texto = "Saída",
                        selecionado =
                            tipoSelecionado == TipoLancamento.SAIDA,
                        aoClicar = {
                            tipoSelecionado = TipoLancamento.SAIDA
                        }
                    )
                }
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = data.isNotBlank() &&
                            (valorComoDouble(valorTexto)?.let {
                                it > 0.0
                            } == true),
                    onClick = {
                        val valor = valorComoDouble(valorTexto) ?: return@Button

                        val lancamentoOriginal = lancamentoEmEdicao

                        if (lancamentoOriginal == null) {
                            // Cadastro de um novo lançamento.
                            viewModel.adicionarLancamento(
                                Lancamento(
                                    data = data,
                                    valor = valor,
                                    tipo = tipoSelecionado
                                )
                            )
                        } else {
                            // Mantém o ID original durante a edição.
                            viewModel.atualizarLancamento(
                                lancamentoOriginal.copy(
                                    data = data,
                                    valor = valor,
                                    tipo = tipoSelecionado
                                )
                            )
                        }

                        cancelarEdicao()
                    }
                ) {
                    Text(
                        text = if (lancamentoEmEdicao == null) {
                            "Adicionar lançamento"
                        } else {
                            "Salvar alterações"
                        }
                    )
                }

                if (lancamentoEmEdicao != null) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { cancelarEdicao() }
                    ) {
                        Text("Cancelar edição")
                    }
                }
            }

            item {
                Text(
                    text = "Meu saldo é : R$ %.2f".format(saldo),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Text(
                    text = "Lançamentos recentes",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (lancamentos.isEmpty()) {
                item {
                    Text("Nenhum lançamento adicionado ainda.")
                }
            } else {
                items(
                    items = lancamentos.reversed(),
                    key = { it.id }
                ) { lancamento ->
                    LancamentoItem(
                        lancamento = lancamento,
                        aoEditar = { iniciarEdicao(lancamento) }
                    )
                }
            }
        }
    }
}

// Aceita vírgula ou ponto como separador decimal.
private fun valorComoDouble(texto: String): Double? =
    texto.replace(',', '.').toDoubleOrNull()

@Composable
private fun TipoButton(
    texto: String,
    selecionado: Boolean,
    aoClicar: () -> Unit
) {
    if (selecionado) {
        Button(onClick = aoClicar) {
            Text(texto)
        }
    } else {
        OutlinedButton(onClick = aoClicar) {
            Text(texto)
        }
    }
}

@Composable
private fun LancamentoItem(
    lancamento: Lancamento,
    aoEditar: () -> Unit
) {
    val ehEntrada = lancamento.tipo == TipoLancamento.ENTRADA

    val cor = if (ehEntrada) {
        Color(0xFF1B5E20)
    } else {
        MaterialTheme.colorScheme.error
    }

    val sinal = if (ehEntrada) "+" else "-"
    val valorFormatado =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            .format(lancamento.valor)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(lancamento.data)

                Text(
                    text = if (ehEntrada) "Entrada" else "Saída",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(4.dp))

                OutlinedButton(onClick = aoEditar) {
                    Text("Editar")
                }
            }

            Text(
                text = "$sinal $valorFormatado",
                color = cor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
