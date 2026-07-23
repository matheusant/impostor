---
name: review
description: >
  Invoca o code-reviewer contra o SPEC.md e o PLAN.md e classifica os problemas em
  BLOQUEANTE, IMPORTANTE e SUGESTÃO. Use ao final de qualquer implementação para validar
  aderência, cobertura de testes e qualidade. Use sempre que o usuário pedir review,
  revisão, verificação ou quiser saber se o código está correto.
---

# /review — Revisão contra o SPEC e o PLAN

Leia `SPEC.md`, `CLAUDE.md` e `PLAN.md` na raiz do projeto.

Invoque o sub-agent `@code-reviewer` (via Task tool, **contexto isolado** — ele não deve
ver a conversa de implementação) para revisar o que foi implementado. Passe como alvo o
diff atual / arquivos recém-alterados (ou `$ARGUMENTS`, se o usuário especificar um escopo).

O reviewer avalia:
1. **Aderência ao SPEC** — o implementado corresponde ao especificado? Falta funcionalidade essencial?
2. **Aderência ao CLAUDE.md/rules** — nomes de domínio, paleta `Spy*`, composables stateless, Room com Flow/suspend, libs no version catalog.
3. **Cobertura de testes** — os testes críticos do PLAN existem? Testam comportamento ou passam por acidente?
4. **Qualidade** — duplicação, recomposição desnecessária, edge cases (categoria vazia, JSON corrompido).
5. **Veredicto** — classifique cada problema:
   - **BLOQUEANTE:** impede o funcionamento
   - **IMPORTANTE:** corrigir antes da entrega
   - **SUGESTÃO:** melhoria para o próximo ciclo

**Não corrija nada** — apenas liste os problemas. Ao final, apresente a contagem por
categoria e o veredicto geral (APROVADO / REPROVADO com bloqueantes). Registre as
SUGESTÕES para não virarem dívida técnica esquecida.
