---
paths:
  - "app/src/test/**"
  - "app/src/androidTest/**"
---
# Regras de teste

- `src/test` = testes unitários JVM (JUnit) para **lógica pura** (sorteio, validação,
  serialização, timer). Sem dependências de Android/Context aqui.
- `src/androidTest` = testes instrumentados (Compose UI Test `createComposeRule` / Espresso)
  para telas e fluxo.
- Escreva o teste **antes** da implementação (RED) e confirme que falha; só então implemente (GREEN).
- Teste **comportamento**, não implementação: um teste que passa sem exercitar a regra é BLOQUEANTE.
- Isole aleatoriedade injetando `kotlin.random.Random(seed)` para asserts determinísticos.
- Rode com `./gradlew testDebugUnitTest` (unit) e `./gradlew connectedDebugAndroidTest` (instrumentado).
- Não afrouxe/ignore testes (`@Ignore`, asserts triviais) para "fazer passar".
