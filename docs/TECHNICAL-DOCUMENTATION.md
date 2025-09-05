# Documentación Técnica - API de Análisis de Kills

## Descripción General

Esta API proporciona análisis detallados de los datos de kills de Counter-Strike almacenados en una base de datos H2 en memoria. Los datos se cargan automáticamente desde el archivo `example.json` al iniciar la aplicación.

## Arquitectura Implementada

### Base de Datos H2
- **Configuración**: Base de datos en memoria con consola web habilitada
- **URL**: `jdbc:h2:mem:testdb`
- **Consola**: `http://localhost:8080/h2-console`
- **Configuración**: `create-drop` (se recrea en cada inicio)
- **Credenciales**: Usuario `sa`, Contraseña `password`

### Entidades JPA

#### KillEntity
- Almacena información detallada de cada kill
- Campos: killId, attacker, victim, place, round, weapon, headshot, distance, timeInRound
- Contexto: posiciones 3D, salud, velocidad, efectos (flash, smoke, molotov, HE)
- Timestamps de creación

#### KillPredictionEntity
- Almacena predicciones del modelo de IA
- Campos: killId, label, confidence, isTopPrediction
- Soporte para múltiples predicciones por kill

#### MatchEntity
- Información general del match
- Campos: matchId, fileName, mapName, tickrate, totalKills, status, hasVideo
- Timestamps de creación y actualización

### Repositorios JPA

#### KillRepository
- Métodos de búsqueda por: attacker, victim, round, weapon, place, side, headshot
- Consultas de rango: roundRange, distanceRange, timeInRoundRange
- Estadísticas: countKillsByAttacker, countDeathsByVictim, countHeadshotsByAttacker
- Agregaciones: weaponUsageStats, locationStats, killsPerRound, killsBySide
- Métricas: averageDistance, averageTimeInRound, totalHeadshots, totalKills

#### KillPredictionRepository
- Búsqueda por killId, label, isTopPrediction
- Estadísticas de predicciones: topPredictionStats, averageConfidenceByLabel
- Conteos y promedios por etiqueta

#### MatchRepository
- Búsqueda por matchId
- Verificación de existencia

### Servicios

#### KillAnalysisService
- **getOverallAnalysis()**: Análisis completo de todos los kills
- **getPlayerStats()**: Estadísticas detalladas de un jugador
- **getRoundAnalysis()**: Análisis detallado de una ronda
- **getTopPlayers()**: Ranking de jugadores por K/D ratio
- **getPredictionStats()**: Estadísticas de predicciones del modelo IA
- **calculatePerformanceScore()**: Algoritmo de puntuación compuesta

#### DataLoaderService
- **loadDataFromJson()**: Carga datos desde archivo JSON
- **loadKillFromJson()**: Procesa kills individuales
- **loadPredictionsFromJson()**: Procesa predicciones del modelo IA
- **clearAllData()**: Limpia todos los datos

#### DatabaseMatchService
- **getAllMatches()**: Obtiene todos los matches de la base de datos
- **getMatchById()**: Obtiene un match específico por ID
- **existsMatch()**: Verifica si un match existe
- **deleteMatch()**: Elimina un match de la base de datos
- **convertToDto()**: Convierte entidades a DTOs con cálculos mock

#### ChatService
- **getChatMessages()**: Obtiene mensajes de chat de un match
- **addChatMessage()**: Agrega un nuevo mensaje de chat
- **convertToDto()**: Convierte entidades de chat a DTOs

#### PreloadedDataService
- **loadPreloadedData()**: Carga datos precargados (matches y chat)
- **loadPreloadedMatches()**: Carga 5 matches de ejemplo
- **loadPreloadedChatMessages()**: Carga mensajes de chat de ejemplo
- **clearPreloadedData()**: Limpia datos precargados

### DTOs (Data Transfer Objects)

#### KillAnalysisDto
- Análisis general con estadísticas completas
- Incluye: totalKills, headshotRate, weaponStats, locationStats, roundStats, sideStats, topPlayers, predictionStats

#### PlayerStatsDto
- Estadísticas de jugador individual
- Incluye: kills, deaths, kdRatio, headshots, headshotRate, averageDistance, favoriteWeapon, performanceScore

#### RoundAnalysisDto
- Análisis de ronda específica
- Incluye: totalKills, duration, mostActivePlayer, hotSpots, weaponDistribution, ctTBalance, headshotRate, averageDistance

### Controladores

