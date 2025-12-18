# Recursos de la API - TactiCore Backend

## Base URL
```
https://65ov51asvd.execute-api.us-east-1.amazonaws.com/prod
```

---

## 1. Health Check

### GET `/api/health`
**Descripción**: Verifica el estado del servicio backend.

**Respuesta JSON**:
```json
"Tacti-Core Backend is running!"
```

---

## 2. Partidas (Matches)

### GET `/api/matches`
**Descripción**: Obtiene todas las partidas. Opcionalmente filtra por usuario.

**Parámetros Query**:
- `user` (opcional): Nombre del usuario para filtrar partidas

**Respuesta JSON**:
```json
{
  "matches": [
    {
      "id": "match_1234567890",
      "fileName": "inferno_demo.dem",
      "hasVideo": false,
      "map": "de_inferno",
      "gameType": "competitive",
      "kills": 143,
      "deaths": 98,
      "goodPlays": 45,
      "badPlays": 12,
      "duration": "45:30",
      "score": 7.9,
      "date": "2025-12-15T10:30:00",
      "status": "completed"
    }
  ],
  "filteredBy": "s1mple"
}
```

### GET `/api/matches/{id}`
**Descripción**: Obtiene los detalles de una partida específica por ID.

**Respuesta JSON**:
```json
{
  "id": "match_1234567890",
  "fileName": "inferno_demo.dem",
  "hasVideo": false,
  "map": "de_inferno",
  "gameType": "competitive",
  "kills": 143,
  "deaths": 98,
  "goodPlays": 45,
  "badPlays": 12,
  "duration": "45:30",
  "score": 7.9,
  "date": "2025-12-15T10:30:00",
  "status": "completed"
}
```

### DELETE `/api/matches/{id}`
**Descripción**: Elimina una partida específica.

**Respuesta JSON**:
```json
{
  "message": "Match deleted successfully",
  "id": "match_1234567890"
}
```

### GET `/api/matches/{id}/kills`
**Descripción**: Obtiene todas las kills de una partida. Opcionalmente filtra por usuario.

**Parámetros Query**:
- `user` (opcional): Nombre del usuario para filtrar kills

**Respuesta JSON**:
```json
{
  "kills": [
    {
      "id": 12345,
      "killer": "s1mple",
      "victim": "device",
      "weapon": "AK-47",
      "isGoodPlay": true,
      "round": 1,
      "time": "12.5s",
      "teamAlive": {
        "ct": 4,
        "t": 5
      },
      "position": "A Site"
    }
  ],
  "matchId": "match_1234567890",
  "filteredBy": "s1mple"
}
```

### GET `/api/matches/{id}/chat`
**Descripción**: Obtiene todos los mensajes del chat de una partida.

**Respuesta JSON**:
```json
[
  {
    "id": 1,
    "user": "Bot",
    "message": "Si tienes una duda, podes realizarme cualquier consulta",
    "timestamp": "2025-12-15T10:30:00"
  },
  {
    "id": 2,
    "user": "s1mple",
    "message": "Nice play!",
    "timestamp": "2025-12-15T10:35:00"
  }
]
```

### POST `/api/matches/{id}/chat`
**Descripción**: Envía un mensaje al chat de una partida.

**Body JSON**:
```json
{
  "user": "s1mple",
  "message": "Nice play!"
}
```

**Respuesta JSON**:
```json
{
  "id": 3,
  "user": "s1mple",
  "message": "Nice play!",
  "timestamp": "2025-12-15T10:40:00"
}
```

### POST `/api/matches`
**Descripción**: Sube y procesa un archivo DEM de una partida.

**Content-Type**: `multipart/form-data`

**Parámetros Form**:
- `demFile` (requerido): Archivo .dem
- `videoFile` (opcional): Archivo de video
- `metadata` (opcional): JSON string con metadatos

**Respuesta JSON**:
```json
{
  "id": "match_1234567890",
  "status": "completed",
  "message": "Match processing completed successfully"
}
```

### GET `/api/matches/{matchId}/status`
**Descripción**: Obtiene el estado de procesamiento de una partida.

