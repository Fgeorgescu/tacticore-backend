# 🎯 Endpoints Detallados

> Documentación completa de todos los endpoints de la API

## 📊 Análisis General

### GET `/api/analysis/overview`

**Descripción**: Obtiene un análisis completo de todos los kills en la base de datos.

**Parámetros**: Ninguno

**Respuesta Exitosa** (200 OK):
```json
{
  "total_kills": 143,
  "total_headshots": 74,
  "headshot_rate": 51.75,
  "average_distance": 794.95,
  "average_time_in_round": 98.0,
  "weapon_stats": [
    {
      "weapon": "ak47",
      "count": 44
    }
  ],
  "location_stats": [
    {
      "location": "BombsiteA",
      "count": 37
    }
  ],
  "top_players": [
    {
      "player": "makazze",
      "kills": 22,
      "deaths": 16,
      "kd_ratio": 1.375
    }
  ]
}
```

**Casos de Uso**:
- Dashboard principal con métricas generales
- Gráficos de distribución por armas y ubicaciones
- Ranking de jugadores

---

## 👤 Análisis de Jugador

### GET `/api/analysis/player/{playerName}`

**Descripción**: Obtiene estadísticas detalladas de un jugador específico.

**Parámetros**:
- `playerName` (path): Nombre del jugador (string, requerido)

**Respuesta Exitosa** (200 OK):
```json
{
  "player_name": "makazze",
  "kills": 22,
  "deaths": 16,
  "kd_ratio": 1.375,
  "headshots": 12,
  "headshot_rate": 54.55,
  "average_distance": 856.32,
  "favorite_weapon": "ak47",
  "performance_score": 145.67
}
```

**Respuesta de Error** (404 Not Found):
```json
{
  "error": "Player not found",
  "message": "No data found for player: unknown_player"
}
```

**Jugadores Disponibles**:
- makazze, broky, jcobbb, rain, b1t, iM, frozen, karrigan, w0nderful, Aleksib

---

## 🎮 Análisis de Ronda

### GET `/api/analysis/round/{roundNumber}`

**Descripción**: Obtiene análisis detallado de una ronda específica.

**Parámetros**:
- `roundNumber` (path): Número de la ronda (integer, requerido)

**Respuesta Exitosa** (200 OK):
```json
{
  "round_number": 1,
  "total_kills": 10,
  "duration": 123.97,
  "most_active_player": "jcobbb",
  "hot_spots": [
    {
      "location": "Connector",
      "kills": 3
    }
  ],
  "weapon_distribution": {
    "usp_silencer": 2,
    "glock": 3
  },
  "ct_t_balance": {
    "ct": 6,
    "t": 4
  },
  "headshot_rate": 60.0,
  "average_distance": 756.43
}
```

**Rondas Disponibles**: 1-21

---

## 🎯 Partidas

### GET `/api/matches`

**Descripción**: Obtiene lista de partidas con filtros opcionales.

**Parámetros**:
- `user` (query, opcional): Filtrar por usuario
- `page` (query, opcional): Número de página
- `limit` (query, opcional): Límite de resultados

**Respuesta Exitosa** (200 OK):
```json
{
  "matches": [
    {
      "id": "1",
      "fileName": "test_match.dem",
      "mapName": "de_dust2",
      "totalKills": 143,
      "tickrate": 128,
      "hasVideo": true,
      "status": "completed",
      "score": 8.5
    }
  ],
  "totalMatches": 1
}
```

### GET `/api/matches/{id}`

**Descripción**: Obtiene detalles de una partida específica.

**Parámetros**:
- `id` (path): ID de la partida (string, requerido)

**Respuesta Exitosa** (200 OK):
```json
{
  "id": "1",
  "fileName": "test_match.dem",
  "mapName": "de_dust2",
  "totalKills": 143,
  "tickrate": 128,
  "hasVideo": true,
  "status": "completed",
  "score": 8.5
}
```

### GET `/api/matches/{id}/kills`

**Descripción**: Obtiene kills de una partida específica.

**Parámetros**:
- `id` (path): ID de la partida (string, requerido)
- `user` (query, opcional): Filtrar por usuario

**Respuesta Exitosa** (200 OK):
```json
{
  "kills": [
    {
      "id": 12345,
      "killer": "makazze",
      "victim": "broky",
      "weapon": "ak47",
      "isGoodPlay": true,
      "round": 1,
      "time": "45.2s",
      "teamAlive": {
        "ct": 5,
        "t": 5
      },
      "position": "BombsiteA"
    }
  ],
  "matchId": "1"
}
```

