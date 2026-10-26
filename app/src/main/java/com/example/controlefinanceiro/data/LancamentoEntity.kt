package com.example.controlefinanceiro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity informa ao Room que esta classe representa uma tabela do banco.
@Entity(tableName = "lancamentos")
data class LancamentoEntity(

    // Chave primária que identifica cada registro de forma única.
    // O mesmo ID será preservado quando um lançamento for editado.
    @PrimaryKey
    val id: String,

    // Data do lançamento. Nesta etapa, continua sendo texto.
    val data: String,

    // Valor numérico utilizado no cálculo do saldo.
    val valor: Double,

    // Tipo armazenado como texto: ENTRADA ou SAIDA.
    val tipo: String
)
