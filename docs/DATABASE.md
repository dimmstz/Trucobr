# Dados — planejamento

Entidades previstas para fases posteriores:
- profiles: perfil público e preferências.
- player_stats: estatísticas agregadas.
- game_rooms: configuração e estado das salas.
- game_players: assentos e participantes.
- matches: estado e resultado das partidas.
- game_events: sequência auditável de eventos.
- rankings: classificações sazonais.
- tournaments: torneios com fichas virtuais.
- virtual_wallets: saldo de fichas virtuais.
- wallet_transactions: livro-razão de movimentações.
- friendships: relações entre jogadores.
- reports: denúncias e moderação.

## Integridade
Saldo e premiações serão alterados somente por operações transacionais no
servidor. Operações serão idempotentes e terão identificador único. O cliente
nunca poderá definir saldo, vencedor ou cartas.
