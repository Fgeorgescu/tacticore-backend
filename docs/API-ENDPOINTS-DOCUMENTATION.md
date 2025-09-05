# Documentación Detallada de Endpoints - API de Análisis de Kills

## Información General

**Base URL**: `http://localhost:8080`  
**Versión**: 1.0.0  
**Formato**: JSON  
**CORS**: Habilitado para todos los orígenes

---

## 1. Análisis General

### GET `/api/analysis/overview`

**Descripción**: Obtiene un análisis completo de todos los kills en la base de datos, incluyendo estadísticas globales, distribuciones y rankings.

**Parámetros**: Ninguno

**Headers**:
```
Accept: application/json
Content-Type: application/json
```

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
  "round_stats": [
    {
      "round": 1,
      "kills": 10
    },
    {
      "round": 2,
      "kills": 9
    },
    {
      "round": 3,
      "kills": 4
    }
  ],
  "side_stats": [
    {
      "side": "ct",
      "count": 66
    },
    {
      "side": "t",
      "count": 76
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
    },
    {
      "player": "jcobbb",
      "kills": 19,
      "deaths": 12,
      "kd_ratio": 1.583
    }
  ],
  "prediction_stats": [
    {
      "label": "good_decision",
      "count": 142,
      "average_confidence": 0.861
    },
    {
      "label": "other",
      "count": 1,
      "average_confidence": 0.999
    }
  ]
}
```

**Casos de Uso Frontend**:
- Dashboard principal con métricas generales
- Gráficos de distribución por armas y ubicaciones
- Ranking de jugadores
- Estadísticas de predicciones del modelo IA

**Ejemplo de Uso**:
```javascript
fetch('/api/analysis/overview')
  .then(response => response.json())
  .then(data => {
    console.log('Total kills:', data.total_kills);
    console.log('Headshot rate:', data.headshot_rate + '%');
    console.log('Top player:', data.top_players[0].player);
  });
```

---

## 2. Análisis de Jugador

### GET `/api/analysis/player/{playerName}`

**Descripción**: Obtiene estadísticas detalladas de un jugador específico, incluyendo K/D ratio, headshot rate, arma favorita y score de rendimiento.

**Parámetros**:
- `playerName` (path): Nombre del jugador (string, requerido)

**Headers**:
```
Accept: application/json
Content-Type: application/json
```

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

**Casos de Uso Frontend**:
- Perfil individual de jugador
- Comparación entre jugadores
- Análisis de rendimiento personalizado
- Estadísticas para coaching

**Ejemplo de Uso**:
```javascript
const playerName = 'makazze';
fetch(`/api/analysis/player/${encodeURIComponent(playerName)}`)
  .then(response => response.json())
  .then(data => {
    console.log(`${data.player_name}: ${data.kills}K/${data.deaths}D`);
    console.log(`K/D Ratio: ${data.kd_ratio}`);
    console.log(`Headshot Rate: ${data.headshot_rate}%`);
    console.log(`Favorite Weapon: ${data.favorite_weapon}`);
    console.log(`Performance Score: ${data.performance_score}`);
  });
```

**Jugadores Disponibles** (basado en los datos):
- makazze, broky, jcobbb, rain, b1t, iM, frozen, karrigan, w0nderful, Aleksib

---

## 3. Análisis de Ronda

### GET `/api/analysis/round/{roundNumber}`

**Descripción**: Obtiene análisis detallado de una ronda específica, incluyendo duración, hotspots, distribución de armas y balance CT/T.

**Parámetros**:
- `roundNumber` (path): Número de la ronda (integer, requerido)

**Headers**:
```
Accept: application/json
Content-Type: application/json
```

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
    },
    {
      "location": "Unknown",
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

**Respuesta de Error** (404 Not Found):
```json
{
  "error": "Round not found",
  "message": "No data found for round: 999"
}
```

**Casos de Uso Frontend**:
- Análisis de ronda específica
- Timeline de kills por ronda
- Mapa de calor de ubicaciones
- Análisis de balance de equipos

**Ejemplo de Uso**:
```javascript
const roundNumber = 1;
fetch(`/api/analysis/round/${roundNumber}`)
  .then(response => response.json())
  .then(data => {
    console.log(`Round ${data.round_number}: ${data.total_kills} kills`);
    console.log(`Duration: ${data.duration}s`);
    console.log(`Most active: ${data.most_active_player}`);
    console.log(`CT vs T: ${data.ct_t_balance.ct} vs ${data.ct_t_balance.t}`);
    console.log(`Headshot rate: ${data.headshot_rate}%`);
  });
