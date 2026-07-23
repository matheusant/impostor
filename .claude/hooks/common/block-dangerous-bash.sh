#!/usr/bin/env bash
# PreToolUse (matcher Bash) dos agents de implementação (compose-ui, data-room, game-logic).
# Bloqueia comandos destrutivos/fora de escopo. Sem jq: extrai o campo "command" do JSON.
INPUT="$(cat)"
CMD="$(printf '%s' "$INPUT" | grep -oE '"command"[[:space:]]*:[[:space:]]*"([^"\\]|\\.)*"' | head -1)"
[ -z "$CMD" ] && CMD="$INPUT"

if printf '%s' "$CMD" | grep -Eiq 'rm[[:space:]]+-[a-z]*r[a-z]*f|rm[[:space:]]+-[a-z]*f[a-z]*r|git[[:space:]]+push|git[[:space:]]+reset[[:space:]]+--hard|git[[:space:]]+commit|git[[:space:]]+clean|Remove-Item|(^|[^a-z])del[[:space:]]+/|mkfs|shutdown|reboot'; then
  echo "Hook de escopo: comando destrutivo/fora de escopo bloqueado (rm -rf, git push/commit/reset --hard, del /, mkfs, etc.). Se for realmente necessário, peça ao usuário." >&2
  exit 2
fi
exit 0
