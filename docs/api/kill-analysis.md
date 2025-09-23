# 🎯 Análisis de Kills

> Endpoints específicos para análisis detallado de kills

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

## 🔍 Análisis por Usuario

### GET `/api/analysis/user/{user}/overview`

**Descripción**: Obtiene análisis específico de un usuario.

**Parámetros**:
- `user` (path): Nombre del usuario (string, requerido)

**Respuesta Exitosa** (200 OK):
```json
{
  "user": "makazze",
  "total_kills": 22,
  "total_deaths": 16,
  "kd_ratio": 1.375,
  "headshot_rate": 54.55,
  "average_distance": 856.32,
  "favorite_weapon": "ak47",
  "performance_score": 145.67
}
```

### GET `/api/analysis/user/{user}/kills`

**Descripción**: Obtiene todos los kills de un usuario específico.

**Parámetros**:
- `user` (path): Nombre del usuario (string, requerido)

**Respuesta Exitosa** (200 OK):
```json
{
  "user": "makazze",
  "kills": [
    {
      "id": 12345,
      "killer": "makazze",
      "victim": "broky",
      "weapon": "ak47",
      "isGoodPlay": true,
      "round": 1,
      "time": "45.2s",
      "position": "BombsiteA"
    }
  ],
  "total_kills": 22
}
```

### GET `/api/analysis/user/{user}/round/{round}`

**Descripción**: Obtiene kills de un usuario en una ronda específica.

**Parámetros**:
- `user` (path): Nombre del usuario (string, requerido)
- `round` (path): Número de la ronda (integer, requerido)

**Respuesta Exitosa** (200 OK):
```json
{
  "user": "makazze",
  "round": 1,
  "kills": [
    {
      "id": 12345,
      "killer": "makazze",
      "victim": "broky",
      "weapon": "ak47",
      "isGoodPlay": true,
      "time": "45.2s",
      "position": "BombsiteA"
    }
  ],
  "total_kills": 3
}
```

### GET `/api/analysis/users`

**Descripción**: Obtiene lista de todos los usuarios disponibles.

**Respuesta Exitosa** (200 OK):
```json
{
  "users": [
    "makazze",
    "broky",
    "jcobbb",
    "rain",
    "b1t",
    "iM",
    "frozen",
    "karrigan",
    "w0nderful",
    "Aleksib"
  ],
  "total_users": 10
}
```

---

## 📈 Métricas y Estadísticas

### Distribución de Armas

```json
{
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
    }
  ]
}
```

### Distribución de Ubicaciones

```json
{
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
    }
  ]
}
```

### Estadísticas por Ronda

```json
{
  "round_stats": [
    {
      "round": 1,
      "kills": 10,
      "duration": 123.97,
      "most_active_player": "jcobbb"
    },
    {
      "round": 2,
      "kills": 9,
      "duration": 98.45,
      "most_active_player": "makazze"
    }
  ]
}
```

---

## 🎯 Casos de Uso Frontend

### Dashboard Principal

```javascript
// Cargar análisis general
const overview = await fetch('/api/analysis/overview')
  .then(response => response.json());

// Mostrar métricas principales
document.getElementById('total-kills').textContent = overview.total_kills;
document.getElementById('headshot-rate').textContent = overview.headshot_rate + '%';
document.getElementById('avg-distance').textContent = overview.average_distance.toFixed(2);
```

### Perfil de Jugador

```javascript
// Cargar estadísticas de jugador
const playerStats = await fetch('/api/analysis/player/makazze')
  .then(response => response.json());

// Mostrar información del jugador
document.getElementById('player-name').textContent = playerStats.player_name;
document.getElementById('kd-ratio').textContent = playerStats.kd_ratio.toFixed(2);
document.getElementById('headshot-rate').textContent = playerStats.headshot_rate + '%';
```

### Análisis de Ronda

```javascript
// Cargar análisis de ronda
const roundAnalysis = await fetch('/api/analysis/round/1')
  .then(response => response.json());

// Mostrar información de la ronda
document.getElementById('round-number').textContent = roundAnalysis.round_number;
document.getElementById('total-kills').textContent = roundAnalysis.total_kills;
document.getElementById('duration').textContent = roundAnalysis.duration + 's';
```

---

## 🔧 Filtros y Parámetros

### Filtros Disponibles

- **Por Usuario**: `?user=nombre`
- **Por Ronda**: `?round=numero`
- **Por Arma**: `?weapon=nombre`
- **Por Ubicación**: `?location=nombre`

### Ejemplos de Uso

```javascript
// Análisis de un usuario específico
const userAnalysis = await fetch('/api/analysis/user/makazze/overview')
  .then(response => response.json());

// Kills de un usuario en una ronda específica
const userRoundKills = await fetch('/api/analysis/user/makazze/round/1')
  .then(response => response.json());

// Lista de todos los usuarios
const allUsers = await fetch('/api/analysis/users')
  .then(response => response.json());
```

---

## 📊 Interpretación de Datos

### Métricas Clave

- **K/D Ratio**: Relación entre kills y deaths
- **Headshot Rate**: Porcentaje de headshots
- **Average Distance**: Distancia promedio de los kills
- **Performance Score**: Puntuación de rendimiento

### Rangos de Interpretación

- **K/D Ratio**: > 1.5 (Excelente), 1.0-1.5 (Bueno), < 1.0 (Mejorable)
- **Headshot Rate**: > 60% (Excelente), 40-60% (Bueno), < 40% (Mejorable)
- **Performance Score**: > 150 (Excelente), 100-150 (Bueno), < 100 (Mejorable)

---

## 🚨 Manejo de Errores

### Errores Comunes

- **404 Not Found**: Jugador o ronda no encontrada
- **400 Bad Request**: Parámetros inválidos
- **500 Internal Server Error**: Error interno del servidor

### Ejemplo de Manejo

```javascript
try {
  const response = await fetch('/api/analysis/player/unknown_player');
  
  if (!response.ok) {
    if (response.status === 404) {
      throw new Error('Jugador no encontrado');
    }
    throw new Error(`Error HTTP: ${response.status}`);
  }
  
  const data = await response.json();
  console.log(data);
  
} catch (error) {
  console.error('Error:', error.message);
  // Mostrar mensaje de error al usuario
}
```
