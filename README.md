# Controle de despesas — Aula 4

Projeto Android completo em Kotlin + Jetpack Compose. Ele cadastra entradas e saídas em memória e as exibe em uma lista.

## Como usar

1. Descompacte o arquivo ZIP em uma pasta local.
2. No Android Studio, selecione **Open** e abra a pasta `controle-despesas-aula4`.
3. Aguarde a sincronização do Gradle. Ela exige internet apenas na primeira abertura, para baixar dependências.
4. Execute em um celular Android conectado por USB ou em um emulador.

O projeto pede Android SDK 35, instalado normalmente pelo Android Studio.

## Objetivo da aula

Ao fim da aula, a turma terá uma tela que cadastra lançamentos em memória, com data, valor e tipo (entrada ou saída), e mostra-os em uma lista.

## Roteiro de construção ao vivo

1. Apresente `TipoLancamento` e `Lancamento` em `model/Lancamento.kt`.
2. Monte a tela estática: título, dois campos e os botões de tipo.
3. Adicione os estados `data`, `valorTexto` e `tipoSelecionado` com `remember` e `mutableStateOf`.
4. Crie `lancamentos` com `mutableStateListOf`.
5. No botão, converta o texto para `Double` e inclua um `Lancamento` na lista.
6. Exiba a lista com `LazyColumn` e `LancamentoItem`.

## Arquitetura adequada agora

```text
model/Lancamento.kt       regra e formato dos dados
ui/ControleDespesasScreen.kt   tela e estado local da tela
MainActivity.kt           ponto de entrada do Android
```

É uma separação suficiente para apresentar a ideia de responsabilidade sem antecipar complexidade. O estado fica na tela porque ainda é temporário e só é usado por ela.

Na próxima etapa, quando houver rotação de tela, mais de uma tela, testes ou persistência, o estado pode migrar para um `ViewModel`. Quando os dados forem salvos, entra um `Repository` ligado a Room ou a uma API.

## Desafios para a turma

- Impedir valor menor ou igual a zero.
- Limpar também a data após adicionar.
- Mostrar o saldo total, somando entradas e subtraindo saídas.
- Criar um botão para remover um lançamento.
- Trocar a digitação da data por um seletor de datas em uma aula futura.






Melhorias
Definição de Atividades (Screen Flow)
 - Modelagem dos Tipos de Dados e Categorias (Classes)
 - Classe de Categoria: Criar a estrutura/classe Categoria (contendo id, nome, icone e tipoTransacao associado) para vincular cada movimentação a um tipo de gasto/ganho.
 - Enum TipoTransacao: Definir as opções fixas ENTRADA e SAIDA.
 - Data Class da Receita/Transação: Criar o modelo principal unificando id (UUID), valor (BigDecimal), dataHora (Calendar/LocalDateTime), tipo e categoriaId.
 - Validações e Regras de Negócio (Domain Layer)

 - MainActivity (Hub / Menu Principal): Exibe o saldo total acumulado, acessos rápidos e os 10 últimos lançamentos no histórico.
 - LancamentoActivity (Formulário / Create e Update): Tela responsável pelo cadastro ou edição de entradas e saídas.
 - ExtratoActivity (Histórico Completo / Read e Delete): Tela com a lista completa de movimentações, busca e filtros por data ou categoria.
 - Ajuste do Sinal Financeiro: Inverter o sinal conforme o tipo (- para SAIDA e + para ENTRADA).
 - Validação do Valor: Bloquear valores zerados, negativos ou campo em branco.
 - Validação de Data/Hora: Travar o seletor para impedir o cadastro de lançamentos no futuro.
 - Cálculo de Saldo Total: Função para somar entradas e subtrair saídas acumuladas.
 - Interface e Usabilidade (UX/UI)
 - Formatação Monetária: Exibir valores usando a moeda local (R$).
 - Componentes DatePicker e TimePicker: Substituir campos de texto manual por seletores de calendário e relógio nativos.
 - Lista com RecyclerView: Exibir os itens do extrato e do Hub de forma performática.
 - Diálogo de Confirmação: AlertDialog para pedir confirmação ao deletar ou alterar um registro.
 - Persistência Local e Arquitetura
 - Banco de Dados (Room Database): Implementar o Room para salvar o histórico no dispositivo de forma persistente.
 - Camada de Repositório (TransacaoRepository): Centralizar as chamadas de banco e isolar o acesso aos dados da interface.
 - Padrão MVVM (ViewModel): Gerenciar o saldo, as validações e a lista de movimentações sem perda de dados no ciclo de vida da tela.



 - AP -> drummond.org

