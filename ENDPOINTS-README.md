# 🚀 Backend API - Endpoints Implementados

## 📋 Resumen

Se han implementado todos los endpoints básicos del backend Java Spring Boot con respuestas mock basadas en el análisis del frontend `tacticore-fe-c3`.

---

## 🔗 Endpoints Disponibles

### **Health Check**
- `GET /ping` - Verificar estado del servicio

### **Gestión de Partidas**
- `GET /api/matches` - Lista de partidas
- `GET /api/matches/{id}` - Detalles de partida específica
- `DELETE /api/matches/{id}` - Eliminar partida

### **Análisis de Jugadas**
- `GET /api/matches/{id}/kills` - Timeline de kills de una partida

### **Chat de Análisis**
- `GET /api/matches/{id}/chat` - Obtener mensajes del chat
- `POST /api/matches/{id}/chat` - Enviar mensaje al chat

### **Subida de Archivos**
- `POST /api/upload/dem` - Subir archivo DEM
- `POST /api/upload/video` - Subir archivo de video
- `POST /api/upload/process` - Procesar archivo DEM

### **Analytics**
- `GET /api/analytics/dashboard` - Estadísticas del dashboard
- `GET /api/analytics/historical` - Datos históricos para gráficos

### **Utilidades**
- `GET /api/maps` - Lista de mapas disponibles
- `GET /api/weapons` - Lista de armas disponibles

### **Gestión de Usuario**
- `GET /api/user/profile` - Obtener perfil de usuario
- `PUT /api/user/profile` - Actualizar perfil de usuario

---

## 🏗️ Estructura del Proyecto

```
src/main/java/com/tesis/lambda/
├── controller/
│   ├── ApiController.java          # Endpoints principales
│   ├── UserController.java         # Gestión de usuario
│   └── MatchController.java        # Controlador existente
├── model/
│   ├── Match.java                  # Modelo de partida
│   ├── Kill.java                  # Modelo de kill individual
│   ├── ChatMessage.java           # Modelo de mensaje de chat
│   ├── AnalyticsData.java         # Modelo de datos de analytics
│   ├── DashboardStats.java        # Modelo de estadísticas
│   └── MatchResponse.java         # Modelo existente
├── config/
│   └── WebConfig.java             # Configuración CORS
└── LambdaApplication.java         # Clase principal
```

---

## 🎯 Datos Mock Implementados

### **Partidas de Ejemplo**
- **ID 1**: `dust2_ranked_2024.dem` - Dust2, Ranked, 24 kills, 18 deaths
- **ID 2**: `mirage_casual_2024.dem` - Mirage, Casual, 16 kills, 22 deaths  
- **ID 3**: `inferno_training_2024.dem` - Inferno, Entrenamiento, 31 kills, 12 deaths

### **Kills de Ejemplo**
- 5 kills detallados para la partida ID 1
- Información completa: killer, victim, weapon, round, tiempo, posición
- Clasificación de buenas/malas jugadas

### **Chat de Ejemplo**
- 3 mensajes iniciales para la partida ID 1
- Diferentes tipos de usuario: Analyst, Player, Coach

### **Analytics de Ejemplo**
- 8 días de datos históricos
- Métricas: kills, deaths, KDR, score, goodPlays, badPlays

---

## 🚀 Cómo Ejecutar

### **1. Compilar y Ejecutar**
```bash
cd tesis
mvn spring-boot:run
```

### **2. Verificar que el servidor esté corriendo**
```bash
curl http://localhost:8080/ping
```

### **3. Probar todos los endpoints**
```bash
./test-endpoints.sh
```

---

## 📊 Ejemplos de Respuesta

### **Lista de Partidas**
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

### **Estadísticas del Dashboard**
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

### **Timeline de Kills**
```json
[
  {
    "id": 1,
    "killer": "Player1",
    "victim": "Enemy1",
    "weapon": "AK-47",
    "isGoodPlay": true,
    "round": 3,
    "time": "1:45",
    "teamAlive": {
      "ct": 4,
      "t": 3
    },
    "position": "Long A"
  }
]
```

---

## 🔧 Configuración

### **Puerto del Servidor**
- Puerto: `8080`
- URL base: `http://localhost:8080`

### **CORS**
- Orígenes permitidos: `http://localhost:3000`, `http://localhost:3001`
- Métodos: GET, POST, PUT, DELETE, OPTIONS
- Headers: Todos permitidos

### **Subida de Archivos**
- Tamaño máximo: 100MB por archivo
- Tamaño máximo de request: 100MB

---

## 🧪 Testing

### **Script de Pruebas**
El archivo `test-endpoints.sh` incluye pruebas para todos los endpoints:

1. Health check
2. Lista de partidas
3. Detalles de partida específica
4. Timeline de kills
5. Chat de partida
6. Envío de mensajes
7. Estadísticas del dashboard
8. Analytics históricos
9. Lista de mapas
10. Lista de armas
11. Perfil de usuario
12. Actualización de perfil
13. Subida de archivos DEM
14. Subida de archivos de video
15. Procesamiento de partidas

### **Ejecutar Pruebas**
```bash
./test-endpoints.sh
```

---

## 🔄 Próximos Pasos

### **Implementación Real**
1. **Base de Datos**: Reemplazar datos mock con persistencia real
2. **Autenticación**: Implementar JWT para seguridad
3. **Procesamiento DEM**: Integrar con la librería existente
4. **Almacenamiento**: Configurar S3 para archivos
5. **WebSockets**: Chat en tiempo real

### **Mejoras**
1. **Validación**: Agregar validación de entrada
2. **Error Handling**: Manejo de errores más robusto
3. **Logging**: Logs estructurados
4. **Caché**: Implementar caché para analytics
5. **Rate Limiting**: Limitar requests por usuario

---

## 📝 Notas Técnicas

- **Framework**: Spring Boot 3.x
- **Java**: 17
- **Build Tool**: Maven
- **JSON**: Jackson para serialización
- **CORS**: Configurado para desarrollo local
- **File Upload**: MultipartFile para archivos

Todos los endpoints devuelven respuestas JSON y están listos para ser consumidos por el frontend Next.js.
