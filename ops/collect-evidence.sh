#!/usr/bin/env bash
set -euo pipefail

OUT="${OUT:-/root/evidencias-soii-$(date +%Y%m%d-%H%M%S)}"
mkdir -p "$OUT"

run() {
  name="$1"
  shift
  echo "Capturando $name"
  "$@" >"$OUT/$name.txt" 2>&1 || true
}

run hostnamectl hostnamectl
run lscpu lscpu
run free free -h
run lsblk lsblk
run df df -h
run docker-ps docker ps -a
run docker-images docker images
run kubectl-nodes kubectl get nodes -o wide
run kubectl-pods-all kubectl get pods -A -o wide
run kubectl-services kubectl get svc -A
run kubectl-deployments kubectl get deployments -A
run kubectl-pvc kubectl get pvc -A
run kubectl-pv kubectl get pv
run kubectl-ingress kubectl get ingress -A
run airport-describe kubectl -n airport describe deploy backend
run monitoring kubectl -n monitoring get all
run firewall firewall-cmd --list-all
run fail2ban fail2ban-client status

echo "Evidencias guardadas en: $OUT"
