#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-airport}"
BACKUP_DIR="${BACKUP_DIR:-/root/backups/airport}"
KEEP_DAYS="${KEEP_DAYS:-7}"
mkdir -p "$BACKUP_DIR"

POD="$(kubectl -n "$NAMESPACE" get pod -l app=oracle -o jsonpath='{.items[0].metadata.name}')"
STAMP="$(date +%Y%m%d-%H%M%S)"
OUT="$BACKUP_DIR/oracle-airport-$STAMP.dmp"

echo "Exportando Oracle desde pod $POD"
kubectl -n "$NAMESPACE" exec "$POD" -- bash -lc "mkdir -p /tmp/backup && expdp AIRPORT_APP/Airport12345@FREEPDB1 schemas=AIRPORT_APP directory=DATA_PUMP_DIR dumpfile=airport-$STAMP.dmp logfile=airport-$STAMP.log" || true
kubectl -n "$NAMESPACE" cp "$POD:/opt/oracle/admin/FREE/dpdump/airport-$STAMP.dmp" "$OUT" || kubectl -n "$NAMESPACE" cp "$POD:/tmp/backup/airport-$STAMP.dmp" "$OUT"
gzip -f "$OUT"
find "$BACKUP_DIR" -name "oracle-airport-*.dmp.gz" -mtime +"$KEEP_DAYS" -delete
echo "Backup creado: $OUT.gz"
