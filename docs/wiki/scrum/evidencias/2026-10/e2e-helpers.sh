# Helpers del E2E exploratorio con playwright-cli (Git Bash). Uso, desde la raíz del workspace:
#   npm install -g @playwright/cli@latest
#   source citas-api/docs/wiki/scrum/evidencias/2026-10/e2e-helpers.sh
#   start admin && login admin admin@demo.invalid && go admin /paciente/inicio
# La clave de laboratorio de las cuentas seed está documentada en database/reference/README_DB.md;
# los helpers la leen del seed y la enmascaran en toda salida.
ROOT="${ROOT:-$(pwd)}"
E2E_DIR="${E2E_DIR:-${TMPDIR:-/tmp}/fcv-e2e}"; mkdir -p "$E2E_DIR"
EVID="$ROOT/citas-api/docs/wiki/scrum/evidencias/2026-10"
WEB="${WEB:-http://localhost:4200}"
API="${API:-http://localhost:8080}"
LAB_PW=$(grep -m1 "admin@demo.invalid" "$ROOT/database/reference/db.sql" | grep -o -- '-- .*' | sed 's/-- //' | tr -d '\r ')

mask() { sed -e "s/${LAB_PW//\*/\\*}/********/g" -e 's/"\(accessToken\|refreshToken\)":"[^"]*"/"\1":"<redacted>"/g' -e 's/Bearer [A-Za-z0-9._-]*/Bearer <redacted>/g'; }
pw() { (cd "$E2E_DIR" && playwright-cli "$@" 2>&1 | mask); }
start() { pw -s="$1" open "$WEB/login" >/dev/null; pw -s="$1" resize 1280 800 >/dev/null; }
login() { pw -s="$1" goto "$WEB/login" >/dev/null; pw -s="$1" fill "input[name=email]" "$2" >/dev/null; pw -s="$1" fill "input[name=password]" "${3:-$LAB_PW}" --submit >/dev/null; sleep 2; url "$1"; }
url() { pw -s="$1" eval "location.pathname" | grep -A1 "### Result" | tail -1; }
go() { pw -s="$1" goto "$WEB$2" >/dev/null; sleep 1.5; url "$1"; }
text() { pw -s="$1" eval "document.body.innerText" | sed -n '/### Result/,/### Ran/p' | sed '1d;$d'; }
shot() { mkdir -p "$EVID/$(dirname "$2")"; pw -s="$1" screenshot --filename="$EVID/$2" | grep -i "screenshot" | head -1; }
token() { curl -s -X POST "$API/api/auth/login" -H "Content-Type: application/json" -d "{\"email\":\"$1\",\"password\":\"${2:-$LAB_PW}\"}" | python -c "import sys,json;print(json.load(sys.stdin)['accessToken'])"; }
apicall() { curl -s -o "$E2E_DIR/apibody" -w "%{http_code}" -X "$1" "$API$2" -H "Authorization: Bearer $3" -H "Content-Type: application/json" ${4:+-d "$4"}; echo " $(head -c 300 "$E2E_DIR/apibody")"; }
