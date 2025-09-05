# Ejemplos de Respuestas de la API - Análisis de Kills

## Estructura de Datos Común

Todos los endpoints retornan respuestas en formato JSON con la siguiente estructura base:

```json
{
  "status": "success|error",
  "data": { ... },
  "message": "Descripción opcional",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## 1. Análisis General - `/api/analysis/overview`

### Respuesta Completa
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
      "count": 44,
      "percentage": 30.77
    },
    {
      "weapon": "m4a1_silencer",
      "count": 23,
      "percentage": 16.08
    },
    {
      "weapon": "awp",
      "count": 16,
      "percentage": 11.19
    },
    {
      "weapon": "glock",
      "count": 9,
      "percentage": 6.29
    },
    {
      "weapon": "galilar",
      "count": 9,
      "percentage": 6.29
    },
    {
      "weapon": "deagle",
      "count": 9,
      "percentage": 6.29
    },
    {
      "weapon": "m4a1",
      "count": 8,
      "percentage": 5.59
    },
    {
      "weapon": "usp_silencer",
      "count": 4,
      "percentage": 2.80
    },
    {
      "weapon": "elite",
      "count": 3,
      "percentage": 2.10
    },
    {
      "weapon": "hegrenade",
      "count": 3,
      "percentage": 2.10
    }
  ],
  "location_stats": [
    {
      "location": "BombsiteA",
      "count": 37,
      "percentage": 25.87
    },
    {
      "location": "BombsiteB",
      "count": 13,
      "percentage": 9.09
    },
    {
      "location": "CTSpawn",
      "count": 12,
      "percentage": 8.39
    },
    {
      "location": "Catwalk",
      "count": 12,
      "percentage": 8.39
    },
    {
      "location": "Underpass",
      "count": 10,
      "percentage": 6.99
    },
    {
      "location": "Connector",
      "count": 8,
      "percentage": 5.59
    },
    {
      "location": "Unknown",
      "count": 51,
      "percentage": 35.66
    }
  ],
  "round_stats": [
    {
      "round": 1,
      "kills": 10,
      "percentage": 6.99
    },
    {
      "round": 2,
      "kills": 9,
      "percentage": 6.29
    },
    {
      "round": 3,
      "kills": 4,
      "percentage": 2.80
    },
    {
      "round": 4,
      "kills": 6,
      "percentage": 4.20
    },
    {
      "round": 5,
      "kills": 6,
      "percentage": 4.20
    },
    {
      "round": 6,
      "kills": 8,
      "percentage": 5.59
    },
    {
      "round": 7,
      "kills": 5,
      "percentage": 3.50
    },
    {
      "round": 8,
      "kills": 5,
      "percentage": 3.50
    },
    {
      "round": 9,
      "kills": 5,
      "percentage": 3.50
    },
    {
      "round": 10,
      "kills": 9,
      "percentage": 6.29
    },
    {
      "round": 11,
      "kills": 6,
      "percentage": 4.20
    },
    {
      "round": 12,
      "kills": 8,
      "percentage": 5.59
    },
    {
      "round": 13,
      "kills": 7,
      "percentage": 4.90
    },
    {
      "round": 14,
      "kills": 9,
      "percentage": 6.29
    },
    {
      "round": 15,
      "kills": 9,
      "percentage": 6.29
    },
    {
      "round": 16,
      "kills": 2,
      "percentage": 1.40
    },
    {
      "round": 17,
      "kills": 7,
      "percentage": 4.90
    },
    {
      "round": 18,
      "kills": 6,
      "percentage": 4.20
    },
    {
      "round": 19,
      "kills": 5,
      "percentage": 3.50
    },
    {
      "round": 20,
      "kills": 8,
      "percentage": 5.59
    },
    {
      "round": 21,
      "kills": 9,
      "percentage": 6.29
    }
  ],
  "side_stats": [
    {
      "side": "ct",
      "count": 66,
      "percentage": 46.15
    },
    {
      "side": "t",
      "count": 76,
      "percentage": 53.15
    },
    {
      "side": null,
      "count": 1,
      "percentage": 0.70
    }
  ],
  "top_players": [
    {
      "player": "makazze",
      "kills": 22,
      "deaths": 16,
      "kd_ratio": 1.375,
      "headshots": 12,
      "headshot_rate": 54.55,
      "favorite_weapon": "ak47"
    },
    {
      "player": "broky",
      "kills": 22,
      "deaths": 15,
      "kd_ratio": 1.467,
      "headshots": 11,
      "headshot_rate": 50.0,
      "favorite_weapon": "ak47"
    },
    {
      "player": "jcobbb",
      "kills": 19,
      "deaths": 12,
      "kd_ratio": 1.583,
      "headshots": 10,
      "headshot_rate": 52.63,
      "favorite_weapon": "usp_silencer"
    },
    {
      "player": "rain",
      "kills": 16,
      "deaths": 14,
      "kd_ratio": 1.143,
      "headshots": 8,
      "headshot_rate": 50.0,
      "favorite_weapon": "ak47"
    },
    {
      "player": "b1t",
      "kills": 13,
      "deaths": 15,
      "kd_ratio": 0.867,
      "headshots": 7,
      "headshot_rate": 53.85,
      "favorite_weapon": "ak47"
    }
  ],
  "prediction_stats": [
    {
      "label": "good_decision",
      "count": 142,
      "percentage": 99.30,
      "average_confidence": 0.861
    },
    {
      "label": "other",
      "count": 1,
      "percentage": 0.70,
      "average_confidence": 0.999
    }
  ]
}
```

