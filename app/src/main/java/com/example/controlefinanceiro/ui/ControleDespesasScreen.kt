
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
    var data by remember { mutableStateOf(TextFieldValue("")) }
    var valorTexto by remember { mutableStateOf(TextFieldValue("")) }
    var tipoSelecionado by remember {
        mutableStateOf(TipoLancamento.SAIDA)
    }

    // Guarda o lançamento que está sendo editado.
    var lancamentoEmEdicao by remember {
        mutableStateOf<Lancamento?>(null)
    }

    var lancamentoParaExcluir by remember {
        mutableStateOf<Lancamento?>(null)
    }
    var confirmarExclusaoTodos by remember { mutableStateOf(false) }

    // A tela observa o estado publicado pela ViewModel.
    val estado by viewModel.uiState
    val lancamentos = estado.lancamentos
    val saldo = estado.saldo

    // Prepara o formulário para editar um lançamento existente.
    fun iniciarEdicao(lancamento: Lancamento) {
        lancamentoEmEdicao = lancamento
        data = TextFieldValue(
            text = lancamento.data,
            selection = TextRange(lancamento.data.length)
        )
        val valorFormatado = formatarValor(lancamento.valor)
        valorTexto = TextFieldValue(
            text = valorFormatado,
            selection = TextRange(valorFormatado.length)
        )
        tipoSelecionado = lancamento.tipo
    }

    // Limpa o formulário e sai do modo de edição.
    fun cancelarEdicao() {
        lancamentoEmEdicao = null
        data = TextFieldValue("")
        valorTexto = TextFieldValue("")
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
                    onValueChange = { data = formatarData(it) },
                    label = { Text("Data") },
                    placeholder = { Text("dd/MM/aaaa") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = valorTexto,
                    label = { Text("Valor") },
                    prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChange = { valorTexto = formatarValor(it) },
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
                    enabled = data.text.length == 10 &&
                            (valorComoDouble(valorTexto.text)?.let {
                                it > 0.0
                            } == true),
                    onClick = {
                        val valor = valorComoDouble(valorTexto.text)
                            ?: return@Button

                        val lancamentoOriginal = lancamentoEmEdicao

                        if (lancamentoOriginal == null) {
                            // Cadastro de um novo lançamento.
                            viewModel.adicionarLancamento(
                                Lancamento(
                                    data = data.text,
                                    valor = valor,
                                    tipo = tipoSelecionado
                                )
                            )
                        } else {
                            // Mantém o ID original durante a edição.
                            viewModel.atualizarLancamento(
                                lancamentoOriginal.copy(
                                    data = data.text,
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Lançamentos recentes",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (lancamentos.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { confirmarExclusaoTodos = true }
                        ) {
                            Text("Excluir todos")
                        }
                    }
                }
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
                        aoEditar = { iniciarEdicao(lancamento) },
                        aoExcluir = { lancamentoParaExcluir = lancamento }
                    )
                }
            }
        }
    }

    lancamentoParaExcluir?.let { lancamento ->
        AlertDialog(
            onDismissRequest = { lancamentoParaExcluir = null },
            title = { Text("Excluir lançamento?") },
            text = { Text("O lançamento será removido permanentemente.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.excluirLancamento(lancamento.id)
                        if (lancamentoEmEdicao?.id == lancamento.id) {
                            cancelarEdicao()
                        }
                        lancamentoParaExcluir = null
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { lancamentoParaExcluir = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (confirmarExclusaoTodos) {
        AlertDialog(
            onDismissRequest = { confirmarExclusaoTodos = false },
            title = { Text("Excluir todos os lançamentos?") },
            text = {
                Text("Todos os lançamentos serão removidos permanentemente.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.excluirTodosLancamentos()
                        cancelarEdicao()
                        confirmarExclusaoTodos = false
                    }
                ) {
                    Text("Excluir todos")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusaoTodos = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Formata a data conforme os dígitos são digitados.
private fun formatarData(valor: TextFieldValue): TextFieldValue {
    val digitos = valor.text.filter(Char::isDigit).take(8)

    val textoFormatado = buildString {
        append(digitos.take(2))

        if (digitos.length > 2) {
            append('/')
            append(digitos.drop(2).take(2))
        }

        if (digitos.length > 4) {
            append('/')
            append(digitos.drop(4).take(4))
        }
    }

    return TextFieldValue(
        text = textoFormatado,
        selection = TextRange(
            mapearCursorData(valor.text, valor.selection.start, textoFormatado),
            mapearCursorData(valor.text, valor.selection.end, textoFormatado)
        )
    )
}

// Formata os dígitos como valor em reais, considerando os dois últimos como centavos.
private fun formatarValor(valor: TextFieldValue): TextFieldValue {
    val digitos = valor.text.filter(Char::isDigit).take(15)
    val centavos = digitos.toLongOrNull() ?: return TextFieldValue("")
    val textoFormatado = formatarValor(centavos / 100.0)

    return TextFieldValue(
        text = textoFormatado,
        selection = TextRange(
            mapearCursor(valor.text, valor.selection.start, textoFormatado),
            mapearCursor(valor.text, valor.selection.end, textoFormatado)
        )
    )
}

private fun formatarValor(valor: Double): String {
    val formatador = NumberFormat.getNumberInstance(Locale("pt", "BR")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    return formatador.format(valor)
}

private fun mapearCursorData(
    textoOriginal: String,
    posicaoOriginal: Int,
    textoFormatado: String
): Int {
    if (posicaoOriginal >= textoOriginal.length) return textoFormatado.length

    val quantidadeDigitos = textoOriginal
        .take(posicaoOriginal)
        .count(Char::isDigit)

    var posicao = quantidadeDigitos
    if (textoFormatado.length > 2 && quantidadeDigitos >= 2) posicao++
    if (textoFormatado.length > 5 && quantidadeDigitos >= 4) posicao++

    return posicao.coerceAtMost(textoFormatado.length)
}

private fun mapearCursor(
    textoOriginal: String,
    posicaoOriginal: Int,
    textoFormatado: String
): Int {
    if (posicaoOriginal >= textoOriginal.length) return textoFormatado.length

    val quantidadeDigitos = textoOriginal
        .take(posicaoOriginal)
        .count(Char::isDigit)
    if (quantidadeDigitos == 0) return 0

    var digitosMapeados = 0
    textoFormatado.forEachIndexed { index, caractere ->
        if (caractere.isDigit()) {
            digitosMapeados++
            if (digitosMapeados == quantidadeDigitos) return index + 1
        }
    }

    return textoFormatado.length
}

// Converte o valor formatado em reais para Double.
private fun valorComoDouble(texto: String): Double? =
    texto.replace(".", "").replace(',', '.').toDoubleOrNull()

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
    aoEditar: () -> Unit,
    aoExcluir: () -> Unit
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

                Row {
                    OutlinedButton(onClick = aoEditar) {
                        Text("Editar")
                    }

                    Spacer(Modifier.width(8.dp))

                    OutlinedButton(onClick = aoExcluir) {
                        Text("Excluir")
                    }
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
