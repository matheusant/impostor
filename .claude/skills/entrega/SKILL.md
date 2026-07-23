---
name: entrega
description: >
  Fecha a sessão verificando testes, checkboxes do PLAN.md, review pendente e gerando o
  relatório de próxima sessão. Use ao final de cada sessão de desenvolvimento ou quando o
  usuário quiser encerrar o trabalho do dia / preparar o projeto para entrega.
---

# /entrega — Gates de encerramento da sessão

Execute os gates em sequência. Se um gate falhar, **reporte e não avance**.

## 1. TESTES
Rode a suíte definida no `CLAUDE.md`:
- `./gradlew testDebugUnitTest` (unitários)
- `./gradlew assembleDebug` (compila / smoke test)
- Se houver device/emulador conectado: `./gradlew connectedDebugAndroidTest`
Se qualquer um falhar, reporte a saída e pare.

## 2. CHECKBOXES
Liste todas as tasks ainda abertas no `PLAN.md`. Se houver tasks da fase atual em aberto,
reporte quais e por quê.

## 3. REVIEW PENDENTE
Verifique se há itens **BLOQUEANTES** pendentes do último `/review`. Se houver, reporte
antes de encerrar.

## 4. PRÓXIMA SESSÃO
Atualize a seção `## Próxima sessão` no `PLAN.md` com:
- Tasks concluídas nesta sessão
- Tasks ainda abertas
- O que precisa estar pronto para continuar
- Decisões que ficaram em aberto

## 5. SETUP VERIFICADO
Confirme que os comandos de setup do `CLAUDE.md` funcionam e que o projeto compila do zero
(`./gradlew assembleDebug`), de modo que outra pessoa consiga clonar e rodar.

Ao concluir todos os gates, reporte o status final e confirme que o projeto está pronto
para entrega.