**Respuesta JSON**:
```json
{
  "id": "match_1234567890",
  "status": "completed",
  "message": "Match processing completed successfully"
}
```

---

## 3. Usuarios (Users)

### GET `/api/users`
**Descripción**: Obtiene todos los usuarios registrados.

**Respuesta JSON**:
```json
[
  {
    "id": 1,
    "name": "s1mple",
    "role": "Francotirador",
    "averageScore": 7.9,
    "totalKills": 279,
    "totalDeaths": 229,
    "totalMatches": 29,
    "kdr": 1.2183406113537119
  }
]
```

### GET `/api/users/{name}`
**Descripción**: Obtiene un usuario específico por nombre.

**Respuesta JSON**:
```json
{
  "id": 1,
  "name": "s1mple",
  "role": "Francotirador",
  "averageScore": 7.9,
  "totalKills": 279,
  "totalDeaths": 229,
  "totalMatches": 29,
  "kdr": 1.2183406113537119
}
```

### GET `/api/users/exists/{name}`
**Descripción**: Verifica si un usuario existe.

**Respuesta JSON**:
```json
true
```

### GET `/api/users/search`
**Descripción**: Busca usuarios por nombre (búsqueda parcial).

**Parámetros Query**:
- `name` (requerido): Texto a buscar (mínimo 2 caracteres)

**Respuesta JSON**:
```json
[
  {
    "id": 1,
    "name": "s1mple",
    "role": "Francotirador",
    "averageScore": 7.9,
    "totalKills": 279,
    "totalDeaths": 229,
    "totalMatches": 29,
    "kdr": 1.2183406113537119
  }
]
```

### GET `/api/users/role/{role}`
**Descripción**: Obtiene todos los usuarios de un rol específico.

**Respuesta JSON**:
```json
[
  {
    "id": 1,
    "name": "s1mple",
    "role": "Francotirador",
    "averageScore": 7.9,
    "totalKills": 279,
    "totalDeaths": 229,
    "totalMatches": 29,
    "kdr": 1.2183406113537119
  }
]
```

### GET `/api/users/top/score`
**Descripción**: Obtiene los mejores jugadores ordenados por puntaje promedio.

**Respuesta JSON**:
```json
[
  {
    "id": 1,
    "name": "s1mple",
    "role": "Francotirador",
    "averageScore": 8.5,
    "totalKills": 350,
    "totalDeaths": 200,
    "totalMatches": 35,
    "kdr": 1.75
  }
]
```

### GET `/api/users/top/kills`
**Descripción**: Obtiene los mejores jugadores ordenados por total de kills.

**Respuesta JSON**:
```json
[
  {
    "id": 1,
    "name": "s1mple",
    "role": "Francotirador",
    "averageScore": 8.5,
    "totalKills": 500,
    "totalDeaths": 250,
    "totalMatches": 40,
    "kdr": 2.0
  }
]
```

### GET `/api/users/top/kdr`
**Descripción**: Obtiene los mejores jugadores ordenados por KDR (Kill/Death Ratio).

**Respuesta JSON**:
```json
[
  {
    "id": 1,
    "name": "s1mple",
    "role": "Francotirador",
    "averageScore": 8.5,
    "totalKills": 500,
    "totalDeaths": 200,
    "totalMatches": 40,
    "kdr": 2.5
  }
]
```

### GET `/api/users/top/matches`
**Descripción**: Obtiene los mejores jugadores con un mínimo de partidas jugadas.

**Parámetros Query**:
- `minMatches` (opcional, default: 5): Número mínimo de partidas

**Respuesta JSON**:
```json
[
  {
    "id": 1,
    "name": "s1mple",
    "role": "Francotirador",
    "averageScore": 8.5,
    "totalKills": 500,
    "totalDeaths": 200,
    "totalMatches": 50,
    "kdr": 2.5
  }
]
```

### POST `/api/users`
**Descripción**: Crea un nuevo usuario o devuelve uno existente.

**Body JSON**:
```json
{
  "name": "newPlayer",
  "role": "Entry Fragger"
}
```

