# 📋 API Documentation

> Documentación completa de la API REST de TactiCore Backend

## 🚀 Información General

- **Base URL**: `http://localhost:8080`
- **Versión**: 1.0.0
- **Formato**: JSON
- **CORS**: Habilitado para todos los orígenes

## 📚 Secciones

- **[Endpoints](endpoints.md)** - Documentación detallada de todos los endpoints
- **[Ejemplos de Respuesta](examples.md)** - Ejemplos de respuestas de la API
- **[Interfaces TypeScript](typescript.md)** - Definiciones TypeScript para el frontend
- **[Análisis de Kills](kill-analysis.md)** - Endpoints específicos para análisis de kills

## 🎯 Endpoints Principales

### Salud y Estado
- `GET /ping` - Health check
- `GET /api/health` - Estado del sistema

### Partidas
- `GET /api/matches` - Lista de partidas
- `GET /api/matches/{id}` - Detalles de partida
- `GET /api/matches/{id}/kills` - Kills de partida
- `GET /api/matches/{id}/chat` - Chat de partida

### Analytics
- `GET /api/analytics/dashboard` - Dashboard principal
- `GET /api/analytics/historical` - Datos históricos

### Configuración
- `GET /api/maps` - Mapas disponibles
- `GET /api/weapons` - Armas disponibles

## 🔧 Uso Básico

### Ejemplo de Request

```javascript
// Obtener lista de partidas
fetch('/api/matches')
  .then(response => response.json())
  .then(data => console.log(data));

// Obtener kills de una partida específica
fetch('/api/matches/1/kills')
  .then(response => response.json())
  .then(data => console.log(data));
```

### Headers Recomendados

```javascript
const headers = {
  'Accept': 'application/json',
  'Content-Type': 'application/json'
};
```

## 📊 Códigos de Estado

- **200 OK** - Solicitud exitosa
- **400 Bad Request** - Parámetros inválidos
- **404 Not Found** - Recurso no encontrado
- **500 Internal Server Error** - Error interno del servidor

## 🔍 Filtros Disponibles

### Partidas
- `?user=nombre` - Filtrar por usuario
- `?page=1&limit=10` - Paginación

### Kills
- `?user=nombre` - Filtrar por usuario
- `?round=1` - Filtrar por ronda

## 📝 Notas Importantes

- Todos los endpoints retornan JSON
- Los números decimales están redondeados apropiadamente
- Las fechas están en formato ISO 8601
- Los datos se calculan en tiempo real
- La base de datos se reinicia al reiniciar la aplicación
