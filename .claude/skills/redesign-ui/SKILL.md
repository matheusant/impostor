---
name: redesign-ui
description: >
  Conduz um redesenho visual das telas Compose do Impostor com feedback sensorial real via
  screenshots do device (adb), evitando estética genérica de IA ("AI slop") e comprometendo
  o app com uma direção estética específica (espião). Use quando o usuário quiser melhorar o
  visual, redesenhar a UI, ajustar o design ou o "look and feel" do app.
---

# /redesign-ui — Redesenho visual guiado por screenshots

Adaptação Android/Compose do fluxo de redesign: em vez de Playwright/browser, o "olho" do
agent são **screenshots do device via adb** (`adb exec-out screencap -p > shot.png` + Read).
Sem MCP.

## Etapa 1 — Diagnóstico visual (UI-PROBLEMS.md)
1. Garanta o app instalado (use `/build-instalar`).
2. Para cada tela principal (Setup, CategorySelect, CategoryForm, PassPhone, GamePlay):
   navegue no device (`adb shell input ...` ou manualmente) e capture `shot.png`; Read o PNG.
3. Para cada tela, registre em `UI-PROBLEMS.md` (uma seção por tela):
   - Padrões de "AI slop" presentes (tipografia genérica, cores sem intenção, cards de peso igual, hierarquia fraca).
   - O que falta em hierarquia visual, o que está inconsistente entre telas, o que prejudica legibilidade.
   Seja específico: não "está genérico", mas "PassPhone: rótulo e diretriz têm o mesmo peso; espaçamento apertado".

## Etapa 2 — Direção estética (não aceite "moderno e limpo")
Antes de qualquer código, comprometa-se com uma direção **específica**, coerente com a
identidade espião já existente (monospace, `SpyBlack/Gray/Green/Red/TextWhite`). Apresente:
- Escala tipográfica e pesos (mantendo `FontFamily.Monospace` como assinatura).
- Uso intencional da paleta `Spy*` (o que é verde=seguro, vermelho=alerta/infiltrado).
- Hierarquia, espaçamento e movimento (transições) por tela.
- O que muda e o que permanece. **Peça aprovação** antes de implementar.

## Etapa 3 — Implementar
Delegue as mudanças ao agent `compose-ui`, uma tela por vez, mantendo composables stateless
e **sem hardcode de cores** (só a paleta `Spy*` de `ui/theme/Color.kt`). `./gradlew assembleDebug` deve continuar verde.

## Etapa 4 — Validar (loop visual)
Reinstale, capture novos screenshots e compare com a direção aprovada. Para cada
discrepância: descreva, corrija, capture de novo. Repita até todas as telas alinharem.

## Restrições
- Não introduza novas fontes/bibliotecas de design sem aprovação.
- Não altere dados nem lógica de jogo — apenas apresentação.
- `UI-PROBLEMS.md` e `shot*.png` são temporários (já estão no `.claudeignore`).