**Respuesta JSON**:
```json
{
  "id": 16,
  "name": "newPlayer",
  "role": "Entry Fragger",
  "averageScore": 0.0,
  "totalKills": 0,
  "totalDeaths": 0,
  "totalMatches": 0,
  "kdr": 0.0
}
```

### GET `/api/users/roles`
**Descripción**: Obtiene todos los roles disponibles.

**Respuesta JSON**:
```json
[
  "Entry Fragger",
  "Soporte",
  "Líder en el juego",
  "Ancla",
  "Francotirador",
  "Observador"
]
```

### GET `/api/users/stats`
**Descripción**: Obtiene estadísticas generales de todos los usuarios.

**Respuesta JSON**:
```json
{
  "totalUsers": 15,
  "averageScore": 7.9,
  "totalKills": 4185,
  "totalDeaths": 3435,
  "totalMatches": 435,
  "roleStats": [
    ["Francotirador", 3, 837, 687],
    ["Entry Fragger", 3, 837, 687]
  ]
}
```

### GET `/api/users/{name}/profile`
**Descripción**: Obtiene el perfil completo de un usuario.

**Respuesta JSON**:
```json
{
  "id": "1",
  "username": "s1mple",
  "email": null,
  "avatar": null,
  "role": "Francotirador",
  "stats": {
    "totalMatches": 29,
    "totalRounds": 580,
    "totalKills": 279,
    "totalDeaths": 229,
    "totalGoodPlays": 89,
    "totalBadPlays": 23,
    "averageScore": 7.9,
    "kdr": 1.2183406113537119,
    "winRate": 0.65,
    "favoriteMap": "de_dust2",
    "favoriteWeapon": "AK-47",
    "hoursPlayed": 145.5,
    "memberSince": "2025-01-15T10:00:00"
  },
  "recentActivity": {
    "lastMatchDate": "2025-12-15T10:30:00",
    "matchesThisWeek": 5,
    "matchesThisMonth": 12
  },
  "preferences": {
    "theme": "dark",
    "notifications": true
  }
}
```

---

## 4. Análisis de Kills (Kill Analysis)

### GET `/api/analysis/overview`
**Descripción**: Obtiene un análisis general de todas las kills.

**Respuesta JSON**:
```json
{
  "total_kills": 5000,
  "total_headshots": 1500,
  "headshot_rate": 0.3,
  "average_distance": 450.5,
  "average_time_in_round": 45.2,
  "weapon_stats": [
    {
      "weapon": "AK-47",
      "kills": 1200,
      "headshots": 400
    }
  ],
  "location_stats": [
    {
      "location": "A Site",
      "kills": 800
    }
  ],
  "round_stats": [
    {
      "round": 1,
      "kills": 10
    }
  ],
  "side_stats": [
    {
      "side": "CT",
      "kills": 2500
    }
  ],
  "top_players": [
    {
      "player": "s1mple",
      "kills": 500
    }
  ],
  "prediction_stats": []
}
```

### GET `/api/analysis/player/{playerName}`
**Descripción**: Obtiene estadísticas detalladas de un jugador específico.

**Respuesta JSON**:
```json
{
  "player_name": "s1mple",
  "kills": 500,
  "deaths": 300,
  "kd_ratio": 1.67,
  "headshots": 200,
  "headshot_rate": 0.4,
  "average_distance": 550.5,
  "favorite_weapon": "AK-47",
  "performance_score": 8.5
}
```

### GET `/api/analysis/round/{roundNumber}`
**Descripción**: Obtiene el análisis de una ronda específica.

**Respuesta JSON**:
```json
{
  "round_number": 1,
  "total_kills": 10,
  "duration": 120.5,
  "most_active_player": "s1mple",
  "hot_spots": [
    {
      "location": "A Site",
      "kills": 5
    }
  ],
  "weapon_distribution": {
    "AK-47": 5,
    "M4A4": 3,
    "AWP": 2
  },
  "ct_t_balance": {
    "ct": 3,
    "t": 2
  },
  "headshot_rate": 0.3,
  "average_distance": 450.5
}
```

### GET `/api/analysis/rounds`
**Descripción**: Obtiene análisis de todas las rondas (placeholder).

