# 📋 API Documentation

> Documentación completa de la API REST de TactiCore Backend

## 🚀 Información General

- **Base URL**: `http://localhost:8080`
- **Versión**: 1.0.0
- **Formato**: JSON
- **CORS**: Habilitado para todos los orígenes

## 📚 Secciones

- **[Endpoints](endpoints.md)** - Documentación detallada de todos los endpoints
- **[Ejemplos de Respuesta](examples.md)** - Ejemplos de respuestas de la API
- **[Interfaces TypeScript](typescript.md)** - Definiciones TypeScript para el frontend
- **[Análisis de Kills](kill-analysis.md)** - Endpoints específicos para análisis de kills

## 🎯 Endpoints Principales

### Salud y Estado
- `GET /ping` - Health check
- `GET /api/health` - Estado del sistema

### Partidas
- `GET /api/matches` - Lista de partidas (soporta filtro `?user=nombre`)
- `POST /api/matches` - Subir nueva partida (DEM + video opcional)
- `GET /api/matches/{id}` - Detalles de partida
- `DELETE /api/matches/{id}` - Eliminar partida
- `GET /api/matches/{id}/kills` - Kills de partida (soporta filtro `?user=nombre`)
- `GET /api/matches/{id}/chat` - Mensajes del chat
- `POST /api/matches/{id}/chat` - Enviar mensaje al chat
- `GET /api/matches/{matchId}/status` - Estado de procesamiento

### Análisis
- `GET /api/analysis/overview` - Análisis general de kills
- `GET /api/analysis/player/{playerName}` - Estadísticas de jugador
- `GET /api/analysis/round/{roundNumber}` - Análisis de ronda
- `GET /api/analysis/user/{user}/overview` - Análisis por usuario
- `GET /api/analysis/user/{user}/kills` - Kills de usuario
- `GET /api/analysis/user/{user}/round/{round}` - Kills de usuario por ronda
- `GET /api/analysis/users` - Lista de usuarios

### Usuarios
- `GET /api/users` - Todos los usuarios
- `POST /api/users` - Crear o obtener usuario
- `GET /api/users/{name}` - Usuario por nombre
- `GET /api/users/exists/{name}` - Verificar existencia
- `GET /api/users/search?name=...` - Buscar usuarios
- `GET /api/users/role/{role}` - Usuarios por rol
- `GET /api/users/top/score` - Top por puntaje
- `GET /api/users/top/kills` - Top por kills
- `GET /api/users/top/kdr` - Top por KDR
- `GET /api/users/top/matches` - Top por partidas
- `GET /api/users/{name}/profile` - Perfil completo

### Datos
- `POST /api/data/load?fileName=...` - Cargar datos desde JSON
- `DELETE /api/data/clear` - Limpiar todos los datos
- `POST /api/data/reload-preloaded` - Recargar datos precargados
- `POST /api/data/reload-dummy` - Recargar datos dummy
- `GET /api/data/status` - Estado de la base de datos

### Analytics
- `GET /api/analytics/dashboard?user=...` - Dashboard principal
- `GET /api/analytics/historical?timeRange=...&metric=...` - Datos históricos

### Uploads
- `POST /api/upload/dem` - Subir archivo DEM
- `POST /api/upload/video` - Subir video
- `POST /api/upload/process` - Procesar partida

### Utilidades
- `GET /api/maps` - Mapas disponibles
- `GET /api/weapons` - Armas disponibles

## 🔧 Uso Básico

### Ejemplo de Request

```javascript
// Obtener lista de partidas
fetch('/api/matches')
  .then(response => response.json())
  .then(data => console.log(data));

// Obtener kills de una partida específica
fetch('/api/matches/1/kills')
  .then(response => response.json())
  .then(data => console.log(data));
```

### Headers Recomendados

```javascript
const headers = {
  'Accept': 'application/json',
  'Content-Type': 'application/json'
};
```

## 📊 Códigos de Estado

- **200 OK** - Solicitud exitosa
- **400 Bad Request** - Parámetros inválidos
- **404 Not Found** - Recurso no encontrado
- **500 Internal Server Error** - Error interno del servidor

## 🔍 Filtros Disponibles

### Partidas
- `?user=nombre` - Filtrar por usuario

### Kills
- `?user=nombre` - Filtrar por usuario (en `/api/matches/{id}/kills`)

### Analytics
- `?user=nombre` - Filtrar por usuario (en `/api/analytics/dashboard`)
- `?timeRange=all|week|month|year` - Rango de tiempo (en `/api/analytics/historical`)
- `?metric=kdr|kills|deaths|...` - Métrica a analizar (en `/api/analytics/historical`)

## 📝 Notas Importantes

- Todos los endpoints retornan JSON
- Los números decimales están redondeados apropiadamente
- Las fechas están en formato ISO 8601
- Los datos se calculan en tiempo real
- La base de datos se reinicia al reiniciar la aplicación