---

## 2. Análisis de Jugador - `/api/analysis/player/{playerName}`

### Jugador Existente (makazze)
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
  "performance_score": 145.67,
  "weapon_breakdown": [
    {
      "weapon": "ak47",
      "kills": 8,
      "headshots": 4,
      "headshot_rate": 50.0
    },
    {
      "weapon": "m4a1_silencer",
      "kills": 6,
      "headshots": 3,
      "headshot_rate": 50.0
    },
    {
      "weapon": "awp",
      "kills": 4,
      "headshots": 2,
      "headshot_rate": 50.0
    },
    {
      "weapon": "glock",
      "kills": 2,
      "headshots": 1,
      "headshot_rate": 50.0
    },
    {
      "weapon": "deagle",
      "kills": 2,
      "headshots": 2,
      "headshot_rate": 100.0
    }
  ],
  "round_performance": [
    {
      "round": 1,
      "kills": 2,
      "deaths": 1,
      "kd_ratio": 2.0
    },
    {
      "round": 2,
      "kills": 1,
      "deaths": 0,
      "kd_ratio": null
    },
    {
      "round": 3,
      "kills": 0,
      "deaths": 1,
      "kd_ratio": 0.0
    }
  ],
  "location_performance": [
    {
      "location": "BombsiteA",
      "kills": 8,
      "deaths": 4,
      "kd_ratio": 2.0
    },
    {
      "location": "BombsiteB",
      "kills": 6,
      "deaths": 3,
      "kd_ratio": 2.0
    },
    {
      "location": "CTSpawn",
      "kills": 4,
      "deaths": 2,
      "kd_ratio": 2.0
    }
  ]
}
```

### Jugador No Encontrado
```json
{
  "error": "Player not found",
  "message": "No data found for player: unknown_player",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## 3. Análisis de Ronda - `/api/analysis/round/{roundNumber}`

### Ronda 1
```json
{
  "round_number": 1,
  "total_kills": 10,
  "duration": 123.97,
  "most_active_player": "jcobbb",
  "hot_spots": [
    {
      "location": "Connector",
      "kills": 3,
      "percentage": 30.0
    },
    {
      "location": "CTSpawn",
      "kills": 2,
      "percentage": 20.0
    },
    {
      "location": "Unknown",
      "kills": 2,
      "percentage": 20.0
    },
    {
      "location": "BombsiteA",
      "kills": 2,
      "percentage": 20.0
    },
    {
      "location": "Catwalk",
      "kills": 1,
      "percentage": 10.0
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
  "average_distance": 756.43,
  "kill_timeline": [
    {
      "time": 31.25,
      "attacker": "jcobbb",
      "victim": "jcobbb",
      "weapon": "world",
      "headshot": false,
      "location": "CTSpawn"
    },
    {
      "time": 123.97,
      "attacker": "jcobbb",
      "victim": "b1t",
      "weapon": "usp_silencer",
      "headshot": true,
      "location": "Connector"
    }
  ],
  "player_performance": [
    {
      "player": "jcobbb",
      "kills": 2,
      "deaths": 1,
      "kd_ratio": 2.0,
      "headshots": 1,
      "headshot_rate": 50.0
    },
    {
      "player": "b1t",
      "kills": 1,
      "deaths": 1,
      "kd_ratio": 1.0,
      "headshots": 0,
      "headshot_rate": 0.0
    }
  ]
}
```

### Ronda 2
```json
{
  "round_number": 2,
  "total_kills": 9,
  "duration": 98.45,
  "most_active_player": "iM",
  "hot_spots": [
    {
      "location": "BombsiteA",
      "kills": 4,
      "percentage": 44.44
    },
    {
      "location": "Underpass",
      "kills": 2,
      "percentage": 22.22
    },
    {
      "location": "Catwalk",
      "kills": 2,
      "percentage": 22.22
    },
    {
      "location": "Unknown",
      "kills": 1,
      "percentage": 11.11
    }
  ],
  "weapon_distribution": {
    "glock": 3,
    "ak47": 2,
    "m4a1_silencer": 2,
    "awp": 1,
    "elite": 1
  },
  "ct_t_balance": {
    "ct": 5,
    "t": 4
  },
  "headshot_rate": 55.56,
  "average_distance": 823.67
}
```

### Ronda No Encontrada
```json
{
  "error": "Round not found",
  "message": "No data found for round: 999",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## 4. Gestión de Datos

### POST `/api/data/load` - Éxito
```json
{
  "status": "success",
  "message": "Datos cargados exitosamente desde example.json",
  "timestamp": "2024-01-15T10:30:00Z",
  "data": {
    "total_kills_loaded": 143,
    "total_predictions_loaded": 572,
    "matches_loaded": 1
  }
}
```

### POST `/api/data/load` - Error
```json
{
  "status": "error",
  "message": "Error cargando datos: File not found: invalid_file.json",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### DELETE `/api/data/clear` - Éxito
```json
{
  "status": "success",
  "message": "Todos los datos han sido eliminados",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### GET `/api/data/status` - Listo
```json
{
  "status": "ready",
  "message": "Base de datos en memoria lista para usar",
  "timestamp": "2024-01-15T10:30:00Z",
  "data": {
    "total_kills": 143,
    "total_players": 10,
    "total_rounds": 21,
    "last_updated": "2024-01-15T10:25:00Z"
  }
}
```

---

## Estructuras de Datos para Frontend

### Para Gráficos de Barras (Armas)
```javascript
const weaponData = {
  labels: ["ak47", "m4a1_silencer", "awp", "glock", "galilar"],
  datasets: [{
    label: "Kills por Arma",
    data: [44, 23, 16, 9, 9],
    backgroundColor: ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF"]
  }]
};
```

### Para Gráficos de Dona (Distribución)
```javascript
const sideData = {
  labels: ["CT", "T"],
  datasets: [{
    data: [66, 76],
    backgroundColor: ["#4BC0C0", "#FF6384"]
  }]
};
```

### Para Tablas de Ranking
```javascript
const playerRankingData = [
  { rank: 1, player: "makazze", kills: 22, deaths: 16, kd_ratio: 1.375 },
  { rank: 2, player: "broky", kills: 22, deaths: 15, kd_ratio: 1.467 },
  { rank: 3, player: "jcobbb", kills: 19, deaths: 12, kd_ratio: 1.583 }
];
```

### Para Mapas de Calor
```javascript
const heatmapData = [
  { location: "BombsiteA", kills: 37, intensity: 0.9 },
  { location: "BombsiteB", kills: 13, intensity: 0.3 },
  { location: "CTSpawn", kills: 12, intensity: 0.3 },
  { location: "Catwalk", kills: 12, intensity: 0.3 },
  { location: "Underpass", kills: 10, intensity: 0.2 }
];
```

---

## Validaciones Recomendadas para Frontend

### Nombres de Jugadores Válidos
```javascript
const validPlayers = [
  "makazze", "broky", "jcobbb", "rain", "b1t", 
  "iM", "frozen", "karrigan", "w0nderful", "Aleksib"
];
```

### Rondas Válidas
```javascript
const validRounds = Array.from({length: 21}, (_, i) => i + 1); // 1-21
```

### Armas Disponibles
```javascript
const availableWeapons = [
  "ak47", "m4a1_silencer", "awp", "glock", "galilar", 
  "deagle", "m4a1", "usp_silencer", "elite", "hegrenade",
  "tec9", "famas", "mac10", "fiveseven", "mp9"
];
```

### Ubicaciones Disponibles
```javascript
const availableLocations = [
  "BombsiteA", "BombsiteB", "CTSpawn", "Catwalk", 
  "Underpass", "Connector", "Unknown"
];
```
