# Truco BR

Jogo Android nativo de truco brasileiro. Esta primeira entrega estabelece a fundação técnica e a automação de validação; regras completas, bots e multiplayer serão desenvolvidos em fases posteriores.

## Fase 1
- Android nativo com Kotlin, Jetpack Compose e Material 3.
- Identificador: `com.dimm.trucobr`.
- Documentação de arquitetura, regras, dados e segurança.
- GitHub Actions para compilar, testar e executar lint.
- Sem apostas, depósitos, saques ou conversão de fichas em dinheiro.

## Requisitos
- JDK 17
- Android SDK 35
- Gradle 8.9

## Build local
```bash
gradle assembleDebug
gradle testDebugUnitTest
gradle lint
```

## CI
O workflow `.github/workflows/android-ci.yml` roda em pushes e pull requests para `main` e `develop`, compila o APK debug, executa testes e lint, e publica o APK como artefato.

## Estrutura
```
app/       Aplicativo Android
docs/      Arquitetura, regras, dados e segurança
.github/   CI do GitHub Actions
```

## Estado
Fase 1 — fundação. Ainda não é um jogo jogável nem possui backend online.
