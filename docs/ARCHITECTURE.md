# Arquitetura — Truco BR

## Princípios
1. O servidor será autoridade sobre cartas, turnos, resultados e saldos online.
2. Paulista e Mineiro serão perfis de regras independentes.
3. A interface não conterá regras críticas nem decidirá resultados.
4. A lógica de domínio deverá ser testável sem Android, rede ou banco.
5. Recursos financeiros reais ficam fora do MVP e bloqueados até revisão jurídica,
   regulatória e aprovação explícita.

## Camadas planejadas
- app: inicialização, navegação e composição das funcionalidades.
- core: utilitários, design system, persistência e rede.
- domain: modelos, regras, pontuação, matchmaking e bots.
- data: repositórios e implementações de acesso a dados.
- feature: autenticação, lobby, partida, perfil e ranking.
- backend: serviço autoritativo separado, API e WebSocket.

## Stack prevista
- Android: Kotlin, Jetpack Compose, Material 3 e Coroutines.
- Persistência local: Room e DataStore.
- Backend: Kotlin/Ktor e WebSocket.
- Dados remotos: PostgreSQL; Supabase poderá fornecer autenticação,
  banco gerenciado e armazenamento após avaliação de custos e limites.
- CI: GitHub Actions.

A Fase 1 contém somente o módulo Android mínimo e documentação. Módulos
adicionais serão criados quando houver implementação concreta, evitando
estrutura vazia excessiva.
