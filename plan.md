# Plan de Evolución - Tacti-Core Backend

## 🎯 Objetivo
Evolucionar el backend de Tacti-Core para incluir persistencia completa en AWS con servicios que tengan free tiers, y agregar nuevos endpoints para un flujo completo de procesamiento de partidas de Counter-Strike.

## 🏗️ Arquitectura AWS (Free Tier Friendly)

### 📦 **Almacenamiento de Archivos**
- **AWS S3** (Free Tier: 5GB por 12 meses)
  - Bucket para archivos DEM y videos
  - Versionado habilitado
  - Encriptación AES256
  - Lifecycle policies para optimizar costos

### 🗄️ **Base de Datos**
- **AWS DynamoDB** (Free Tier: 25GB por 12 meses)
  - Tabla `matches` para metadata de partidas
  - Tabla `players` para información de jugadores
  - Tabla `analysis_results` para resultados de análisis
  - Índices GSI para consultas eficientes

### 🔄 **Procesamiento Asíncrono**
- **AWS SQS** (Free Tier: 1M requests/mes)
  - Cola para procesamiento de partidas
  - Dead letter queue para manejo de errores
- **AWS Lambda** (Free Tier: 1M requests/mes)
  - Función para procesamiento de DEM files
  - Función para análisis de video
  - Función para generación de reportes

### 📊 **Monitoreo y Logs**
- **AWS CloudWatch** (Free Tier: 5GB logs/mes)
  - Logs de Lambda functions
  - Métricas de API Gateway
  - Dashboards de monitoreo

### 🔐 **Autenticación y Autorización**
- **AWS Cognito** (Free Tier: 50K MAUs)
  - User pools para autenticación
  - Identity pools para acceso a recursos AWS

## 📋 Endpoints y Flujos

