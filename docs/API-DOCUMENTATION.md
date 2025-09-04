# API Documentation - Tacticore Backend

## 📋 Resumen de Endpoints

Basado en el análisis de los mocks del frontend `tacticore-fe-c3`, se identificaron las siguientes funcionalidades que requieren endpoints del backend:

### 🎯 Funcionalidades Principales

1. **Gestión de Partidas (Matches)**
2. **Subida de Archivos (DEM + Video)**
3. **Análisis de Jugadas**
4. **Chat de Análisis**
5. **Analytics Históricos**
6. **Dashboard y Estadísticas**

---

## 🔗 Endpoints Detallados

### 1. **Gestión de Partidas**

#### `GET /api/matches`
**Descripción:** Obtener lista de partidas del usuario
**Respuesta:**
```json
{
  "matches": [
    {
      "id": "1",
      "fileName": "dust2_ranked_2024.dem",
      "hasVideo": true,
      "map": "Dust2",
      "gameType": "Ranked",
      "kills": 24,
      "deaths": 18,
      "goodPlays": 8,
      "badPlays": 3,
      "duration": "32:45",
      "score": 8.5,
      "date": "2024-01-15"
    }
  ]
}
```

#### `GET /api/matches/{id}`
**Descripción:** Obtener detalles de una partida específica
**Respuesta:**
```json
{
  "id": "1",
  "fileName": "dust2_ranked_2024.dem",
  "map": "Dust2",
  "gameType": "Ranked",
  "duration": "32:45",
  "score": 8.5,
  "kills": [
    {
      "id": 1,
      "killer": "Player1",
      "victim": "Enemy1",
      "weapon": "AK-47",
      "isGoodPlay": true,
      "round": 3,
      "time": "1:45",
      "teamAlive": { "ct": 4, "t": 3 },
      "position": "Long A"
    }
  ]
}
```

#### `DELETE /api/matches/{id}`
**Descripción:** Eliminar una partida

### 2. **Subida de Archivos**

#### `POST /api/upload/dem`
**Descripción:** Subir archivo DEM
**Content-Type:** `multipart/form-data`
**Body:**
- `file`: Archivo .dem
- `metadata`: JSON con información adicional

#### `POST /api/upload/video`
**Descripción:** Subir archivo de video
**Content-Type:** `multipart/form-data`
**Body:**
- `file`: Archivo de video
- `matchId`: ID de la partida asociada

#### `POST /api/upload/process`
**Descripción:** Procesar archivo DEM y generar análisis
**Body:**
```json
{
  "matchId": "1",
  "analysisType": "full"
}
```

### 3. **Análisis de Jugadas**

#### `GET /api/matches/{id}/kills`
**Descripción:** Obtener timeline de kills de una partida

#### `GET /api/matches/{id}/analysis`
**Descripción:** Obtener análisis completo de una partida

#### `POST /api/matches/{id}/analyze`
**Descripción:** Ejecutar análisis de una partida

### 4. **Chat de Análisis**

#### `GET /api/matches/{id}/chat`
**Descripción:** Obtener mensajes del chat de análisis

#### `POST /api/matches/{id}/chat`
**Descripción:** Enviar mensaje al chat de análisis
**Body:**
```json
{
  "message": "¿Qué opinas de la jugada en el round 3?",
  "user": "Player"
}
```

### 5. **Analytics Históricos**

#### `GET /api/analytics/historical`
**Descripción:** Obtener datos históricos para gráficos
**Query Parameters:**
- `timeRange`: "all" | "30d" | "7d"
- `metric`: "kdr" | "score" | "kills" | "goodPlays"

**Respuesta:**
```json
{
  "data": [
    {
      "date": "2024-01-01",
      "kills": 18,
      "deaths": 22,
      "kdr": 0.82,
      "score": 6.5,
      "goodPlays": 4,
      "badPlays": 8,
      "matches": 1
    }
  ]
}
```

#### `GET /api/analytics/dashboard`
**Descripción:** Obtener estadísticas del dashboard
**Respuesta:**
```json
{
  "totalMatches": 8,
  "totalKills": 185,
  "totalDeaths": 150,
  "totalGoodPlays": 58,
  "totalBadPlays": 40,
  "averageScore": 7.6,
  "kdr": 1.23
}
```

### 6. **Gestión de Usuarios**

#### `GET /api/user/profile`
**Descripción:** Obtener perfil del usuario

#### `PUT /api/user/profile`
**Descripción:** Actualizar perfil del usuario

---

## 🔧 Endpoints de Utilidad

### `GET /api/health`
**Descripción:** Health check del servicio

### `GET /api/maps`
**Descripción:** Obtener lista de mapas disponibles

### `GET /api/weapons`
**Descripción:** Obtener lista de armas disponibles

---

## 📊 Códigos de Respuesta

- `200`: OK - Operación exitosa
- `201`: Created - Recurso creado exitosamente
- `400`: Bad Request - Datos inválidos
- `401`: Unauthorized - No autenticado
- `403`: Forbidden - No autorizado
- `404`: Not Found - Recurso no encontrado
- `500`: Internal Server Error - Error del servidor

---

## 🔐 Autenticación

Todos los endpoints requieren autenticación mediante JWT Bearer Token:

```
Authorization: Bearer <jwt_token>
```

---

## 📝 Notas de Implementación

1. **Procesamiento de DEMs**: Los archivos DEM deben ser procesados para extraer información de kills, rounds, y estadísticas
2. **Análisis de Video**: Los videos deben ser analizados para identificar jugadas buenas y malas
3. **Chat en Tiempo Real**: Considerar implementar WebSockets para el chat de análisis
4. **Almacenamiento**: Los archivos DEM y videos deben ser almacenados en S3 o similar
5. **Caché**: Implementar caché para analytics y estadísticas frecuentemente consultadas
