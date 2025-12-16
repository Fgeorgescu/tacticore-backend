# 🎯 Endpoints Detallados de la API

> Documentación completa y actualizada de todos los endpoints de la API Tacticore Backend

**Última actualización**: Noviembre 2024

---

## 📋 Índice

- [Partidas (Matches)](#-partidas-matches)
- [Análisis de Kills](#-análisis-de-kills)
- [Usuarios](#-usuarios)
- [Datos](#-datos)
- [Analytics](#-analytics)
- [Chat](#-chat)
- [Uploads](#-uploads)
- [Utilidades](#-utilidades)

---

## 🎮 Partidas (Matches)

### GET `/api/matches`

Obtiene todas las partidas disponibles. Soporta filtrado por usuario.

**Query Parameters**:
- `user` (opcional): Filtrar partidas por nombre de usuario

**Respuesta Exitosa** (200 OK):
```json
{
  "matches": [
    {
      "id": "match_1234567890",
      "fileName": "example.dem",
      "hasVideo": false,
      "map": "de_mirage",
      "gameType": "Ranked",
      "kills": 143,
      "deaths": 120,
      "goodPlays": 85,
      "badPlays": 35,
      "duration": "45:30",
      "score": 8.5,
      "date": "2024-11-04T10:00:00"
    }
  ],
  "filteredBy": "nombre_usuario" // Solo si se especificó el parámetro user
}
```

**Ejemplo**:
```bash
curl "http://localhost:8080/api/matches"
curl "http://localhost:8080/api/matches?user=makazze"
```

---

### GET `/api/matches/{id}`

Obtiene los detalles de una partida específica.

**Path Parameters**:
- `id` (requerido): ID único de la partida

**Respuesta Exitosa** (200 OK):
```json
{
  "id": "match_1234567890",
  "fileName": "example.dem",
  "hasVideo": false,
  "map": "de_mirage",
  "gameType": "Ranked",
  "kills": 143,
  "deaths": 120,
  "goodPlays": 85,
  "badPlays": 35,
  "duration": "45:30",
  "score": 8.5,
  "date": "2024-11-04T10:00:00"
}
```

**Respuesta de Error** (404 Not Found):
```json
// Respuesta vacía
```

---

### DELETE `/api/matches/{id}`

Elimina una partida específica.

**Path Parameters**:
- `id` (requerido): ID único de la partida

**Respuesta Exitosa** (200 OK):
```json
{
  "message": "Match deleted successfully",
  "id": "match_1234567890"
}
```

---

### GET `/api/matches/{id}/kills`

Obtiene todos los kills de una partida específica. Soporta filtrado por usuario.

**Path Parameters**:
- `id` (requerido): ID único de la partida

**Query Parameters**:
- `user` (opcional): Filtrar kills por nombre de usuario

**Respuesta Exitosa** (200 OK):
```json
{
  "matchId": "match_1234567890",
  "filteredBy": "makazze", // Solo si se especificó el parámetro user
  "kills": [
    {
      "id": 12345,
      "killer": "makazze",
      "victim": "jcobbb",
      "weapon": "ak47",
      "isGoodPlay": true,
      "round": 1,
      "time": "45.2s",
      "teamAlive": {
        "ct": 4,
        "t": 3
      },
      "position": "BombsiteA"
    }
  ]
}
```

**Nota**: Si existe un archivo JSON con datos del modelo ML, se devuelven esos datos completos con predicciones. Si no, se devuelven datos desde la base de datos.

---

### POST `/api/matches`

Sube una nueva partida (archivo DEM y opcionalmente video).

**Content-Type**: `multipart/form-data`

**Request Body**:
- `demFile` (requerido): Archivo .dem
- `videoFile` (opcional): Archivo de video
- `metadata` (opcional): JSON string con metadatos

**Ejemplo de metadata**:
```json
{
  "notes": "de_mirage",
  "gameType": "Ranked"
}
```

**Respuesta Exitosa** (200 OK):
```json
{
  "id": "match_1234567890",
  "status": "processing",
  "message": "Match uploaded successfully and is being processed"
}
```

**El procesamiento es asíncrono**. Usa `/api/matches/{id}/status` para verificar el estado.

---

### GET `/api/matches/{matchId}/status`

Obtiene el estado de procesamiento de una partida.

**Path Parameters**:
- `matchId` (requerido): ID único de la partida

**Respuesta Exitosa** (200 OK):
```json
{
  "id": "match_1234567890",
  "status": "processing", // "processing", "completed", "failed"
  "message": "Match is being processed"
}
```

**Estados posibles**:
- `processing`: La partida está siendo procesada
- `completed`: Procesamiento completado exitosamente
- `failed`: El procesamiento falló

---

## 🔍 Análisis de Kills

### GET `/api/analysis/overview`

Obtiene un análisis completo de todos los kills en la base de datos.

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
  "round_stats": [
    {
      "round": 1,
      "kills": 7
    }
  ],
  "side_stats": [
    {
      "side": "ct",
      "kills": 72
    }
  ],
  "top_players": [
    {
      "player": "makazze",
      "kills": 22,
      "deaths": 16,
      "kd_ratio": 1.375
    }
  ],
  "prediction_stats": [
    {
      "label": "good_play",
      "count": 85,
      "percentage": 59.44
    }
  ]
}
```

---

### GET `/api/analysis/player/{playerName}`

Obtiene estadísticas detalladas de un jugador específico.

**Path Parameters**:
- `playerName` (requerido): Nombre del jugador

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

---

### GET `/api/analysis/round/{roundNumber}`

Obtiene análisis detallado de una ronda específica.

**Path Parameters**:
- `roundNumber` (requerido): Número de la ronda (integer)

**Respuesta Exitosa** (200 OK):
```json
{
  "round_number": 1,
  "total_kills": 7,
  "headshot_rate": 57.14,
  "average_distance": 820.5,
  "players": [
    {
      "player": "makazze",
      "kills": 2,
      "deaths": 1
    }
  ]
}
```

---

### GET `/api/analysis/rounds`

Obtiene análisis de todas las rondas. (Endpoint en desarrollo)

**Respuesta Exitosa** (200 OK):
```json
{
  "message": "Análisis de todas las rondas - implementar según necesidad"
}
```

---

### GET `/api/analysis/players`

Obtiene análisis de todos los jugadores. (Endpoint en desarrollo)

**Respuesta Exitosa** (200 OK):
```json
{
  "message": "Análisis de todos los jugadores - implementar según necesidad"
}
```

---

### GET `/api/analysis/user/{user}/overview`

Obtiene análisis completo filtrado por usuario.

**Path Parameters**:
- `user` (requerido): Nombre de usuario

**Respuesta Exitosa** (200 OK):
```json
{
  "total_kills": 45,
  "total_headshots": 25,
  "headshot_rate": 55.56,
  "average_distance": 845.2,
  "average_time_in_round": 95.5,
  "weapon_stats": [...],
  "location_stats": [...],
  "top_players": [...]
}
```

---

### GET `/api/analysis/user/{user}/kills`

Obtiene todos los kills de un usuario específico.

**Path Parameters**:
- `user` (requerido): Nombre de usuario

**Respuesta Exitosa** (200 OK):
```json
{
  "user": "makazze",
  "kills": [
    {
      "killId": "1234_makazze_jcobbb",
      "attacker": "makazze",
      "victim": "jcobbb",
      "round": 1,
      "weapon": "ak47",
      "headshot": true,
      "distance": 856.32
    }
  ]
}
```

---

### GET `/api/analysis/user/{user}/round/{round}`

Obtiene kills de un usuario en una ronda específica.

**Path Parameters**:
- `user` (requerido): Nombre de usuario
- `round` (requerido): Número de ronda (integer)

**Respuesta Exitosa** (200 OK):
```json
{
  "user": "makazze",
  "round": 1,
  "kills": [
    {
      "killId": "1234_makazze_jcobbb",
      "attacker": "makazze",
      "victim": "jcobbb",
      "weapon": "ak47"
    }
  ]
}
```

---

### GET `/api/analysis/users`

Obtiene lista de todos los usuarios disponibles.

**Respuesta Exitosa** (200 OK):
```json
{
  "users": ["makazze", "jcobbb", "rain", "broky"]
}
```

---

## 👥 Usuarios

### GET `/api/users`

Obtiene todos los usuarios registrados.

**Respuesta Exitosa** (200 OK):
```json
[
  {
    "id": 1,
    "name": "makazze",
    "role": "PLAYER",
    "averageScore": 8.5,
    "totalKills": 145,
    "totalDeaths": 98,
    "totalMatches": 12,
    "kdr": 1.48
  }
]
```

---

### GET `/api/users/{name}`

Obtiene un usuario específico por nombre.

**Path Parameters**:
- `name` (requerido): Nombre del usuario

**Respuesta Exitosa** (200 OK):
```json
{
  "id": 1,
  "name": "makazze",
  "role": "PLAYER",
  "averageScore": 8.5,
  "totalKills": 145,
  "totalDeaths": 98,
  "totalMatches": 12,
  "kdr": 1.48
}
```

**Respuesta de Error** (404 Not Found):
```json
// Respuesta vacía
```

---

### GET `/api/users/exists/{name}`

Verifica si un usuario existe.

**Path Parameters**:
- `name` (requerido): Nombre del usuario

**Respuesta Exitosa** (200 OK):
```json
true
```

---

### GET `/api/users/search`

Busca usuarios por nombre (búsqueda parcial).

**Query Parameters**:
- `name` (requerido): Texto a buscar

**Respuesta Exitosa** (200 OK):
```json
[
  {
    "id": 1,
    "name": "makazze",
    "role": "PLAYER",
    "averageScore": 8.5,
    "totalKills": 145,
    "totalDeaths": 98,
    "totalMatches": 12,
    "kdr": 1.48
  }
]
```

---

### GET `/api/users/role/{role}`

Obtiene usuarios filtrados por rol.

**Path Parameters**:
- `role` (requerido): Rol del usuario (PLAYER, COACH, ANALYST, ADMIN)

**Respuesta Exitosa** (200 OK):
```json
[
  {
    "id": 1,
    "name": "makazze",
    "role": "PLAYER",
    ...
  }
]
```

---

### GET `/api/users/top/score`

Obtiene los mejores jugadores ordenados por puntaje promedio.

**Respuesta Exitosa** (200 OK):
```json
[
  {
    "id": 1,
    "name": "makazze",
    "averageScore": 9.2,
    ...
  }
]
```

---

### GET `/api/users/top/kills`

Obtiene los mejores jugadores ordenados por kills totales.

**Respuesta Exitosa** (200 OK):
```json
[
  {
    "id": 1,
    "name": "makazze",
    "totalKills": 245,
    ...
  }
]
```

---

### GET `/api/users/top/kdr`

Obtiene los mejores jugadores ordenados por KDR (Kill/Death Ratio).

**Respuesta Exitosa** (200 OK):
```json
[
  {
    "id": 1,
    "name": "makazze",
    "kdr": 1.85,
    ...
  }
]
```

---

### GET `/api/users/top/matches`

Obtiene los mejores jugadores con mínimo de partidas.

**Query Parameters**:
- `minMatches` (opcional, default: 5): Mínimo de partidas requeridas

**Respuesta Exitosa** (200 OK):
```json
[
  {
    "id": 1,
    "name": "makazze",
    "totalMatches": 15,
    ...
  }
]
```

---

### POST `/api/users`

Crea un nuevo usuario o obtiene uno existente.

**Request Body**:
```json
{
  "name": "makazze",
  "role": "PLAYER"
}
```

**Respuesta Exitosa** (200 OK):
```json
{
  "id": 1,
  "name": "makazze",
  "role": "PLAYER",
  ...
}
```

---

### GET `/api/users/roles`

Obtiene los roles disponibles.

**Respuesta Exitosa** (200 OK):
```json
["PLAYER", "COACH", "ANALYST", "ADMIN"]
```

---

### GET `/api/users/stats`

Obtiene estadísticas generales de usuarios.

**Respuesta Exitosa** (200 OK):
```json
{
  "totalUsers": 45,
  "totalPlayers": 40,
  "totalCoaches": 3,
  "totalAnalysts": 2
}
```

---

### GET `/api/users/{name}/profile`

Obtiene el perfil completo de un usuario.

**Path Parameters**:
- `name` (requerido): Nombre del usuario

**Respuesta Exitosa** (200 OK):
```json
{
  "name": "makazze",
  "role": "PLAYER",
  "stats": {
    "kills": 145,
    "deaths": 98,
    "kdr": 1.48
  },
  "recentActivity": [...],
  "preferences": {...}
}
```

---

### GET `/api/users/debug/kills-users`

Endpoint de debug: Obtiene información sobre usuarios desde kills.

**Respuesta Exitosa** (200 OK):
```json
{
  "totalKills": 143,
  "uniqueUsers": 10,
  "users": [...]
}
```

---

### POST `/api/users/debug/reload-kills`

Endpoint de debug: Recarga kills con mapeo de usuarios.

**Respuesta Exitosa** (200 OK):
```json
"Kills recargados exitosamente con mapeo de usuarios"
```

---

### POST `/api/users/debug/update-stats`

Endpoint de debug: Actualiza estadísticas de todos los usuarios desde kills.

**Respuesta Exitosa** (200 OK):
```json
"Estadísticas de usuarios actualizadas exitosamente"
```

---

### GET `/api/users/debug/{userName}/real-stats`

Endpoint de debug: Obtiene estadísticas reales de un usuario calculadas desde kills.

**Path Parameters**:
- `userName` (requerido): Nombre del usuario

**Respuesta Exitosa** (200 OK):
```json
{
  "userName": "makazze",
  "kills": 145,
  "deaths": 98,
  "kdr": 1.48
}
```

---

## 📊 Datos

### POST `/api/data/load`

Carga datos desde un archivo JSON.

**Query Parameters**:
- `fileName` (opcional, default: "example.json"): Nombre del archivo JSON a cargar

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "success",
  "message": "Datos cargados exitosamente desde example.json"
}
```

**Respuesta de Error** (400 Bad Request):
```json
{
  "status": "error",
  "message": "Error cargando datos: <detalle del error>"
}
```

---

### DELETE `/api/data/clear`

Elimina todos los datos de la base de datos en memoria.

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "success",
  "message": "Todos los datos han sido eliminados"
}
```

---

### POST `/api/data/reload-preloaded`

Recarga datos precargados del sistema.

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "success",
  "message": "Datos precargados recargados exitosamente"
}
```

---

### POST `/api/data/reload-dummy`

Recarga datos dummy de prueba.

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "success",
  "message": "Datos dummy recargados exitosamente"
}
```

---

### GET `/api/data/status`

Obtiene el estado de la base de datos.

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "ready",
  "message": "Base de datos en memoria lista para usar"
}
```

---

## 📈 Analytics

### GET `/api/analytics/historical`

Obtiene análisis histórico de métricas.

**Query Parameters**:
- `timeRange` (opcional, default: "all"): Rango de tiempo (all, week, month, year)
- `metric` (opcional, default: "kdr"): Métrica a analizar (kdr, kills, deaths, etc.)

**Respuesta Exitosa** (200 OK):
```json
{
  "data": [
    {
      "date": "2024-11-04",
      "value": 1.48,
      "metric": "kdr"
    }
  ]
}
```

---

### GET `/api/analytics/dashboard`

Obtiene estadísticas del dashboard.

**Query Parameters**:
- `user` (opcional): Filtrar por usuario específico

**Respuesta Exitosa** (200 OK):
```json
{
  "totalMatches": 12,
  "totalKills": 143,
  "averageKDR": 1.48,
  "topWeapon": "ak47",
  "topMap": "de_mirage"
}
```

---

## 💬 Chat

### GET `/api/matches/{id}/chat`

Obtiene todos los mensajes de chat de una partida.

**Path Parameters**:
- `id` (requerido): ID único de la partida

**Respuesta Exitosa** (200 OK):
```json
[
  {
    "matchId": "match_1234567890",
    "user": "Bot",
    "message": "Si tienes una duda, podes realizarme cualquier consulta",
    "timestamp": "2024-11-04T10:00:00"
  },
  {
    "matchId": "match_1234567890",
    "user": "makazze",
    "message": "¿Qué arma uso más?",
    "timestamp": "2024-11-04T10:05:00"
  }
]
```

---

### POST `/api/matches/{id}/chat`

Envía un mensaje de chat en una partida.

**Path Parameters**:
- `id` (requerido): ID único de la partida

**Request Body**:
```json
{
  "user": "makazze",
  "message": "¿Qué arma uso más?"
}
```

**Respuesta Exitosa** (201 Created):
```json
{
  "matchId": "match_1234567890",
  "user": "makazze",
  "message": "¿Qué arma uso más?",
  "timestamp": "2024-11-04T10:05:00"
}
```

**Respuesta de Error** (400 Bad Request):
```json
// Respuesta vacía
```

---

## 📤 Uploads

### POST `/api/upload/dem`

Sube un archivo DEM para procesamiento.

**Content-Type**: `multipart/form-data`

**Request Body**:
- `file` (requerido): Archivo .dem
- `metadata` (opcional): JSON string con metadatos

**Respuesta Exitosa** (201 Created):
```json
{
  "success": true,
  "message": "Archivo .dem procesado exitosamente",
  "id": "dem_1234567890",
  "fileName": "example.dem",
  "status": "processed",
  "aiResponse": {
    "totalKills": 143,
    "map": "Unknown",
    "tickrate": 64,
    "status": "success"
  },
  "totalKills": 143,
  "map": "Unknown",
  "tickrate": 64
}
```

---

### POST `/api/upload/video`

Sube un archivo de video asociado a una partida.

**Content-Type**: `multipart/form-data`

**Request Body**:
- `file` (requerido): Archivo de video
- `matchId` (opcional): ID de la partida asociada

**Respuesta Exitosa** (201 Created):
```json
{
  "id": "video_1234567890",
  "matchId": "match_1234567890",
  "status": "uploaded"
}
```

---

### POST `/api/upload/process`

Inicia el procesamiento de una partida.

**Request Body**:
```json
{
  "matchId": "match_1234567890"
}
```

**Respuesta Exitosa** (200 OK):
```json
{
  "matchId": "match_1234567890",
  "status": "processing",
  "estimatedTime": 300
}
```

---

## 🗺️ Utilidades

### GET `/api/maps`

Obtiene lista de mapas disponibles.

**Respuesta Exitosa** (200 OK):
```json
[
  "de_mirage",
  "de_dust2",
  "de_inferno",
  "de_nuke",
  "de_overpass",
  "de_vertigo",
  "de_ancient",
  "de_anubis"
]
```

---

### GET `/api/weapons`

Obtiene lista de armas disponibles.

**Respuesta Exitosa** (200 OK):
```json
[
  "ak47",
  "m4a4",
  "awp",
  "glock",
  "usp_silencer",
  "desert_eagle"
]
```

---

### GET `/api/health`

Verifica el estado de salud del servicio.

**Respuesta Exitosa** (200 OK):
```json
"Tacti-Core Backend is running!"
```

---

### GET `/hello`

Endpoint de prueba básico.

**Respuesta Exitosa** (200 OK):
```json
"Hello World from Spring Boot!"
```

---

### GET `/ping`

Endpoint de ping con información del servicio.

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "ok",
  "timestamp": "2024-11-04T11:33:30.342249515",
  "service": "tacticore-backend-java"
}
```

---

## 🔒 Autenticación

Actualmente la API no requiere autenticación. Todos los endpoints son públicos y permiten CORS desde cualquier origen (`*`).

---

## ⚠️ Manejo de Errores

### Códigos de Estado HTTP

- `200 OK`: Solicitud exitosa
- `201 Created`: Recurso creado exitosamente
- `400 Bad Request`: Solicitud inválida
- `404 Not Found`: Recurso no encontrado
- `500 Internal Server Error`: Error interno del servidor

### Formato de Error

```json
{
  "status": "error",
  "message": "Descripción del error"
}
```

---

## 📝 Notas Importantes

1. **Base de Datos en Memoria**: La base de datos H2 es en memoria, por lo que los datos se pierden al reiniciar el servicio.

2. **Filtrado por Usuario**: Muchos endpoints soportan el parámetro `user` para filtrar resultados.

3. **Procesamiento Asíncrono**: La subida de partidas se procesa de forma asíncrona. Usa el endpoint de status para verificar el progreso.

4. **Formato de Datos**: Los kills pueden venir en dos formatos:
   - Formato completo desde JSON (con predicciones del modelo ML)
   - Formato desde base de datos (estructura simplificada)

5. **CORS**: Todos los endpoints permiten CORS desde cualquier origen.

---

## 🧪 Ejemplos de Uso Completo

### Cargar datos de ejemplo
```bash
curl -X POST "http://localhost:8080/api/data/load?fileName=example.json"
```

### Obtener todas las partidas de un usuario
```bash
curl "http://localhost:8080/api/matches?user=makazze"
```

### Obtener kills de una partida filtrados por usuario
```bash
curl "http://localhost:8080/api/matches/match_123/kills?user=makazze"
```

### Obtener análisis de un usuario
```bash
curl "http://localhost:8080/api/analysis/user/makazze/overview"
```

### Subir una nueva partida
```bash
curl -X POST "http://localhost:8080/api/matches" \
  -F "demFile=@partida.dem" \
  -F "videoFile=@partida.mp4" \
  -F 'metadata={"notes":"de_mirage","gameType":"Ranked"}'
```

---

**Última actualización**: Noviembre 2024