**Respuesta JSON**:
```json
{
  "message": "Análisis de todas las rondas - implementar según necesidad"
}
```

### GET `/api/analysis/players`
**Descripción**: Obtiene análisis de todos los jugadores (placeholder).

**Respuesta JSON**:
```json
{
  "message": "Análisis de todos los jugadores - implementar según necesidad"
}
```

### GET `/api/analysis/user/{user}/overview`
**Descripción**: Obtiene un análisis general de las kills de un usuario específico.

**Respuesta JSON**:
```json
{
  "total_kills": 279,
  "total_headshots": 89,
  "headshot_rate": 0.319,
  "average_distance": 450.5,
  "average_time_in_round": 45.2,
  "weapon_stats": [],
  "location_stats": [],
  "round_stats": [],
  "side_stats": [],
  "top_players": [],
  "prediction_stats": []
}
```

### GET `/api/analysis/user/{user}/kills`
**Descripción**: Obtiene todas las kills de un usuario específico.

**Respuesta JSON**:
```json
{
  "user": "s1mple",
  "kills": [
    {
      "killId": "kill_123",
      "attacker": "s1mple",
      "victim": "device",
      "weapon": "AK-47",
      "headshot": true,
      "distance": 550.5,
      "round": 1,
      "timeInRound": 12.5
    }
  ]
}
```

### GET `/api/analysis/user/{user}/round/{round}`
**Descripción**: Obtiene las kills de un usuario en una ronda específica.

**Respuesta JSON**:
```json
{
  "user": "s1mple",
  "round": 1,
  "kills": [
    {
      "killId": "kill_123",
      "attacker": "s1mple",
      "victim": "device",
      "weapon": "AK-47",
      "headshot": true,
      "distance": 550.5,
      "round": 1,
      "timeInRound": 12.5
    }
  ]
}
```

### GET `/api/analysis/users`
**Descripción**: Obtiene la lista de todos los usuarios que tienen kills registradas.

**Respuesta JSON**:
```json
{
  "users": ["s1mple", "device", "ZywOo"]
}
```

---

## 5. Analytics

### GET `/api/analytics/historical`
**Descripción**: Obtiene datos históricos de analytics.

**Parámetros Query**:
- `timeRange` (opcional, default: "all"): Rango de tiempo ("all", "week", "month", "year")
- `metric` (opcional, default: "kdr"): Métrica a analizar ("kdr", "kills", "score")

**Respuesta JSON**:
```json
{
  "data": [
    {
      "date": "2025-12-15",
      "kills": 50,
      "deaths": 40,
      "kdr": 1.25,
      "score": 7.5,
      "goodPlays": 15,
      "badPlays": 5,
      "matches": 3
    }
  ]
}
```

### GET `/api/analytics/dashboard`
**Descripción**: Obtiene estadísticas del dashboard. Opcionalmente filtra por usuario.

**Parámetros Query**:
- `user` (opcional): Nombre del usuario para filtrar estadísticas

**Respuesta JSON**:
```json
{
  "totalMatches": 435,
  "totalKills": 4185,
  "totalDeaths": 3435,
  "totalGoodPlays": 1250,
  "totalBadPlays": 320,
  "averageScore": 7.9,
  "kdr": 1.2183406113537119
}
```

---

## 6. Datos del Juego (Game Data)

### GET `/api/maps`
**Descripción**: Obtiene la lista de mapas activos.

**Respuesta JSON**:
```json
[
  "de_dust2",
  "de_inferno",
  "de_mirage",
  "de_overpass",
  "de_vertigo"
]
```

### GET `/api/weapons`
**Descripción**: Obtiene la lista de armas activas.

**Respuesta JSON**:
```json
[
  "AK-47",
  "M4A4",
  "AWP",
  "Glock-18",
  "USP-S"
]
```

---

## 7. Carga de Datos (Data Loading)

### POST `/api/data/load`
**Descripción**: Carga datos desde un archivo JSON.

**Parámetros Query**:
- `fileName` (opcional, default: "example.json"): Nombre del archivo a cargar

