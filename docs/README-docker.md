# Tacticore Docker Compose

Este proyecto contiene la configuración Docker Compose para levantar simultáneamente el frontend y backend de Tacticore con health checks.

## Servicios

- **Frontend**: Next.js (puerto 3000)
- **Backend**: Spring Boot Java (puerto 8080)

## Health Checks

Ambos servicios incluyen health checks automáticos:

- **Frontend**: Verifica que responda en http://localhost:3000
- **Backend**: Verifica que responda en http://localhost:8080/hello

El frontend no se iniciará hasta que el backend esté saludable.

## Requisitos

- Docker
- Docker Compose

## Uso

### Levantar todos los servicios

```bash
docker-compose up
```

### Levantar en segundo plano

```bash
docker-compose up -d
```

### Reconstruir imágenes

```bash
docker-compose up --build
```

### Parar servicios

```bash
docker-compose down
```

### Ver logs

```bash
# Todos los servicios
docker-compose logs

# Servicio específico
docker-compose logs frontend
docker-compose logs backend
```

### Verificar estado de health checks

```bash
# Ver estado de los servicios
docker-compose ps

# Ver health checks específicos
docker-compose exec frontend curl -f http://localhost:3000
docker-compose exec backend curl -f http://localhost:8080/hello
```

## URLs

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Health Check Backend: http://localhost:8080/ping
- Hello Endpoint: http://localhost:8080/hello

## Variables de Entorno

### Frontend
- `NEXT_PUBLIC_API_URL`: URL del backend (por defecto: http://backend:8080)

### Backend
- `SPRING_PROFILES_ACTIVE`: Perfil de Spring (por defecto: docker)
- `SERVER_PORT`: Puerto del servidor (por defecto: 8080)

## Personalización

### Cambiar puertos
Edita el archivo `docker-compose.yml` y modifica las líneas de `ports`:

```yaml
ports:
  - "3001:3000"  # Frontend en puerto 3001
  - "8081:8080"  # Backend en puerto 8081
```

### Agregar variables de entorno
Crea un archivo `.env` en la raíz del proyecto:

```env
NODE_ENV=development
SPRING_PROFILES_ACTIVE=docker
```

## Estructura de Directorios

```
.
├── docker-compose.yml
├── tacticore-fe-c3/
│   ├── Dockerfile
│   ├── .dockerignore
│   └── ... (código del frontend)
└── tesis/
    ├── Dockerfile
    ├── .dockerignore
    ├── pom.xml
    ├── src/
    └── ... (código del backend Java)
```

## Testing Manual

Una vez que los servicios estén corriendo, puedes probar:

```bash
# Test frontend
curl http://localhost:3000

# Test backend
curl http://localhost:8080/hello

# Test health check
curl http://localhost:8080/ping
```
