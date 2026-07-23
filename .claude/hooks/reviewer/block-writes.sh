#!/usr/bin/env bash
# PreToolUse (matcher Write|Edit|Bash) do agent code-reviewer.
# O reviewer é SOMENTE LEITURA: bloqueia qualquer tentativa de escrever/editar/executar.
echo "code-reviewer é somente leitura: não pode escrever, editar nem executar comandos (Write/Edit/Bash bloqueados). Apenas leia, analise e reporte." >&2
exit 2
