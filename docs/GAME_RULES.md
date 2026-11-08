# Especificação de regras — pendente de aprovação

Paulista e Mineiro serão implementados separadamente. Não codificar regras
definitivas antes de validar a variante pretendida.

## Pontos a aprovar
- Hierarquia completa das cartas em cada modalidade.
- Ordem e definição das manilhas.
- Valor da mão e progressão dos pedidos.
- Regras da mão de onze e mão de ferro.
- Critérios de empate em cada rodada e na mão.
- Regra de empate entre primeira, segunda e terceira rodadas.
- Variações regionais e opções de mesa.

## Requisitos do motor
- Funções determinísticas para comparar cartas e resolver rodadas.
- Baralho e embaralhamento isolados da lógica de pontuação.
- Testes unitários cobrindo combinações de empate.
- Eventos de jogo imutáveis para auditoria e replay.
- Nenhuma carta oculta exposta a adversários.
