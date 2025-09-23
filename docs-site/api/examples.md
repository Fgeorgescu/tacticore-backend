# 📋 Ejemplos de Respuesta

> Ejemplos detallados de respuestas de la API

## 🎯 Análisis General

### GET `/api/analysis/overview`

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
    },
    {
      "weapon": "m4a1_silencer", 
      "count": 23
    },
    {
      "weapon": "awp",
      "count": 16
    }
  ],
  "location_stats": [
    {
      "location": "BombsiteA",
      "count": 37
    },
    {
      "location": "BombsiteB",
      "count": 13
    },
    {
      "location": "CTSpawn",
      "count": 12
    }
  ],
  "top_players": [
    {
      "player": "makazze",
      "kills": 22,
      "deaths": 16,
      "kd_ratio": 1.375
    },
    {
      "player": "broky",
      "kills": 22,
      "deaths": 15,
      "kd_ratio": 1.467
    }
  ]
}
```

## 👤 Análisis de Jugador

### GET `/api/analysis/player/{playerName}`

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

## 🎮 Análisis de Ronda

### GET `/api/analysis/round/{roundNumber}`

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
    },
    {
      "location": "CTSpawn",
      "kills": 2
    }
  ],
  "weapon_distribution": {
    "usp_silencer": 2,
    "glock": 3,
    "world": 1,
    "elite": 2,
    "m4a1_silencer": 2
  },
  "ct_t_balance": {
    "ct": 6,
    "t": 4
  },
  "headshot_rate": 60.0,
  "average_distance": 756.43
}
```

## 🎯 Partidas

### GET `/api/matches`

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

### GET `/api/matches/{id}/kills`

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

## 💬 Chat

### GET `/api/matches/{id}/chat`

**Respuesta Exitosa** (200 OK):
```json
{
  "messages": [
    {
      "id": 1,
      "userName": "makazze",
      "message": "Nice shot!",
      "createdAt": "2024-01-15T10:30:00Z"
    },
    {
      "id": 2,
      "userName": "broky",
      "message": "Thanks!",
      "createdAt": "2024-01-15T10:31:00Z"
    }
  ]
}
```

## 📊 Analytics

### GET `/api/analytics/dashboard`

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

## 🗺️ Configuración

### GET `/api/maps`

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

## 🚨 Errores

### Formato de Error General

```json
{
  "error": "Error Type",
  "message": "Descripción detallada del error",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Códigos de Estado

- **200 OK** - Solicitud exitosa
- **201 Created** - Recurso creado exitosamente
- **400 Bad Request** - Parámetros inválidos
- **404 Not Found** - Recurso no encontrado
- **500 Internal Server Error** - Error interno del servidor
