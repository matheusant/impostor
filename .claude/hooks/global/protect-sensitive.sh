#!/usr/bin/env bash
# Hook GLOBAL (settings.json), PreToolUse matcher Edit|Write.
# Bloqueia escrita/edição em arquivos sensíveis ou gerados.
# Sem jq: extrai APENAS o campo "file_path" do JSON (nunca o "content"), para não dar
# falso-positivo quando o conteúdo do arquivo menciona esses caminhos.
INPUT="$(cat)"
FP="$(printf '%s' "$INPUT" | grep -oE '"file_path"[[:space:]]*:[[:space:]]*"([^"\\]|\\.)*"' | head -1)"

if [ -n "$FP" ] && printf '%s' "$FP" | grep -Eiq 'local\.properties|\.jks|\.keystore|app[/\\]+build[/\\]|[/\\]build[/\\]|\.gradle[/\\]|[/\\]\.idea[/\\]'; then
  echo "Hook global: edição bloqueada — arquivo sensível ou gerado (local.properties, *.jks/*.keystore, build/, .gradle/, .idea/): $FP" >&2
  exit 2
fi
exit 0
