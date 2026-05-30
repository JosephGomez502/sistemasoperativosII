# Airport Platform

Sistema web profesional de gestion aeroportuaria con Spring Boot 3, Java 21, Angular 19, Oracle Database 23ai Free, Docker y Kubernetes.

## Estructura

```text
backend/   API REST, seguridad JWT, JPA, Flyway, PDF y Swagger
frontend/  Angular standalone, Material, guards, interceptor JWT y UI responsive
k8s/       Manifiestos Kubernetes para 2 VPS con NGINX Ingress
```

## Funcionalidad

- Portal publico y busqueda de vuelos.
- Registro, login, JWT, refresh token y roles `ADMIN` / `CLIENT`.
- Checkout con simulador de pagos configurable y formulario de pasajero estilo aerolinea.
- Reservas con bloqueo pesimista de asiento.
- Ticket PDF compacto tipo boarding pass, pensado para impresion termica.
- Envio automatico del detalle y PDF por correo via SMTP configurable.
- Panel cliente con historial y descarga PDF.
- Panel admin con dashboard, CRM de clientes, reservas, pagos, aeropuertos, aviones y vuelos.
- Migraciones Flyway para Oracle con secuencias, constraints, indices y triggers.
- Docker Compose local y manifiestos Kubernetes listos para adaptar imagen/dominio.

## Ejecucion local con Docker

```bash
docker compose up --build
```

Servicios:

- Frontend: http://localhost:8081
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- MailHog Inbox local: http://localhost:8025
- Oracle: `localhost:1521/FREEPDB1`

Credenciales iniciales:

```text
Admin: admin@airport.local / Admin12345
```

> En produccion cambia `JWT_SECRET`, passwords y `CORS_ALLOWED_ORIGINS`. El admin semilla usa `{noop}` solo para facilitar el primer acceso local; los usuarios registrados se almacenan con BCrypt mediante Spring Security.

## Correo sin suscripcion

Localmente el proyecto levanta `mailhog`, un servidor SMTP de desarrollo que no pide login ni suscripcion. El backend envia el detalle de compra y adjunta el PDF al SMTP en `mailhog:1025`, y puedes ver los correos en http://localhost:8025.

En VPS puedes mantener MailHog solo para pruebas o apuntar estas variables a un SMTP propio, por ejemplo Postfix instalado en el servidor:

```text
MAIL_HOST=localhost
MAIL_PORT=25
MAIL_FROM=reservas@tudominio.com
MAIL_SMTP_AUTH=false
MAIL_SMTP_STARTTLS=false
```

No se integra ninguna API externa de pago ni de email. Para entrega real a Gmail/Outlook/Yahoo conviene configurar DNS del dominio (`SPF`, `DKIM`, `DMARC`) en el VPS.

## Ejecucion backend local

Levanta Oracle con Compose:

```bash
docker compose up oracle
```

Ejecuta migraciones:

```bash
cd backend
set DB_URL=jdbc:oracle:thin:@localhost:1521/FREEPDB1
set DB_USERNAME=AIRPORT_APP
set DB_PASSWORD=Airport12345
mvn flyway:migrate
mvn spring-boot:run
```

En Linux/macOS usa `export` en lugar de `set`.

## Ejecucion frontend local

```bash
cd frontend
npm install
npm start
```

El frontend usa `/api`. En desarrollo puedes configurar proxy o usar el contenedor NGINX incluido.

## API principal

### Auth

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@airport.local","password":"Admin12345"}'
```

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Cliente Demo","email":"cliente@demo.com","password":"Cliente12345","documentId":"DPI-1","phone":"+50212345678"}'
```

### Buscar vuelos

```bash
curl "http://localhost:8080/api/public/flights?origin=GUA&destination=SAL"
```

### Checkout

```bash
curl -X POST http://localhost:8080/api/client/checkout \
  -H "Authorization: Bearer ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"flightId":1,"seatNumber":"A1","cardNumber":"4111111111111111","cardHolder":"Cliente Demo","expiry":"12/30","cvv":"123","title":"Sr.","gender":"MASCULINO","birthDate":"1995-05-30","nationality":"Guatemala","documentType":"DPI","documentId":"1234567890101","documentExpiration":"2030-12-31","documentCountry":"Guatemala","frequentFlyer":""}'
```

