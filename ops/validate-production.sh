#!/usr/bin/env bash
set -euo pipefail

APP_URL="${APP_URL:-http://144.91.92.71:32383}"
NAMESPACE="${NAMESPACE:-airport}"
EMAIL="vps-$(date +%s)@demo.com"
PASSWORD="Cliente12345"

echo "== Sistema operativo =="
hostnamectl || true
lscpu | sed -n '1,12p' || true
free -h || true
lsblk || true
df -h || true

echo "== Kubernetes =="
kubectl get nodes -o wide
kubectl -n "$NAMESPACE" get pods -o wide
kubectl -n "$NAMESPACE" get svc
kubectl -n "$NAMESPACE" get deploy
kubectl -n "$NAMESPACE" get pvc
kubectl get pv

echo "== Monitoreo =="
kubectl -n monitoring get pods -o wide || true
kubectl -n monitoring get svc || true

echo "== Health backend via ingress =="
curl -fsS "$APP_URL/api/public/flights" >/tmp/flights.json
cat /tmp/flights.json
echo

echo "== Registro usuario =="
AUTH_JSON="$(curl -fsS -X POST "$APP_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -H "Origin: $APP_URL" \
  -d "{\"fullName\":\"Cliente VPS\",\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\",\"documentId\":\"DOC-$(date +%s)\",\"phone\":\"5555\"}")"
echo "$AUTH_JSON" | sed 's/"accessToken":"[^"]*"/"accessToken":"***"/g' | sed 's/"refreshToken":"[^"]*"/"refreshToken":"***"/g'

TOKEN="$(printf '%s' "$AUTH_JSON" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')"
FLIGHT_ID="$(sed -n 's/.*"id":\([0-9][0-9]*\).*/\1/p' /tmp/flights.json | head -1)"

if [ -z "$TOKEN" ] || [ -z "$FLIGHT_ID" ]; then
  echo "No se pudo extraer token o vuelo para prueba checkout."
  exit 1
fi

echo "== Asientos vuelo $FLIGHT_ID =="
curl -fsS "$APP_URL/api/public/flights/$FLIGHT_ID/seats" >/tmp/seats.json
SEAT="$(sed -n 's/.*"seatNumber":"\([^"]*\)","reserved":false.*/\1/p' /tmp/seats.json | head -1)"
echo "Asiento seleccionado: $SEAT"

echo "== Checkout =="
CHECKOUT_JSON="$(curl -fsS -X POST "$APP_URL/api/client/checkout" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"flightId\":$FLIGHT_ID,\"seatNumber\":\"$SEAT\",\"cardNumber\":\"4111111111111111\",\"cardHolder\":\"Cliente VPS\",\"expiry\":\"12/30\",\"cvv\":\"123\",\"title\":\"Sr.\",\"gender\":\"MASCULINO\",\"birthDate\":\"1995-05-30\",\"nationality\":\"Guatemala\",\"documentType\":\"DPI\",\"documentId\":\"1234567890101\",\"documentExpiration\":\"2030-12-31\",\"documentCountry\":\"Guatemala\",\"frequentFlyer\":\"\"}")"
echo "$CHECKOUT_JSON"

CODE="$(printf '%s' "$CHECKOUT_JSON" | sed -n 's/.*"code":"\([^"]*\)".*/\1/p')"
echo "== Descarga PDF $CODE =="
curl -fsS -H "Authorization: Bearer $TOKEN" "$APP_URL/api/client/reservations/$CODE/ticket.pdf" -o "/tmp/ticket-$CODE.pdf"
ls -lh "/tmp/ticket-$CODE.pdf"

echo "Validacion end-to-end completada: $EMAIL / $PASSWORD / reserva $CODE"