```

**Rondas Disponibles**: 1-21 (basado en los datos)

---

## 4. Gestión de Datos

### POST `/api/data/load`

**Descripción**: Carga datos desde un archivo JSON especificado. Si no se especifica archivo, usa `example.json` por defecto.

**Parámetros**:
- `fileName` (query, opcional): Nombre del archivo JSON (string, default: "example.json")

**Headers**:
```
Accept: application/json
Content-Type: application/json
```

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
  "message": "Error cargando datos: File not found: invalid_file.json"
}
```

**Casos de Uso Frontend**:
- Recargar datos después de cambios
- Cargar nuevos archivos de análisis
- Reset de datos para pruebas

**Ejemplo de Uso**:
```javascript
// Cargar archivo por defecto
fetch('/api/data/load', { method: 'POST' })
  .then(response => response.json())
  .then(data => {
    console.log(data.message);
    // Recargar análisis después de cargar datos
    location.reload();
  });

// Cargar archivo específico
const fileName = 'new_analysis.json';
fetch(`/api/data/load?fileName=${encodeURIComponent(fileName)}`, { 
  method: 'POST' 
})
  .then(response => response.json())
  .then(data => console.log(data.message));
```

---

### DELETE `/api/data/clear`

**Descripción**: Elimina todos los datos de la base de datos en memoria.

**Parámetros**: Ninguno

**Headers**:
```
Accept: application/json
Content-Type: application/json
```

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "success",
  "message": "Todos los datos han sido eliminados"
}
```

**Casos de Uso Frontend**:
- Limpiar datos para pruebas
- Reset completo del sistema
- Preparar para nueva carga de datos

**Ejemplo de Uso**:
```javascript
fetch('/api/data/clear', { method: 'DELETE' })
  .then(response => response.json())
  .then(data => {
    console.log(data.message);
    // Mostrar mensaje de confirmación al usuario
    alert('Datos eliminados exitosamente');
  });
```

---

### GET `/api/data/status`

**Descripción**: Verifica el estado de la base de datos y confirma que está lista para usar.

**Parámetros**: Ninguno

**Headers**:
```
Accept: application/json
Content-Type: application/json
```

**Respuesta Exitosa** (200 OK):
```json
{
  "status": "ready",
  "message": "Base de datos en memoria lista para usar"
}
```

**Casos de Uso Frontend**:
- Verificar estado antes de hacer requests
- Health check de la aplicación
- Confirmar que los datos están cargados

**Ejemplo de Uso**:
```javascript
fetch('/api/data/status')
  .then(response => response.json())
  .then(data => {
    if (data.status === 'ready') {
      console.log('Base de datos lista');
      // Proceder con análisis
      loadAnalysisData();
    } else {
      console.log('Base de datos no disponible');
    }
  });
