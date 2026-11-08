# Especificação do motor de cartas — Fase 2

## Modalidades
- Truco Paulista e Truco Mineiro são perfis separados no código.
- Nesta implementação inicial, ambos usam manilha definida pela vira, conforme decisão do produto.
- O motor não presume diferenças regionais de pontuação, mão de onze, mão de ferro ou desempate sem validação específica.

## Baralho e força das cartas
- Baralho de 40 cartas: 4, 5, 6, 7, Q, J, K, A, 2 e 3 em cada naipe.
- Ordem das cartas comuns, da menor para a maior: 4, 5, 6, 7, Q, J, K, A, 2 e 3.
- A manilha é a carta imediatamente seguinte à vira nessa sequência; depois do 3, a sequência retorna ao 4.
- Ordem de naipes das manilhas nesta base: ouros < espadas < copas < paus.
- Naipes não desempatarão cartas comuns de mesmo valor.

## Distribuição e rodada
- Suporte inicial para mesas de 2 ou 4 jogadores.
- Cada jogador recebe 3 cartas; uma carta seguinte é revelada como vira.
- O embaralhamento é isolado da comparação de cartas e aceita fonte aleatória injetável para testes.
- O resolvedor compara uma rodada individual e identifica vencedor ou empate.

## Ainda não implementado
- Resolução completa das três rodadas e critérios regionais de desempate.
- Pontuação da mão, pedidos de truco e progressão de pontos.
- Mão de onze, mão de ferro, abandono e regras opcionais.
- Interface de mesa, bots, partidas online e persistência.

## Segurança
- O objeto de distribuição contém todas as mãos e é estado privado do servidor.
- Não enviar o objeto completo de distribuição aos clientes. Cada jogador deve receber apenas sua própria mão, a vira e os dados públicos da partida.