### 🔐 **Autenticación**
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
```

### 🎮 **Gestión de Partidas**

#### 1. Subir Nueva Partida
```
POST /api/matches
Content-Type: multipart/form-data
Body: {
  demFile: File,           // Archivo .dem (requerido)
  videoFile?: File,        // Video complementario (opcional)
  metadata?: {
    playerName: string,
    notes?: string
  }
}
Response: {
  id: string,
  status: 'processing' | 'completed' | 'failed',
  message: string
}
```

#### 2. Obtener Lista de Partidas
```
GET /api/matches?page=1&limit=10&sortBy=date&order=desc
Response: {
  matches: Array<{
    id: string,
    map: string,
    matchType: 'Ranked' | 'Casual' | 'Entrenamiento' | 'Otros',
    kills: number,
    deaths: number,
    goodPlays: number,
    badPlays: number,
    duration: string,
    score: number,
    hasVideo: boolean,
    createdAt: string,
    status: 'processing' | 'completed' | 'failed'
  }>,
  totalMatches: number,
  aggregatedStats: {
    totalMatches: number,
    totalKills: number,
    totalDeaths: number,
    totalGoodPlays: number,
    totalBadPlays: number,
    averageScore: number
  }
}
```

#### 3. Obtener Detalles de Partida
```
GET /api/matches/:id
Response: {
  id: string,
  map: string,
  matchType: string,
  duration: string,
  score: number,
  hasVideo: boolean,
  videoUrl?: string,
  kills: Array<{
    id: string,
    round: number,
    time: string,
    killer: string,
    victim: string,
    weapon: string,
    isGoodPlay: boolean,
    teamAlive: { ct: number, t: number },
    position: { x: number, y: number }
  }>,
  summary: {
    kills: number,
    deaths: number,
    goodPlays: number,
    badPlays: number
  }
}
```

#### 4. Eliminar Partida
```
DELETE /api/matches/:id
Response: {
  success: boolean,
  message: string
}
```

### 📊 **Análisis y Reportes**

#### 5. Obtener Datos Históricos
```
GET /api/analytics/historical?period=30d&metrics=kills,deaths,score
Response: {
  timeSeriesData: Array<{
    date: string,
    kills: number,
    deaths: number,
    goodPlays: number,
    badPlays: number,
    score: number
  }>,
  trends: {
    kills: { value: number, trend: 'up' | 'down', percentage: number },
    deaths: { value: number, trend: 'up' | 'down', percentage: number },
    score: { value: number, trend: 'up' | 'down', percentage: number }
  }
}
```

### 📁 **Gestión de Archivos**

#### 6. Obtener Video de Partida
```
GET /api/matches/:id/video
Response: Video stream o redirect a URL del video
```

#### 7. Estado de Procesamiento
```
GET /api/matches/:id/status
Response: {
  id: string,
  status: 'processing' | 'completed' | 'failed',
  progress?: number,
  error?: string
}
```

### 👤 **Gestión de Jugadores**
```
GET /api/players/profile             # Perfil del jugador actual
PUT /api/players/profile             # Actualizar perfil
GET /api/players/{playerId}/matches  # Partidas de un jugador
GET /api/players/{playerId}/stats    # Estadísticas del jugador
```

### 📈 **Dashboard y Analytics**
```
GET /api/dashboard/overview          # Resumen del dashboard
GET /api/dashboard/recent-matches     # Partidas recientes
GET /api/dashboard/performance       # Métricas de rendimiento
GET /api/analytics/trends            # Tendencias de rendimiento
```

### 🔧 **Sistema y Monitoreo**
```
GET /api/health                      # Health check (existente)
GET /api/status                      # Estado del sistema
GET /api/metrics                     # Métricas de la aplicación
```

## 🔄 Flujos de Procesamiento

### 1. **Upload de Partida**
```
1. Usuario sube archivos (DEM + video opcional)
2. Validación de archivos y metadata
3. Guardado en S3 con nombres únicos
4. Creación de registro en DynamoDB (status: "processing")
5. Envío a cola SQS para procesamiento DEM
6. Si hay video, envío a cola SQS para análisis de video
7. Respuesta inmediata con matchId y status "processing"
```

### 2. **Procesamiento DEM Asíncrono**
```
1. Lambda consume mensaje de SQS (DEM processing)
2. Descarga archivo DEM de S3
3. Parsea archivo DEM (usando librería específica de CS:GO)
4. Extrae datos de partida:
   - Información de rounds
   - Kills y deaths con detalles
   - Posiciones de jugadores
   - Armas utilizadas
   - Estados de equipos
5. Analiza cada kill para determinar si es "good play" o "bad play"
6. Calcula estadísticas agregadas
7. Guarda resultados en DynamoDB
8. Actualiza status a "completed"
9. Envía notificación al usuario
```

### 3. **Análisis de Video (Opcional)**
```
1. Lambda consume mensaje de SQS (Video processing)
2. Descarga archivo de video de S3
3. Analiza video frame por frame
4. Identifica momentos clave (kills, deaths, highlights)
5. Genera clips de highlights
6. Crea heatmaps de movimiento
7. Guarda resultados en S3 y DynamoDB
8. Actualiza match con URLs de highlights
```

### 4. **Consulta de Resultados**
```
1. Usuario consulta análisis por matchId
2. Verificación de permisos (usuario propietario)
3. Obtención de datos de DynamoDB
4. Si hay video, generación de URLs firmadas de S3
5. Respuesta con análisis completo:
   - Estadísticas generales
   - Lista detallada de kills
   - Good plays vs bad plays
   - URLs de highlights (si aplica)