```

---

## 5. Endpoints de Placeholder

### GET `/api/analysis/rounds`

**Descripción**: Placeholder para análisis de todas las rondas. Actualmente retorna un mensaje informativo.

**Respuesta**:
```json
{
  "message": "Análisis de todas las rondas - implementar según necesidad"
}
```

**Uso Futuro**: Implementar análisis comparativo de todas las rondas.

---

### GET `/api/analysis/players`

**Descripción**: Placeholder para análisis de todos los jugadores. Actualmente retorna un mensaje informativo.

**Respuesta**:
```json
{
  "message": "Análisis de todos los jugadores - implementar según necesidad"
}
```

**Uso Futuro**: Implementar ranking completo y comparaciones entre jugadores.

---

## Manejo de Errores

### Códigos de Estado HTTP

- **200 OK**: Solicitud exitosa
- **400 Bad Request**: Parámetros inválidos o archivo no encontrado
- **404 Not Found**: Recurso no encontrado (jugador/ronda inexistente)
- **500 Internal Server Error**: Error interno del servidor

### Formato de Errores

```json
{
  "error": "Error Type",
  "message": "Descripción detallada del error",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## Ejemplos de Integración Frontend

### 1. Dashboard Principal

```javascript
class KillAnalysisDashboard {
  async loadOverview() {
    try {
      const response = await fetch('/api/analysis/overview');
      const data = await response.json();
      
      this.updateMetrics(data);
      this.renderWeaponChart(data.weapon_stats);
      this.renderLocationChart(data.location_stats);
      this.renderPlayerRanking(data.top_players);
      
    } catch (error) {
      console.error('Error loading overview:', error);
    }
  }
  
  updateMetrics(data) {
    document.getElementById('total-kills').textContent = data.total_kills;
    document.getElementById('headshot-rate').textContent = data.headshot_rate + '%';
    document.getElementById('avg-distance').textContent = data.average_distance.toFixed(2);
  }
}
```

### 2. Perfil de Jugador

```javascript
class PlayerProfile {
  async loadPlayerStats(playerName) {
    try {
      const response = await fetch(`/api/analysis/player/${encodeURIComponent(playerName)}`);
      
      if (!response.ok) {
        throw new Error(`Player ${playerName} not found`);
      }
      
      const data = await response.json();
      this.renderPlayerCard(data);
      
    } catch (error) {
      this.showError(`Error loading player: ${error.message}`);
    }
  }
  
  renderPlayerCard(player) {
    const card = document.createElement('div');
    card.innerHTML = `
      <h3>${player.player_name}</h3>
      <p>K/D: ${player.kd_ratio.toFixed(2)} (${player.kills}K/${player.deaths}D)</p>
      <p>Headshot Rate: ${player.headshot_rate.toFixed(1)}%</p>
      <p>Favorite Weapon: ${player.favorite_weapon}</p>
      <p>Performance Score: ${player.performance_score.toFixed(1)}</p>
    `;
    document.getElementById('player-container').appendChild(card);
  }
}
```

### 3. Análisis de Ronda

```javascript
class RoundAnalysis {
  async loadRoundData(roundNumber) {
    try {
      const response = await fetch(`/api/analysis/round/${roundNumber}`);
      const data = await response.json();
      
      this.renderRoundInfo(data);
      this.renderHotSpots(data.hot_spots);
      this.renderWeaponDistribution(data.weapon_distribution);
      
    } catch (error) {
      console.error(`Error loading round ${roundNumber}:`, error);
    }
  }
  
  renderHotSpots(hotSpots) {
    const container = document.getElementById('hotspots');
    container.innerHTML = hotSpots.map(spot => 
      `<div class="hotspot">
        <span class="location">${spot.location}</span>
        <span class="kills">${spot.kills} kills</span>
      </div>`
    ).join('');
  }
}
```

---

## Consideraciones para el Frontend

### 1. Manejo de Datos
- Todos los endpoints retornan JSON
- Los números decimales están redondeados apropiadamente
- Las fechas están en formato ISO 8601

### 2. Rendimiento
- Los datos se calculan en tiempo real
- Considerar implementar caché en el frontend para datos que no cambian frecuentemente
- Los endpoints son rápidos debido a la base de datos en memoria

### 3. Validación
- Validar nombres de jugadores antes de hacer requests
- Verificar que los números de ronda estén en el rango válido (1-21)
- Manejar casos donde no hay datos disponibles

### 4. UX/UI
- Mostrar estados de carga mientras se obtienen los datos
- Implementar manejo de errores user-friendly
- Considerar paginación para listas largas de datos

### 5. Actualización de Datos
- Los datos se cargan automáticamente al iniciar la aplicación
- Usar `/api/data/status` para verificar disponibilidad
- Implementar recarga de datos cuando sea necesario