**Respuesta JSON**:
```json
{
  "status": "success",
  "message": "Datos cargados exitosamente desde example.json"
}
```

### DELETE `/api/data/clear`
**Descripción**: Elimina todos los datos de la base de datos en memoria.

**Respuesta JSON**:
```json
{
  "status": "success",
  "message": "Todos los datos han sido eliminados"
}
```

### POST `/api/data/reload-preloaded`
**Descripción**: Recarga los datos precargados (usuarios, partidas, kills).

**Respuesta JSON**:
```json
{
  "status": "success",
  "message": "Datos precargados recargados exitosamente"
}
```

### POST `/api/data/reload-dummy`
**Descripción**: Recarga los datos dummy (mapas, armas, analytics).

**Respuesta JSON**:
```json
{
  "status": "success",
  "message": "Datos dummy recargados exitosamente"
}
```

### GET `/api/data/status`
**Descripción**: Verifica el estado de la base de datos.

**Respuesta JSON**:
```json
{
  "status": "ready",
  "message": "Base de datos en memoria lista para usar"
}
```

---

## 8. Uploads

### POST `/api/upload/dem`
**Descripción**: Sube un archivo DEM (simulado, no procesa realmente).

**Content-Type**: `multipart/form-data`

**Parámetros Form**:
- `file` (requerido): Archivo .dem
- `metadata` (opcional): JSON string con metadatos

**Respuesta JSON**:
```json
{
  "success": true,
  "message": "Archivo .dem procesado exitosamente",
  "id": "dem_1234567890",
  "fileName": "inferno_demo.dem",
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

### POST `/api/upload/video`
**Descripción**: Sube un archivo de video asociado a una partida.

**Content-Type**: `multipart/form-data`

**Parámetros Form**:
- `file` (requerido): Archivo de video
- `matchId` (opcional): ID de la partida asociada

**Respuesta JSON**:
```json
{
  "id": "video_1234567890",
  "matchId": "match_1234567890",
  "status": "uploaded"
}
```

### POST `/api/upload/process`
**Descripción**: Inicia el procesamiento de una partida.

**Body JSON**:
```json
{
  "matchId": "match_1234567890"
}
```

**Respuesta JSON**:
```json
{
  "matchId": "match_1234567890",
  "status": "processing",
  "estimatedTime": 300
}
```

---

## 9. Endpoints de Debug (Users)

### GET `/api/users/debug/kills-users`
**Descripción**: Obtiene información de debug sobre usuarios en los datos de kills.

**Respuesta JSON**:
```json
{
  "totalKills": 5000,
  "uniqueUsers": 15,
  "usersInKills": ["s1mple", "device", "ZywOo"],
  "usersNotInKills": []
}
```

### POST `/api/users/debug/reload-kills`
**Descripción**: Recarga los kills con mapeo de usuarios.

**Respuesta JSON**:
```json
"Kills recargados exitosamente con mapeo de usuarios"
```

### POST `/api/users/debug/update-stats`
**Descripción**: Actualiza las estadísticas de todos los usuarios desde los kills.

**Respuesta JSON**:
```json
"Estadísticas de usuarios actualizadas exitosamente"
```

### GET `/api/users/debug/{userName}/real-stats`
**Descripción**: Obtiene las estadísticas reales de un usuario calculadas desde los kills.

**Respuesta JSON**:
```json
{
  "userName": "s1mple",
  "totalKills": 279,
  "totalDeaths": 229,
  "kdr": 1.2183406113537119,
  "headshots": 89,
  "headshotRate": 0.319
}
```

---

## Códigos de Estado HTTP

- `200 OK`: Solicitud exitosa
- `201 Created`: Recurso creado exitosamente
- `400 Bad Request`: Solicitud inválida
- `404 Not Found`: Recurso no encontrado
- `500 Internal Server Error`: Error del servidor

## Headers CORS

Todos los endpoints incluyen los siguientes headers CORS:
- `Access-Control-Allow-Origin: *`
- `Access-Control-Allow-Methods: GET,POST,OPTIONS`
- `Access-Control-Allow-Headers: Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token`