---

## 💬 Chat

### GET `/api/matches/{id}/chat`

**Descripción**: Obtiene mensajes de chat de una partida.

**Parámetros**:
- `id` (path): ID de la partida (string, requerido)

**Respuesta Exitosa** (200 OK):
```json
{
  "messages": [
    {
      "id": 1,
      "userName": "makazze",
      "message": "Nice shot!",
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ]
}
```

### POST `/api/matches/{id}/chat`

**Descripción**: Agrega un mensaje de chat a una partida.

**Parámetros**:
- `id` (path): ID de la partida (string, requerido)

**Body**:
```json
{
  "userName": "makazze",
  "message": "Nice shot!"
}
```

**Respuesta Exitosa** (201 Created):
```json
{
  "id": 2,
  "userName": "makazze",
  "message": "Nice shot!",
  "createdAt": "2024-01-15T10:35:00Z"
}
```

---

## 📊 Analytics

### GET `/api/analytics/dashboard`

**Descripción**: Obtiene estadísticas del dashboard principal.

**Respuesta Exitosa** (200 OK):
```json
{
  "totalMatches": 1,
  "totalKills": 143,
  "totalDeaths": 89,
  "totalGoodPlays": 95,
  "totalBadPlays": 48,
  "averageScore": 8.5,
  "kdr": 1.61
}
```

### GET `/api/analytics/historical`

**Descripción**: Obtiene datos históricos de analytics.

**Parámetros**:
- `timeRange` (query, opcional): Rango de tiempo
- `metric` (query, opcional): Métrica específica

**Respuesta Exitosa** (200 OK):
```json
{
  "data": [
    {
      "date": "2024-01-15",
      "kills": 25,
      "deaths": 15,
      "kdr": 1.67,
      "score": 8.5,
      "goodPlays": 18,
      "badPlays": 7,
      "matches": 1
    }
  ]
}
```

---

## 🗺️ Configuración

### GET `/api/maps`

**Descripción**: Obtiene lista de mapas disponibles.

**Respuesta Exitosa** (200 OK):
```json
{
  "maps": [
    "de_dust2",
    "de_mirage",
    "de_inferno",
    "de_overpass",
    "de_vertigo"
  ]
}
```

### GET `/api/weapons`

**Descripción**: Obtiene lista de armas disponibles.

**Respuesta Exitosa** (200 OK):
```json
{
  "weapons": [
    "ak47",
    "m4a1_silencer",
    "awp",
    "glock",
    "usp_silencer"
  ]
}
```

---

## 🔧 Gestión de Datos

### POST `/api/data/load`

**Descripción**: Carga datos desde un archivo JSON.

**Parámetros**:
- `fileName` (query, opcional): Nombre del archivo (default: "example.json")

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "success",
  "message": "Datos cargados exitosamente desde example.json"
}
```

### DELETE `/api/data/clear`

**Descripción**: Elimina todos los datos de la base de datos.

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "success",
  "message": "Todos los datos han sido eliminados"
}
```

### GET `/api/data/status`

**Descripción**: Verifica el estado de la base de datos.

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "ready",
  "message": "Base de datos en memoria lista para usar"
}
```

---

## 🚨 Manejo de Errores

### Códigos de Estado HTTP

- **200 OK** - Solicitud exitosa
- **201 Created** - Recurso creado exitosamente
- **400 Bad Request** - Parámetros inválidos
- **404 Not Found** - Recurso no encontrado
- **500 Internal Server Error** - Error interno del servidor

### Formato de Errores

```json
{
  "error": "Error Type",
  "message": "Descripción detallada del error",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## 💡 Ejemplos de Uso

### JavaScript/Fetch

```javascript
// Obtener análisis general
const overview = await fetch('/api/analysis/overview')
  .then(response => response.json());

// Obtener estadísticas de jugador
const playerStats = await fetch('/api/analysis/player/makazze')
  .then(response => response.json());

// Obtener kills de partida con filtro
const kills = await fetch('/api/matches/1/kills?user=makazze')
  .then(response => response.json());
```

### cURL

```bash
# Análisis general
curl http://localhost:8080/api/analysis/overview

# Estadísticas de jugador
curl http://localhost:8080/api/analysis/player/makazze

# Kills de partida
curl http://localhost:8080/api/matches/1/kills
```
