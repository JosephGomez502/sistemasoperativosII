#!/usr/bin/env bash
set -euo pipefail

DOMAIN="${DOMAIN:?Debes indicar DOMAIN, ejemplo: DOMAIN=airport.midominio.com}"
EMAIL="${EMAIL:?Debes indicar EMAIL, ejemplo: EMAIL=admin@midominio.com}"
APP_URL="https://$DOMAIN"

echo "Configurando Let’s Encrypt para $DOMAIN"

if ! kubectl get namespace cert-manager >/dev/null 2>&1; then
  echo "Instalando cert-manager. Si falla, instala cert-manager manualmente y reintenta."
  kubectl apply -f https://github.com/cert-manager/cert-manager/releases/latest/download/cert-manager.yaml
fi

echo "Esperando cert-manager"
kubectl -n cert-manager rollout status deployment/cert-manager --timeout=240s
kubectl -n cert-manager rollout status deployment/cert-manager-webhook --timeout=240s
kubectl -n cert-manager rollout status deployment/cert-manager-cainjector --timeout=240s

tmpdir="$(mktemp -d)"
sed "s/admin@example.com/$EMAIL/g" k8s/tls/clusterissuer-letsencrypt.yaml > "$tmpdir/clusterissuer.yaml"
sed "s/airport.example.com/$DOMAIN/g" k8s/tls/ingress-tls.yaml > "$tmpdir/ingress-tls.yaml"

kubectl apply -f "$tmpdir/clusterissuer.yaml"
kubectl apply -f "$tmpdir/ingress-tls.yaml"

kubectl -n airport patch configmap airport-config --type merge \
  -p "{\"data\":{\"CORS_ALLOWED_ORIGINS\":\"$APP_URL,http://144.91.92.71:32383,http://144.91.92.71,http://173.212.198.19,http://localhost:4200,http://localhost:8081\"}}"
kubectl -n airport rollout restart deployment/backend
kubectl -n airport rollout status deployment/backend --timeout=180s

echo "Estado del certificado:"
kubectl -n airport get certificate,secret,ingress
echo "Cuando el certificado este Ready=True, abre: $APP_URL"
