# Motor de partidas — Fase 3

## Base comum
- Partida individual (1v1) ou em duplas (2v2), com duas equipes identificadas pelo servidor.
- Cada vaza só termina quando todos os assentos participantes entregam uma carta.
- Em 2v2, vence a equipe cuja melhor carta supera a melhor carta adversária; duas cartas altas da mesma equipe não causam empate artificial.
- A mão é melhor de três vazas. Regras de desempate implementadas: duas vitórias encerram a mão; empate na primeira é decidido pela próxima vaza não empatada; empate depois de uma vitória favorece quem venceu a primeira; se cada equipe vencer uma, a terceira decide; empate na terceira favorece quem venceu a primeira; três empates anulam a mão.
- O estado de apostas permite aumento, aceite e fuga. A equipe que pediu por último não pode aumentar novamente até a outra equipe responder e aumentar.

## Perfis iniciais de pontuação (configuráveis)
- Paulista: mão base 1; progressão 1 → 3 → 6 → 9 → 12; alvo 12; mão de onze aos 11; se correr, adversário recebe 1; se jogar, mão vale 3.
- Mineiro: baseline configurável 2 → 4 → 6 → 10 → 12; alvo 12; mão de dez aos 10; se correr, adversário recebe 2; se jogar, mão vale 4.
- Decisão explícita do produto: **ambos os perfis mantêm manilha definida pela vira**, apesar de referências tradicionais descreverem o Mineiro com manilhas fixas. A hierarquia e as regras de mesa devem ser confirmadas pelo usuário antes do lançamento público.
- Mão de ferro: quando ambas as equipes atingem o limite especial, a mão fica sem truco; quem vencer encerra a partida.
- Os parâmetros ficam no objeto `TrucoRules`, permitindo criar variantes de pontuação sem alterar o motor.

## Limites desta fase
- A interface ainda não aplica o motor.
- A validação da carta escolhida, ordem de turno, cartas cobertas, distribuição/rotação de mão e persistência devem ser orquestradas pelo servidor na fase online.
- Sinais, punições de torneio, abandono e exceções de regulamentos regionais ainda não são regras universais; não foram inventadas aqui.

## Segurança
- `teamByPlayer`, cartas privadas, placar e estado de aposta devem ser validados no servidor.
- O cliente não deve decidir o resultado de vazas ou pontuação.