### Descargar ticket

```bash
curl -L -o ticket.pdf \
  -H "Authorization: Bearer ACCESS_TOKEN" \
  http://localhost:8080/api/client/reservations/AP-CODIGO/ticket.pdf
```

### Admin: crear vuelo

```bash
curl -X POST http://localhost:8080/api/admin/flights \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"originId":1,"destinationId":2,"aircraftId":1,"departureTime":"2026-06-01T10:00:00-06:00","arrivalTime":"2026-06-01T11:10:00-06:00","price":199.99,"status":"SCHEDULED"}'
```

## Kubernetes

1. Construye y publica imagenes:

```bash
docker build -t ghcr.io/your-org/airport-backend:1.0.0 backend
docker build -t ghcr.io/your-org/airport-frontend:1.0.0 frontend
docker push ghcr.io/your-org/airport-backend:1.0.0
docker push ghcr.io/your-org/airport-frontend:1.0.0
```

2. Edita `k8s/backend-deployment.yaml`, `k8s/frontend-deployment.yaml`, `k8s/configmap.yaml`, `k8s/secrets.yaml` e `k8s/ingress.yaml` con tu registry, dominio y secretos.

3. Aplica:

```bash
kubectl apply -f k8s/
kubectl -n airport get pods
kubectl -n airport get ingress
```

## Operacion VPS para Sistemas Operativos II

El proyecto incluye una capa operativa para el despliegue final del curso:

```bash
chmod +x ops/*.sh
DANA_IP=173.212.198.19 APP_DIR=/root/sistemasoperativosII ./ops/deploy-vps.sh
APP_URL=http://144.91.92.71:32383 ./ops/validate-production.sh
kubectl apply -f k8s/monitoring/
```

Accesos recomendados para evidencias:

- Aplicacion: `http://144.91.92.71:32383`
- Prometheus: `http://144.91.92.71:30090`
- Grafana: `http://144.91.92.71:30300` (`admin` / `AdminGrafana123`)
- VirtualMin: `https://144.91.92.71:10000`

Documentacion del proyecto final:

- `docs/PROYECTO-SOII-RUNBOOK.md`
- `docs/INFORME-SOII-PLANTILLA.md`

## Despliegue en 2 VPS Linux

Recomendacion:

- VPS 1: control-plane + ingress + frontend/backend.
- VPS 2: worker + Oracle con PVC local o storage class persistente.
- Instalar Kubernetes con `kubeadm`, `k3s` o `rke2`.
- Instalar `ingress-nginx`.
- Configurar DNS `airport.example.com` apuntando al balanceador o IP publica del ingress.
- Usar TLS con cert-manager en produccion.

## Variables

Ver `.env.example`.

| Variable | Uso |
| --- | --- |
| `DB_URL` | JDBC Oracle |
| `DB_USERNAME` | Usuario de esquema |
| `DB_PASSWORD` | Password de esquema |
| `JWT_SECRET` | Secreto HMAC de al menos 64 caracteres |
| `CORS_ALLOWED_ORIGINS` | Origenes frontend permitidos |
| `PAYMENT_APPROVAL_RATE` | Probabilidad de aprobacion del simulador |
| `MAIL_HOST` | Host SMTP local o externo |
| `MAIL_PORT` | Puerto SMTP |
| `MAIL_FROM` | Remitente de los tickets |
| `MAIL_SMTP_AUTH` | Activa autenticacion SMTP |
| `MAIL_SMTP_STARTTLS` | Activa STARTTLS SMTP |

## Notas de produccion

- Sustituye secretos en `k8s/secrets.yaml` por Sealed Secrets, External Secrets o el gestor de secretos de tu nube.
- Usa una storage class confiable para Oracle.
- Mantén Oracle en una unica replica salvo que migres a una arquitectura Oracle HA.
- Añade TLS al ingress antes de exponer usuarios reales.
- Activa backup de `/opt/oracle/oradata`.
