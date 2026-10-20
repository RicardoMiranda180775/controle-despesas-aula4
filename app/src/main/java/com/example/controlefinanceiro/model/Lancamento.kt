package com.example.controlefinanceiro.model

import java.util.UUID

// Enum: restringe o tipo do lançamento às duas opções válidas do app.
enum class TipoLancamento {
    ENTRADA,
    SAIDA
}

// Data class: representa os dados de um lançamento financeiro.
// Ela já cria automaticamente recursos úteis, como toString(), equals() e copy().
data class Lancamento(
    // A data ainda é texto nesta etapa do curso. Futuramente poderá ser LocalDate.
    val data: String,
    // O valor é numérico para permitir cálculos, como a soma do saldo.
    val valor: Double,
    // O tipo usa o enum acima, evitando textos livres como "entrada" ou "Entrada".
    val tipo: TipoLancamento,
    // Cada lançamento recebe um identificador único por padrão.
    val id: String = UUID.randomUUID().toString()
)
