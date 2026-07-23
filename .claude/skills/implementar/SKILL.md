---
name: implementar
description: >
  Executa UMA task do PLAN.md com TDD e review automático ao final. Use quando o usuário
  quiser implementar/desenvolver uma task específica (ex.: "implementar task 2.1",
  "fazer a Task 1.2", "desenvolver a próxima task pendente"). Uso: /implementar [id da task].
---

# /implementar — Executa uma task do PLAN.md com TDD

Leia a task `$ARGUMENTS` no `PLAN.md`. Se `$ARGUMENTS` estiver vazio, use a próxima task
pendente (primeiro checkbox aberto respeitando dependências de fase).

Execute nesta **sequência obrigatória** (não avance sem confirmar a etapa anterior):

1. Leia `SPEC.md`, `CLAUDE.md` e a task no `PLAN.md` (Input, Output, Testes críticos).
2. Identifique o sub-agent responsável pelo campo `Agent` da task:
   - `game-logic` → lógica pura de jogo (`game/`)
   - `data-room` → dados/Room/JSON (`data/`, `assets/`)
   - `compose-ui` → telas/tema (`ui/screen/`, `ui/theme/`)
   - `qa-tester` → testes (`src/test`, `src/androidTest`)
3. Despache o agent (Task tool) para escrever os **testes críticos primeiro (RED)**.
4. Confirme que os testes **falham** antes de implementar
   (`./gradlew testDebugUnitTest` para lógica; instrumentado para UI).
5. Despache o agent para implementar o **mínimo** necessário para os testes passarem.
6. Confirme que os testes **passam (GREEN)** e que `./gradlew assembleDebug` compila.
7. Marque o checkbox da task no `PLAN.md` como concluído.
8. Invoque automaticamente `/review` (sub-agent `@code-reviewer`, contexto isolado).
9. Reporte o veredicto (contagem BLOQUEANTE/IMPORTANTE/SUGESTÃO) antes de encerrar.

Se houver item **BLOQUEANTE** no review, corrija (novo despacho ao mesmo agent) e revise
de novo antes de considerar a task concluída.

## Restrições
- Não implemente nada além da task pedida.
- Não modifique `SPEC.md`/`CLAUDE.md`. No `PLAN.md`, só marque o checkbox desta task.
- Respeite os gates de fase do `PLAN.md`.
