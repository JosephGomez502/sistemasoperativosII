#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-airport}"
TARGET="${1:-backend}"

case "$TARGET" in
  backend|frontend|oracle|mailhog)
    echo "Eliminando un pod de $TARGET para demostrar recuperacion automatica"
    POD="$(kubectl -n "$NAMESPACE" get pod -l "app=$TARGET" -o jsonpath='{.items[0].metadata.name}')"
    kubectl -n "$NAMESPACE" delete pod "$POD"
    kubectl -n "$NAMESPACE" rollout status "deployment/$TARGET" --timeout=180s || true
    kubectl -n "$NAMESPACE" get pods -o wide
    ;;
  scale)
    echo "Demostrando escalamiento horizontal backend 2 -> 3 -> 2"
    kubectl -n "$NAMESPACE" scale deployment/backend --replicas=3
    kubectl -n "$NAMESPACE" rollout status deployment/backend --timeout=180s
    kubectl -n "$NAMESPACE" get pods -l app=backend -o wide
    kubectl -n "$NAMESPACE" scale deployment/backend --replicas=2
    ;;
  *)
    echo "Uso: $0 backend|frontend|oracle|mailhog|scale"
    exit 1
    ;;
esac
