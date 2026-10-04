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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.controlefinanceiro.model.Lancamento
import com.example.controlefinanceiro.model.TipoLancamento
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControleDespesasScreen() {
    // "var" é usado porque o usuário pode alterar estes valores.
    // remember mantém o valor enquanto esta tela estiver na composição.
    // mutableStateOf avisa o Compose para redesenhar a tela quando o valor mudar.
    var data by remember { mutableStateOf("") }
    var valorTexto by remember { mutableStateOf("") }
    var tipoSelecionado by remember { mutableStateOf(TipoLancamento.SAIDA) }
//    var saldo by remember { mutableStateOf("") }

    // Lista observável: quando adicionamos um item, o Compose atualiza a LazyColumn.
    // Os dados existem apenas enquanto o app estiver aberto; ainda não usamos banco de dados.
    val lancamentos = remember { mutableStateListOf<Lancamento>() }
    val saldo = calculaSaldo(lancamentos)


    Scaffold(
        // Scaffold organiza estruturas comuns de uma tela, como barra superior e conteúdo.
        topBar = { TopAppBar(title = { Text("Controle de despesas") }) }
    ) { paddingValues ->
        // LazyColumn mostra uma lista vertical e cria na tela apenas os itens necessários.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text("Novo lançamento", style = MaterialTheme.typography.titleLarge)
            }

            item {
                // O campo guarda texto; a conversão para número acontece ao adicionar o lançamento.
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Text("Tipo", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    TipoButton(
                        texto = "Entrada",
                        selecionado = tipoSelecionado == TipoLancamento.ENTRADA,
                        aoClicar = { tipoSelecionado = TipoLancamento.ENTRADA }
                    )
                    Spacer(Modifier.width(8.dp))
                    TipoButton(
                        texto = "Saída",
                        selecionado = tipoSelecionado == TipoLancamento.SAIDA,
                        aoClicar = { tipoSelecionado = TipoLancamento.SAIDA }
                    )
                }
            }

            item {
                // Só permite adicionar quando há data e o valor digitado pode virar Double.
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = data.isNotBlank() && valorComoDouble(valorTexto) != null,
                    onClick = {
                        // Cria um objeto Lancamento com os valores atuais do formulário.
                        lancamentos.add(
                            Lancamento(
                                data = data,
                                valor = valorComoDouble(valorTexto)!!,
                                tipo = tipoSelecionado
                            )
                        )
                        // Limpa o campo para preparar o próximo cadastro.
                        valorTexto = ""
                    }
                ) {
                    Text("Adicionar lançamento")
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
                item { Text("Nenhum lançamento adicionado ainda.") }
            } else {
                // reversed mostra o lançamento mais recente no topo da lista.
                items(lancamentos.reversed()) { lancamento ->
                    LancamentoItem(lancamento)
                }
            }
        }
    }
}

// Aceita vírgula ou ponto decimal, pois o usuário brasileiro costuma digitar "10,50".
private fun valorComoDouble(texto: String): Double? =
    texto.replace(',', '.').toDoubleOrNull()

@Composable
private fun TipoButton(texto: String, selecionado: Boolean, aoClicar: () -> Unit) {
    // O botão selecionado tem preenchimento; o outro fica apenas contornado.
    if (selecionado) {
        Button(onClick = aoClicar) { Text(texto) }
    } else {
        OutlinedButton(onClick = aoClicar) { Text(texto) }
    }
}

@Composable
private fun LancamentoItem(lancamento: Lancamento) {
    // A aparência do item muda de acordo com seu tipo.
    val ehEntrada = lancamento.tipo == TipoLancamento.ENTRADA
    val cor = if (ehEntrada) Color(0xFF1B5E20) else MaterialTheme.colorScheme.error
    val sinal = if (ehEntrada) "+" else "-"
    // Formata o Double no padrão brasileiro, por exemplo: R$ 10,50.
    val valorFormatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        .format(lancamento.valor)

    Card(modifier = Modifier.fillMaxWidth()) {
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
            }
            Text(
                text = "$sinal $valorFormatado",
                color = cor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun calculaSaldo(lancamento: List<Lancamento>): Double{
    return lancamento.sumOf {
        if (it.tipo == TipoLancamento.ENTRADA){
            it.valor
        } else {
            -it.valor
        }
    }
}

