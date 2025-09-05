# Documentación de la API de Análisis de Kills

## Índice de Documentación

Esta carpeta contiene toda la documentación necesaria para entender y utilizar la API de análisis de kills de Counter-Strike.

### 📋 Documentos Principales

1. **[API-ENDPOINTS-DOCUMENTATION.md](./API-ENDPOINTS-DOCUMENTATION.md)**
   - Documentación detallada de cada endpoint
   - Parámetros, respuestas y ejemplos de uso
   - Casos de uso específicos para frontend
   - Manejo de errores y códigos de estado

2. **[API-RESPONSE-EXAMPLES.md](./API-RESPONSE-EXAMPLES.md)**
   - Ejemplos completos de respuestas JSON
   - Estructuras de datos para diferentes escenarios
   - Datos de ejemplo para desarrollo frontend
   - Validaciones y constantes recomendadas

3. **[FRONTEND-TYPESCRIPT-INTERFACES.md](./FRONTEND-TYPESCRIPT-INTERFACES.md)**
   - Interfaces TypeScript completas
   - Servicios y hooks de React
   - Utilidades y formateadores
   - Configuración de TypeScript

4. **[TECHNICAL-DOCUMENTATION.md](./TECHNICAL-DOCUMENTATION.md)**
   - Documentación técnica completa de la implementación
   - Estructura de la base de datos y entidades JPA
   - Servicios, repositorios y controladores
   - Consideraciones técnicas y arquitectura

5. **[KILL-ANALYSIS-RECOMMENDATIONS.md](./KILL-ANALYSIS-RECOMMENDATIONS.md)**
   - Recomendaciones de algoritmos
   - Patrones identificados en los datos
   - Métricas clave para dashboards
   - Consideraciones de implementación

---

## 🚀 Inicio Rápido

### Para Desarrolladores Frontend

1. **Leer la documentación de endpoints**: [API-ENDPOINTS-DOCUMENTATION.md](./API-ENDPOINTS-DOCUMENTATION.md)
2. **Revisar ejemplos de respuestas**: [API-RESPONSE-EXAMPLES.md](./API-RESPONSE-EXAMPLES.md)
3. **Implementar interfaces TypeScript**: [FRONTEND-TYPESCRIPT-INTERFACES.md](./FRONTEND-TYPESCRIPT-INTERFACES.md)

### Para Desarrolladores Backend

1. **Entender la implementación**: [TECHNICAL-DOCUMENTATION.md](./TECHNICAL-DOCUMENTATION.md)
2. **Revisar recomendaciones**: [KILL-ANALYSIS-RECOMMENDATIONS.md](./KILL-ANALYSIS-RECOMMENDATIONS.md)

---

## 📊 Endpoints Disponibles

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/analysis/overview` | GET | Análisis general de todos los kills |
| `/api/analysis/player/{name}` | GET | Estadísticas de jugador específico |
| `/api/analysis/round/{number}` | GET | Análisis de ronda específica |
| `/api/data/load` | POST | Cargar datos desde JSON |
| `/api/data/clear` | DELETE | Limpiar todos los datos |
| `/api/data/status` | GET | Estado de la base de datos |

---

## 🔧 Configuración

### Base de Datos H2
- **URL**: `jdbc:h2:mem:testdb`
- **Consola**: `http://localhost:8080/h2-console`
- **Usuario**: `sa`
- **Contraseña**: `password`

### Aplicación
- **Puerto**: `8080`
- **Base URL**: `http://localhost:8080`
- **CORS**: Habilitado para todos los orígenes

---

## 📈 Datos de Ejemplo

La aplicación incluye datos de ejemplo con:
- **143 kills** de Counter-Strike
- **10 jugadores** diferentes
- **21 rondas** de juego
- **Predicciones del modelo IA** para cada kill
- **Información detallada** de contexto y posiciones

---

## 🧪 Pruebas

### Script de Prueba
```bash
./test-kill-analysis-api.sh
```

### Ejemplos de Uso
```bash
# Análisis general
curl http://localhost:8080/api/analysis/overview

# Estadísticas de jugador
curl http://localhost:8080/api/analysis/player/makazze

# Análisis de ronda
curl http://localhost:8080/api/analysis/round/1

# Estado de datos
curl http://localhost:8080/api/data/status
```

---

## 📝 Notas Importantes

### Para el Frontend
- Todos los endpoints retornan JSON
- Los datos se calculan en tiempo real
- Implementar manejo de errores apropiado
- Considerar estados de carga y validación

### Para el Backend
- Base de datos en memoria (se pierde al reiniciar)
- Datos se cargan automáticamente al iniciar
- Estructura preparada para escalabilidad
- Consultas optimizadas para rendimiento

---

## 🤝 Contribución

Para agregar nuevos endpoints o funcionalidades:

1. Actualizar las entidades JPA si es necesario
2. Agregar métodos al repositorio correspondiente
3. Implementar lógica en el servicio
4. Crear endpoint en el controlador
5. Actualizar la documentación
6. Agregar tests si es necesario

---

## 📞 Soporte

Para preguntas o problemas:

1. Revisar la documentación correspondiente
2. Verificar los ejemplos de uso
3. Consultar los logs de la aplicación
4. Usar la consola H2 para inspeccionar datos

---

## 🔄 Actualizaciones

Esta documentación se actualiza cuando:
- Se agregan nuevos endpoints
- Se modifican estructuras de datos
- Se implementan nuevas funcionalidades
- Se corrigen errores o mejoran ejemplos

**Última actualización**: Enero 2024
