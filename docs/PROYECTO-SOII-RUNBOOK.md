# Runbook Operativo - Proyecto Sistemas Operativos II

## Arquitectura

Empresa ficticia: NovaTech Solutions  
Aplicacion: TagAirlines Airport Platform  
Cluster: Kubernetes activo-activo funcional sobre 2 VPS Oracle Linux 10.1

Nodos:

| Nodo | Rol | IP | Funcion |
| --- | --- | --- | --- |
| xela.local | control-plane | 144.91.92.71 | Administracion Kubernetes, despliegue, ingress |
| dana.local | worker | 173.212.198.19 | Ejecucion de cargas backend/frontend/oracle/mailhog |

Componentes:

| Capa | Kubernetes | Descripcion |
| --- | --- | --- |
| Frontend | deployment/frontend, service/frontend | Angular 19 + NGINX |
| Backend | deployment/backend, service/backend | Java 21 + Spring Boot 3 |
| Base de datos | deployment/oracle, pvc/oracle-data | Oracle Database Free/AI compatible |
| Email local | deployment/mailhog, service/mailhog | SMTP sin suscripcion para pruebas |
| Monitoreo | namespace/monitoring | Prometheus + Grafana |

## Accesos

Completar antes de entregar:

| Servicio | URL | Usuario | Password |
| --- | --- | --- | --- |
| SSH Xela | ssh root@144.91.92.71 | root | ENTREGAR_EN_PRIVADO |
| SSH Dana | ssh root@173.212.198.19 | root | ENTREGAR_EN_PRIVADO |
| Aplicacion | http://144.91.92.71:32383 | admin@airport.local | Admin12345 |
| Prometheus | http://144.91.92.71:30090 | N/A | N/A |
| Grafana | http://144.91.92.71:30300 | admin | AdminGrafana123 |
| VirtualMin | https://144.91.92.71:10000 | root/operador | ENTREGAR_EN_PRIVADO |

## Despliegue simplificado

Ejecutar desde Xela:

```bash
cd /root/sistemasoperativosII
chmod +x ops/*.sh
DANA_IP=173.212.198.19 APP_DIR=/root/sistemasoperativosII ./ops/deploy-vps.sh
```

El script realiza:

1. Build de backend y frontend.
2. Export de imagenes `.tar`.
3. Import en containerd de Xela.
4. Copia por SCP a Dana.
5. Import en containerd de Dana.
6. `kubectl apply -f k8s/`.
7. `kubectl apply -f k8s/monitoring/`.
8. `kubectl set image`.
9. Validacion de rollout.

## Validacion end-to-end

Ejecutar desde Xela:

```bash
APP_URL=http://144.91.92.71:32383 ./ops/validate-production.sh
```

Valida:

- Sistema operativo.
- Recursos del nodo.
- Estado Kubernetes.
- Monitoreo.
- Endpoint de vuelos.
- Registro de usuario.
- Checkout.
- Descarga de PDF.

## Monitoreo

Aplicar:

```bash
kubectl apply -f k8s/monitoring/
kubectl -n monitoring get pods -o wide
kubectl -n monitoring get svc
```

Acceso:

```text
Prometheus: http://144.91.92.71:30090
Grafana:    http://144.91.92.71:30300
Usuario:    admin
Password:   AdminGrafana123
```

En Grafana:

1. Entrar con `admin/AdminGrafana123`.
2. Confirmar datasource Prometheus.
3. Crear dashboard con paneles:
   - CPU de nodos.
   - Memoria.
   - Pods por namespace.
   - Reinicios de contenedores.
   - Disponibilidad HTTP del backend.

Consultas Prometheus utiles:

```promql
up
sum(rate(container_cpu_usage_seconds_total[5m])) by (pod)
sum(container_memory_working_set_bytes) by (pod)
kube_pod_container_status_restarts_total
up{app="backend"}
http_server_requests_seconds_count
```

## Alta disponibilidad

Evidenciar replicas:

```bash
kubectl -n airport get deploy
kubectl -n airport get pods -o wide
```

Simular incidente:

```bash
./ops/simulate-incident.sh backend
./ops/simulate-incident.sh frontend
./ops/simulate-incident.sh scale
```

Capturar:

- Pod eliminado.
- Kubernetes crea reemplazo.
- Servicio sigue accesible.
- Grafana/Prometheus muestran el evento.

## Backups

Backup manual:

```bash
mkdir -p /root/backups/airport
./ops/backup-oracle.sh
ls -lh /root/backups/airport
```

Cron recomendado:

```bash
crontab -e
```

Agregar:

```text
0 2 * * * /root/sistemasoperativosII/ops/backup-oracle.sh >> /var/log/airport-backup.log 2>&1
```

## Seguridad Linux

Aplicar con cuidado despues de tener llaves SSH probadas:

```bash
ADMIN_USER=operador ./ops/hardening-oraclelinux.sh
```

Validar:

```bash
firewall-cmd --list-all
fail2ban-client status
getenforce
systemctl status sshd firewalld fail2ban
```

## VirtualMin

Instalacion:

```bash
./ops/install-virtualmin-oraclelinux.sh
```

Acceso esperado:

```text
https://144.91.92.71:10000
```

Capturar pantalla de:

- Login.
- Dashboard.
- Estado de servicios.

## Evidencias

Recolectar salidas:

```bash
./ops/collect-evidence.sh
```

Comandos obligatorios para capturas:

```bash
hostnamectl
lscpu
free -h
lsblk
df -h
kubectl get nodes -o wide
kubectl get pods -A -o wide
kubectl get svc -A
kubectl get deployments -A
kubectl get pvc -A
kubectl get pv
docker ps -a
docker images
```

## Checklist final

- [ ] Oracle Linux 10.1 en ambos VPS.
- [ ] 4 vCPU o mas.
- [ ] 6 GB RAM o mas.
- [ ] 200 GB SSD o 100 GB NVMe.
- [ ] Region America.
- [ ] Cluster Kubernetes con Xela y Dana Ready.
- [ ] Namespace `airport`.
- [ ] Deployments backend/frontend/oracle/mailhog.
- [ ] Services backend/frontend/oracle/mailhog.
- [ ] PV y PVC de Oracle.
- [ ] ConfigMap y Secret.
- [ ] Ingress funcional.
- [ ] Prometheus funcional.
- [ ] Grafana funcional.
- [ ] VirtualMin funcional.
- [ ] App funcional con login, compra, PDF y correo.
- [ ] Evidencia de recuperacion ante caida de pod.
- [ ] Informe PDF completo.
