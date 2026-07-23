#!/usr/bin/env bash
# Hook GLOBAL (settings.json), PostToolUse matcher Write.
# Sempre que um arquivo é criado por uma ferramenta Write, faz `git add` nele —
# assim todo desenvolvimento deixa os arquivos criados já rastreados/staged.
# Sem jq: extrai APENAS o campo "file_path" do JSON (nunca o "content"), para não
# dar falso-positivo quando o conteúdo do arquivo mencionar um caminho.
# NUNCA bloqueia (sempre exit 0): erros do git add (arquivo no .gitignore, fora do
# repositório, etc.) são silenciados de propósito — este hook não é um guardrail.
INPUT="$(cat)"
FP="$(printf '%s' "$INPUT" | grep -oE '"file_path"[[:space:]]*:[[:space:]]*"([^"\\]|\\.)*"' | head -1)"

if [ -n "$FP" ]; then
  # extrai só o valor (o caminho): remove a chave e as aspas externas
  VAL="$(printf '%s' "$FP" | sed -E 's/^"file_path"[[:space:]]*:[[:space:]]*"//; s/"$//')"
  # normaliza barras do Windows para o git: "\\" (JSON) -> "\" -> "/"
  VAL="$(printf '%s' "$VAL" | sed 's/\\\\/\\/g' | sed 's#\\#/#g')"
  if [ -n "$VAL" ]; then
    git add -- "$VAL" 2>/dev/null || true
  fi
fi
exit 0
