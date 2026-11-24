#!/usr/bin/env bash
# Gera docs/acesso/acesso.pdf com o link do sistema implantado e os logins.
# Uso: ./gerar-pdf.sh https://moeda-estudantil.vercel.app https://moeda-estudantil-api.onrender.com
set -euo pipefail
FRONT="${1:?informe a URL do frontend}"
API="${2:?informe a URL da API}"
DIR="$(cd "$(dirname "$0")" && pwd)"
CHROME="$(command -v google-chrome || command -v chromium || command -v chromium-browser)"
TMP="$(mktemp --suffix=.html)"
sed -e "s#{{FRONT}}#$FRONT#g" -e "s#{{API}}#$API#g" "$DIR/acesso.template.html" > "$TMP"
"$CHROME" --headless --disable-gpu --no-pdf-header-footer --print-to-pdf="$DIR/acesso.pdf" "file://$TMP" 2>/dev/null
rm -f "$TMP"
echo "PDF gerado em $DIR/acesso.pdf"
