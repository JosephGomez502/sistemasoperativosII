#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/root/sistemasoperativosII}"
NAMESPACE="${NAMESPACE:-airport}"
DANA_IP="${DANA_IP:-173.212.198.19}"
BACKEND_IMAGE="${BACKEND_IMAGE:-airport-backend:tagairlines-v2}"
FRONTEND_IMAGE="${FRONTEND_IMAGE:-airport-frontend:tagairlines-v2}"
BACKEND_TAR="${BACKEND_TAR:-airport-backend-tagairlines-v2.tar}"
FRONTEND_TAR="${FRONTEND_TAR:-airport-frontend-tagairlines-v2.tar}"

cd "$APP_DIR"

echo "[1/9] Estado Git"
git status --short --branch

echo "[2/9] Construyendo backend"
docker build -t "$BACKEND_IMAGE" backend

echo "[3/9] Construyendo frontend"
docker build -t "$FRONTEND_IMAGE" frontend

echo "[4/9] Exportando imagenes"
docker save "$BACKEND_IMAGE" -o "$BACKEND_TAR"
docker save "$FRONTEND_IMAGE" -o "$FRONTEND_TAR"

echo "[5/9] Importando imagenes en Xela/containerd"
ctr -n k8s.io images import "$BACKEND_TAR"
ctr -n k8s.io images import "$FRONTEND_TAR"

echo "[6/9] Copiando imagenes a Dana $DANA_IP"
scp "$BACKEND_TAR" "root@$DANA_IP:/root/$BACKEND_TAR"
scp "$FRONTEND_TAR" "root@$DANA_IP:/root/$FRONTEND_TAR"

echo "[7/9] Importando imagenes en Dana/containerd"
ssh "root@$DANA_IP" "ctr -n k8s.io images import /root/$BACKEND_TAR && ctr -n k8s.io images import /root/$FRONTEND_TAR && crictl images | grep airport"

echo "[8/9] Aplicando Kubernetes"
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/pv-oracle.yaml || true
kubectl apply -f k8s/pvc.yaml || true
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/oracle-deployment.yaml
kubectl apply -f k8s/oracle-service.yaml
kubectl apply -f k8s/backend-deployment.yaml
kubectl apply -f k8s/backend-service.yaml
kubectl apply -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/frontend-service.yaml
kubectl apply -f k8s/mailhog-deployment.yaml
kubectl apply -f k8s/mailhog-service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/monitoring/
kubectl -n "$NAMESPACE" set image deployment/backend "backend=$BACKEND_IMAGE"
kubectl -n "$NAMESPACE" set image deployment/frontend "frontend=$FRONTEND_IMAGE"

echo "[9/9] Esperando rollout"
kubectl -n "$NAMESPACE" rollout status deployment/backend --timeout=240s
kubectl -n "$NAMESPACE" rollout status deployment/frontend --timeout=240s
kubectl -n "$NAMESPACE" get pods -o wide
kubectl -n monitoring get pods -o wide

echo "Deploy completado."
