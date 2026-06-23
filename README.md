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
| Contenedores | Docker · Docker Compose |

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

- [Docker](https://www.docker.com/) y Docker Compose instalados

---

## Cómo ejecutar

```bash
# Clonar el repositorio
git clone <url-del-repo>
cd CRUD-Lab

# Levantar todos los servicios
docker compose up --build
```

La aplicación estará disponible en **http://localhost**.

Para detenerla:

```bash
docker compose down
```

Para detenerla y eliminar los datos de la base de datos:

```bash
docker compose down -v
```

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
│   └── nginx.conf
├── Dockerfile
├── docker-compose.yml
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

Al arrancar, `data.sql` se ejecuta automáticamente y carga un catálogo de ejemplo con más de 40 videojuegos distribuidos en categorías: RPGs, plataformas, shooters, indies, clásicos, deportes y estrategia.
