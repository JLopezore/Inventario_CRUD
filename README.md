# CRUD Lab — Catálogo de Videojuegos

Aplicación web full-stack para gestionar un catálogo personal de videojuegos. Desarrollada como práctica de CRUD con Spring Boot, PostgreSQL y un frontend vanilla servido por Nginx.

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Backend | Java 21 · Spring Boot 4.1.0 · Spring Data JPA · Lombok |
| Base de datos | PostgreSQL 16 |
| Frontend | HTML · CSS · JavaScript (vanilla) |
| Servidor web | Nginx Alpine |
| Contenedores | Docker · Docker Compose · Podman (Red Hat) |

---

## Arquitectura

```
http://localhost:80
       │
    [Nginx]
    ├── /        → index.html (frontend estático)
    └── /api/    → proxy → [Spring Boot :8080] → [PostgreSQL :5432]
```

Nginx sirve el frontend y actúa como reverse proxy hacia la API. Spring Boot no expone ningún puerto al exterior — solo se comunica internamente con Nginx y PostgreSQL.

---

## Requisitos

- [Docker](https://www.docker.com/) y Docker Compose, **o bien**
- [Podman](https://podman.io/) y `podman-compose` (Red Hat / RHEL / Fedora)

---

## Cómo ejecutar

### Con Docker

```bash
git clone <url-del-repo>
cd CRUD-Lab

docker compose up --build
```

La aplicación estará disponible en **http://localhost** (puerto 80).

```bash
docker compose down        # detener
docker compose down -v     # detener y borrar datos
```

---

### Con Podman en Red Hat (RHEL 8/9)

#### 1. Instalar dependencias

```bash
sudo dnf install -y podman podman-compose
```

#### 2. Clonar y construir

```bash
git clone <url-del-repo>
sudo cp -r CRUD-Lab /opt/crudlab
cd /opt/crudlab

podman-compose -f podman-compose.yml build
podman-compose -f podman-compose.yml up -d
```

La aplicación estará disponible en **http://localhost:8080** (el nginx del contenedor escucha en el puerto 8080 del host).

```bash
podman-compose -f podman-compose.yml down        # detener
podman-compose -f podman-compose.yml down -v     # detener y borrar datos
```

#### 3. Redirigir el puerto 80 → 8080 (opcional)

Si quieres que la app responda en el puerto 80 estándar:

```bash
sudo firewall-cmd --permanent --add-forward-port=port=80:proto=tcp:toport=8080
sudo firewall-cmd --reload
```

#### 4. Ejecutar como servicio systemd (inicio automático)

```bash
# Copiar la unidad de servicio
sudo cp /opt/crudlab/systemd/crudlab.service /etc/systemd/system/

# Editar la ruta WorkingDirectory si el proyecto no está en /opt/crudlab
sudo systemctl daemon-reload
sudo systemctl enable --now crudlab

# Verificar estado
sudo systemctl status crudlab
journalctl -u crudlab -f    # logs en tiempo real
```

#### Diferencias frente a Docker

| Aspecto | Docker | Podman (RHEL) |
|---|---|---|
| Archivo compose | `docker-compose.yml` | `podman-compose.yml` |
| Imágenes | `postgres:16-alpine` | `docker.io/postgres:16-alpine` |
| SELinux | No aplica | Etiqueta `:Z` en bind mounts |
| Puerto nginx | 80 | 8080 (sin root) |
| Daemon | Requiere Docker daemon | Sin daemon (rootless) |

---

## Funcionalidades

- **Listar** todos los videojuegos en un catálogo ordenado
- **Buscar** por título o plataforma en tiempo real
- **Ordenar** por título (A-Z / Z-A), año de lanzamiento o fecha de registro
- **Agregar** nuevos videojuegos mediante un formulario
- **Ver detalle** al pulsar cualquier fila: imagen y descripción obtenidas de Wikipedia automáticamente
- **Editar** los datos de un videojuego desde el modal de detalle
- **Eliminar** un videojuego con confirmación antes de borrar
- **Estadísticas** en tiempo real: total de juegos, completados y pendientes

---

## API REST

Base URL: `http://localhost/api/videojuego`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/videojuego` | Listar todos los videojuegos |
| `POST` | `/api/videojuego` | Crear un nuevo videojuego |
| `PUT` | `/api/videojuego/{id}` | Actualizar un videojuego existente |
| `DELETE` | `/api/videojuego/{id}` | Eliminar un videojuego |

### Modelo

```json
{
  "id": 1,
  "titulo": "Elden Ring",
  "plataforma": "PC",
  "anioLanzamiento": 2022,
  "completado": false
}
```

### Ejemplos

```bash
# Listar todos
curl http://localhost/api/videojuego

# Crear
curl -X POST http://localhost/api/videojuego \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Elden Ring","plataforma":"PC","anioLanzamiento":2022,"completado":false}'

# Actualizar
curl -X PUT http://localhost/api/videojuego/1 \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Elden Ring","plataforma":"PC","anioLanzamiento":2022,"completado":true}'

# Eliminar
curl -X DELETE http://localhost/api/videojuego/1
```

---

## Estructura del proyecto

```
CRUD-Lab/
├── src/
│   └── main/
│       ├── java/com/jlopezore/crudlab/
│       │   ├── CrudLabApplication.java
│       │   ├── controlle/
│       │   │   └── VideojuegoController.java
│       │   ├── model/
│       │   │   └── Videojuego.java
│       │   ├── repository/
│       │   │   └── VideojuegoRepository.java
│       │   └── service/
│       │       └── VideojuegoService.java
│       └── resources/
│           ├── application.properties
│           ├── data.sql               ← datos de ejemplo
│           └── static/
│               └── index.html         ← frontend
├── nginx/
│   ├── nginx.conf                     ← config de Nginx (producción)
│   └── Dockerfile                     ← imagen Nginx con frontend integrado
├── k8s/                               ← manifiestos para MicroShift
│   ├── namespace.yaml
│   ├── secret.yaml
│   ├── postgres-pvc.yaml
│   ├── postgres-deployment.yaml
│   ├── postgres-service.yaml
│   ├── app-deployment.yaml            ← 2 réplicas para Alta Disponibilidad
│   ├── app-service.yaml
│   ├── nginx-deployment.yaml
│   ├── nginx-service.yaml
│   └── nginx-route.yaml              ← Route OpenShift para acceso externo
├── systemd/
│   └── crudlab.service               ← unidad systemd para Podman
├── Dockerfile                         ← imagen Spring Boot
├── docker-compose.yml                 ← despliegue con Docker
├── podman-compose.yml                 ← despliegue con Podman (RHEL)
├── deploy.sh                          ← script automatizado para MicroShift
└── build.gradle
```

---

## Variables de entorno

La aplicación lee la configuración de la base de datos desde variables de entorno con valores por defecto para desarrollo local:

| Variable | Defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/inventario` | URL de conexión a PostgreSQL |
| `DB_USER` | `admin` | Usuario de la base de datos |
| `DB_PASSWORD` | `123456` | Contraseña de la base de datos |

---

## Datos de ejemplo

Al arrancar con Docker/Podman, `data.sql` se ejecuta automáticamente y carga un catálogo de ejemplo con más de 40 videojuegos. En MicroShift esta inicialización está desactivada (`SPRING_SQL_INIT_MODE=never`) para evitar duplicados entre réplicas — los datos se agregan desde la interfaz web.

---

## Despliegue en MicroShift (Red Hat)

### Arquitectura de 3 nodos

```
[Nodo 1 — Cliente]          [Nodo 2 — Backend réplica 1]
  Navegador web    ───────→  Pod: crudlab-app (8080)  ──→ [PostgreSQL]
       │                                                        ↑
       │           ───────→  Pod: crudlab-nginx (80)  ──→ [Nodo 3 — Backend réplica 2]
                                    ↑                    Pod: crudlab-app (8080)
                                 Route
                              OpenShift
```

El Deployment del backend tiene `replicas: 2`. Cuando el docente ejecuta `oc delete pod <nombre>`, MicroShift levanta automáticamente un pod nuevo mientras el otro sigue sirviendo tráfico sin interrupción.

### Ejecución del script de despliegue

```bash
# Dentro de la VM con MicroShift activo
cd /opt/crudlab
bash deploy.sh
```

El script realiza en orden:
1. Levanta un registro local de contenedores en `localhost:5000`
2. Configura CRI-O para aceptar el registro inseguro local
3. Construye la imagen de Spring Boot con Gradle
4. Construye la imagen de Nginx con el frontend integrado
5. Descarga y re-etiqueta la imagen de PostgreSQL
6. Publica las tres imágenes en el registro local
7. Aplica todos los manifiestos con `oc apply -f k8s/`

### Verificación del clúster

```bash
oc get nodes                    # debe mostrar STATUS = Ready
oc get pods -A                  # pods internos de MicroShift
oc get pods -n crudlab          # pods del proyecto (5 total)
oc get route -n crudlab         # URL de acceso al frontend
```

### Prueba de Tolerancia a Fallos (Chaos Testing)

```bash
# Ver los pods del backend
oc get pods -n crudlab -l app=crudlab-app

# Eliminar uno (simula fallo)
oc delete pod -n crudlab <nombre-del-pod>

# MicroShift crea uno nuevo en segundos — verificar:
oc get pods -n crudlab -w
```

### Manifiestos YAML

| Archivo | Descripción |
|---|---|
| `k8s/namespace.yaml` | Espacio de nombres `crudlab` |
| `k8s/secret.yaml` | Credenciales de BD (DB_URL, DB_USER, DB_PASSWORD) |
| `k8s/postgres-pvc.yaml` | Volumen persistente de 1 Gi para PostgreSQL |
| `k8s/postgres-deployment.yaml` | Pod de base de datos con readiness probe |
| `k8s/postgres-service.yaml` | DNS interno `postgres:5432` |
| `k8s/app-deployment.yaml` | **2 réplicas** del backend con liveness/readiness probes |
| `k8s/app-service.yaml` | DNS interno `crudlab-app:8080` |
| `k8s/nginx-deployment.yaml` | Pod de Nginx con el frontend ya integrado en la imagen |
| `k8s/nginx-service.yaml` | Servicio interno del frontend |
| `k8s/nginx-route.yaml` | Route OpenShift — expone la app al exterior |