```

### 5. **Análisis Histórico**
```
1. Usuario solicita datos históricos
2. Consulta DynamoDB por userId y período
3. Agregación de métricas por día/semana/mes
4. Cálculo de tendencias y porcentajes
5. Respuesta con time series data y trends
```

## 🗄️ Modelos de Datos (DynamoDB)

### **Tabla: matches**
```json
{
  "matchId": "match_abc12345",
  "userId": "user_123",
  "playerName": "TestPlayer",
  "uploadTime": "2024-12-31T18:33:49Z",
  "status": "completed",
  "demFileKey": "matches/match_abc12345/dem_20241231_183349.dem",
  "videoFileKey": "matches/match_abc12345/video_20241231_183349.mp4",
  "metadata": {
    "notes": "Test match",
    "map": "de_dust2",
    "matchType": "Ranked",
    "duration": "00:45:30"
  },
  "analysis": {
    "kills": 25,
    "deaths": 15,
    "assists": 8,
    "goodPlays": 18,
    "badPlays": 7,
    "score": 85,
    "accuracy": 0.68,
    "headshotPercentage": 0.45,
    "mvpCount": 3
  },
  "kills": [
    {
      "id": "kill_001",
      "round": 1,
      "time": "00:01:23",
      "killer": "TestPlayer",
      "victim": "EnemyPlayer",
      "weapon": "AK-47",
      "isGoodPlay": true,
      "teamAlive": { "ct": 4, "t": 3 },
      "position": { "x": 100, "y": 200 }
    }
  ],
  "createdAt": "2024-12-31T18:33:49Z",
  "updatedAt": "2024-12-31T18:45:12Z"
}
```

### **Tabla: players**
```json
{
  "userId": "user_123",
  "username": "TestPlayer",
  "email": "player@example.com",
  "profile": {
    "rank": "Global Elite",
    "preferredMaps": ["de_dust2", "de_mirage"],
    "playStyle": "aggressive",
    "team": "TeamName"
  },
  "stats": {
    "totalMatches": 150,
    "totalKills": 3375,
    "totalDeaths": 2250,
    "totalGoodPlays": 2700,
    "totalBadPlays": 675,
    "averageScore": 82.5,
    "averageKills": 22.5,
    "averageAccuracy": 0.65,
    "bestMap": "de_dust2",
    "winRate": 0.68
  },
  "createdAt": "2024-01-01T00:00:00Z",
  "lastActive": "2024-12-31T18:33:49Z"
}
```

### **Tabla: analysis_results**
```json
{
  "matchId": "match_abc12345",
  "analysisType": "dem_analysis",
  "results": {
    "rounds": [
      {
        "roundNumber": 1,
        "kills": 3,
        "deaths": 1,
        "assists": 0,
        "weapon": "AK-47",
        "position": {"x": 100, "y": 200},
        "isGoodPlay": true,
        "teamAlive": {"ct": 4, "t": 3}
      }
    ],
    "heatmap": "s3://bucket/heatmaps/match_abc12345.png",
    "timeline": "s3://bucket/timelines/match_abc12345.json",
    "videoAnalysis": {
      "goodPlays": 18,
      "badPlays": 7,
      "highlights": ["s3://bucket/highlights/match_abc12345_001.mp4"]
    }
  },
  "processedAt": "2024-12-31T18:45:12Z"
}
```

### **Tabla: analytics_history**
```json
{
  "userId": "user_123",
  "date": "2024-12-31",
  "metrics": {
    "kills": 25,
    "deaths": 15,
    "goodPlays": 18,
    "badPlays": 7,
    "score": 85,
    "matchesPlayed": 3
  },
  "trends": {
    "kills": { "value": 25, "trend": "up", "percentage": 12.5 },
    "deaths": { "value": 15, "trend": "down", "percentage": -8.3 },
    "score": { "value": 85, "trend": "up", "percentage": 15.2 }
  }
}
```

## 🚀 Plan de Implementación

### **Fase 1: Infraestructura Base (Semana 1-2)**
- [ ] Configurar DynamoDB tables
- [ ] Configurar S3 bucket con versionado
- [ ] Configurar SQS queues
- [ ] Actualizar IAM roles y políticas
- [ ] Migrar MatchService para usar S3 y DynamoDB

### **Fase 2: Autenticación (Semana 3)**
- [ ] Configurar AWS Cognito
- [ ] Implementar endpoints de auth
- [ ] Integrar autenticación en endpoints existentes
- [ ] Crear middleware de autorización

### **Fase 3: Endpoints Básicos (Semana 4-5)**
- [ ] Implementar CRUD de matches
- [ ] Implementar gestión de jugadores
- [ ] Agregar validaciones y permisos
- [ ] Implementar paginación y filtros

### **Fase 4: Procesamiento Asíncrono (Semana 6-7)**
- [ ] Implementar Lambda para procesamiento DEM
- [ ] Implementar Lambda para análisis de video
- [ ] Configurar SQS y dead letter queues
- [ ] Implementar sistema de notificaciones

### **Fase 5: Análisis y Reportes (Semana 8-9)**
- [ ] Implementar análisis de estadísticas
- [ ] Generar reportes PDF
- [ ] Crear dashboards
- [ ] Implementar analytics

### **Fase 6: Testing y Optimización (Semana 10)**
- [ ] Tests unitarios y de integración
- [ ] Performance testing
- [ ] Optimización de costos
- [ ] Documentación final

## 💰 Estimación de Costos (Free Tier)

### **Free Tier (12 meses)**
- **S3**: 5GB almacenamiento
- **DynamoDB**: 25GB almacenamiento + 25WCU/25RCU
- **Lambda**: 1M requests/mes
- **SQS**: 1M requests/mes
- **CloudWatch**: 5GB logs/mes
- **Cognito**: 50K MAUs

### **Costo Estimado Post Free Tier**
- **S3**: ~$0.023/GB/mes
- **DynamoDB**: ~$0.25/GB/mes + $1.25/RCU/mes
- **Lambda**: ~$0.20/1M requests
- **SQS**: ~$0.40/1M requests
- **CloudWatch**: ~$0.50/GB/mes

**Total estimado para 1000 usuarios activos**: ~$50-100/mes

## 🔧 Configuración Técnica

### **Variables de Entorno**
```yaml
app:
  aws:
    region: us-east-1
    s3:
      bucket-name: tacti-core-matches
    dynamodb:
      matches-table: tacti-core-matches
      players-table: tacti-core-players
      analysis-table: tacti-core-analysis
    sqs:
      processing-queue: tacti-core-processing
      dead-letter-queue: tacti-core-dlq
    cognito:
      user-pool-id: us-east-1_xxxxxxxxx
      client-id: xxxxxxxxxxxxxxxxxxxxxxxxxx