#### KillAnalysisController
- **GET /api/analysis/overview**: Análisis general
- **GET /api/analysis/player/{playerName}**: Estadísticas de jugador
- **GET /api/analysis/round/{roundNumber}**: Análisis de ronda
- **GET /api/analysis/rounds**: Análisis de todas las rondas (placeholder)
- **GET /api/analysis/players**: Análisis de todos los jugadores (placeholder)

#### DataController
- **POST /api/data/load**: Cargar datos desde JSON
- **DELETE /api/data/clear**: Limpiar todos los datos
- **GET /api/data/status**: Estado de la base de datos

### Configuración

#### DataInitializer
- Componente que carga automáticamente los datos al iniciar la aplicación
- Carga datos precargados (matches y chat messages) primero
- Luego carga datos de kills desde `example.json`
- Manejo de errores graceful

#### PreloadedDataService
- Servicio para cargar datos precargados en la base de datos
- Incluye 5 matches de ejemplo con diferentes mapas
- Incluye mensajes de chat precargados para cada match
- Evita duplicación de datos al verificar existencia previa

#### application.properties
- Configuración de H2 database
- Configuración de JPA/Hibernate
- Habilitación de consola H2
- Configuración de logging SQL

## Funcionalidades Implementadas

### Análisis de Jugadores
- K/D ratio, headshot rate, distancia promedio
- Arma favorita, score de rendimiento
- Estadísticas comparativas

### Análisis de Rondas
- Duración, hotspots, balance CT/T
- Distribución de armas, jugador más activo
- Métricas temporales

### Análisis General
- Estadísticas globales de kills
- Distribución por armas, ubicaciones, rondas
- Ranking de jugadores
- Estadísticas de predicciones del modelo IA

### Gestión de Datos
- Carga automática desde JSON
- Limpieza de datos
- Verificación de estado

## Endpoints Disponibles

### Análisis de Kills
```
GET  /api/analysis/overview           - Análisis general
GET  /api/analysis/player/{name}      - Estadísticas de jugador
GET  /api/analysis/round/{number}     - Análisis de ronda
```

### Gestión de Datos
```
POST /api/data/load                   - Cargar datos desde JSON
DELETE /api/data/clear                - Limpiar todos los datos
POST /api/data/reload-preloaded       - Recargar datos precargados
GET  /api/data/status                 - Estado de BD
```

### Matches (Reemplaza Mocks)
```
GET  /api/matches                     - Lista de matches
GET  /api/matches/{id}                - Match específico
DELETE /api/matches/{id}              - Eliminar match
```

### Chat (Reemplaza Mocks)
```
GET  /api/matches/{id}/chat           - Mensajes de chat
POST /api/matches/{id}/chat           - Enviar mensaje
```

## Datos de Ejemplo

### Datos Precargados (Reemplazan Mocks)
- **5 matches** de ejemplo con diferentes mapas (Dust2, Mirage, Inferno, Cache, Overpass)
- **Mensajes de chat** precargados para cada match
- **Datos calculados dinámicamente** (K/D ratio, duración, score, etc.)

### Datos de Kills (Desde example.json)
- **143 kills** de Counter-Strike
- **10 jugadores** diferentes (makazze, broky, jcobbb, rain, b1t, iM, frozen, karrigan, w0nderful, Aleksib)
- **21 rondas** de juego (1-21)
- **Predicciones del modelo IA** para cada kill
- **Información detallada** de contexto y posiciones

## Consideraciones Técnicas

### Rendimiento
- Base de datos en memoria para acceso rápido
- Consultas optimizadas con índices automáticos
- Cálculos en tiempo real desde la base de datos

### Escalabilidad
- Estructura preparada para múltiples matches
- Soporte para filtros y paginación
- Consultas agregadas eficientes

### Mantenibilidad
- Separación clara de responsabilidades
- DTOs para respuestas estructuradas
- Servicios modulares y reutilizables

## Próximos Pasos Sugeridos

1. **Implementar paginación** para grandes datasets
2. **Agregar filtros avanzados** por fecha, mapa, etc.
3. **Implementar caché** para consultas frecuentes
4. **Agregar validaciones** de entrada
5. **Implementar logging** detallado
6. **Agregar tests unitarios** para servicios
7. **Implementar métricas de rendimiento**
8. **Agregar soporte para múltiples matches**

## Uso

1. **Iniciar aplicación**: `mvn spring-boot:run`
2. **Verificar datos**: `GET /api/data/status`
3. **Obtener análisis**: `GET /api/analysis/overview`
4. **Probar endpoints**: `./test-kill-analysis-api.sh`
5. **Explorar BD**: `http://localhost:8080/h2-console`