```

### **Dependencias Maven**
```xml
<!-- AWS SDK DynamoDB -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>dynamodb</artifactId>
    <version>2.21.0</version>
</dependency>

<!-- AWS SDK SQS -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>sqs</artifactId>
    <version>2.21.0</version>
</dependency>

<!-- AWS SDK Cognito -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>cognitoidentityprovider</artifactId>
    <version>2.21.0</version>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- CS:GO DEM Parser -->
<dependency>
    <groupId>com.github.stefanhaustein</groupId>
    <artifactId>demoinfo</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Video Processing -->
<dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>javacv-platform</artifactId>
    <version>1.5.9</version>
</dependency>

<!-- JSON Processing -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.3</version>
</dependency>

<!-- Date/Time Processing -->
<dependency>
    <groupId>java.time</groupId>
    <artifactId>java-time</artifactId>
    <version>8</version>
</dependency>
```

## 📊 Métricas de Éxito

### **Técnicas**
- [ ] Tiempo de respuesta < 200ms para endpoints básicos
- [ ] Tiempo de procesamiento < 5 minutos para partidas
- [ ] Disponibilidad > 99.9%
- [ ] Errores < 0.1%

### **Negocio**
- [ ] 1000 usuarios activos en 6 meses
- [ ] 10000 partidas procesadas en 6 meses
- [ ] Retención de usuarios > 70%
- [ ] NPS > 50

## 🔄 Próximos Pasos

1. **Revisar y aprobar el plan**
2. **Configurar entorno de desarrollo AWS**
3. **Implementar Fase 1 (Infraestructura Base)**
4. **Crear pruebas de concepto**
5. **Iterar y refinar basado en feedback**

---

*Este plan está diseñado para ser escalable, costo-efectivo y aprovechar al máximo los servicios gratuitos de AWS.*
