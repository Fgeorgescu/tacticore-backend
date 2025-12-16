# 📘 Documentación Swagger/OpenAPI

> Especificación OpenAPI 3.0.3 completa de la API de Tacticore Backend

## 📋 Descripción

Este directorio contiene la especificación técnica de la API en formato **OpenAPI 3.0.3** (anteriormente Swagger). Este archivo es consumido por herramientas como:

- **Swagger UI**: Interfaz interactiva para probar endpoints (`http://localhost:8080/swagger-ui.html`)
- **Postman**: Importación automática de colecciones
- **Herramientas de generación de código**: Para crear clientes SDK automáticamente
- **Documentación automatizada**: Generación de documentación desde código

## 📁 Archivos

- **`swagger.yaml`**: Especificación completa de la API en formato OpenAPI 3.0.3

## 🚀 Uso

### Ver en Swagger UI

La aplicación Spring Boot incluye Swagger UI automáticamente. Cuando el backend está corriendo:

```bash
# Asegúrate de que el backend esté corriendo
make run

# Abre en tu navegador:
# http://localhost:8080/swagger-ui.html
```

### Importar en Postman

1. Abre Postman
2. Click en **Import**
3. Selecciona **File** y carga `swagger.yaml`
4. Postman generará automáticamente una colección con todos los endpoints

### Generar Código Cliente

Con herramientas como [OpenAPI Generator](https://openapi-generator.tech/):

```bash
# Generar cliente TypeScript
npx @openapi-generator-plus/cli \
  -i docs-swagger/swagger.yaml \
  -g typescript-fetch \
  -o src/generated/api-client

# Generar cliente Python
openapi-generator generate \
  -i docs-swagger/swagger.yaml \
  -g python \
  -o generated/python-client
```

## 📊 Estructura de la Especificación

La especificación incluye:

### Endpoints Documentados

- ✅ **Partidas** (`/api/matches`) - Gestión completa de partidas
- ✅ **Análisis** (`/api/analysis`) - Análisis de kills y estadísticas
- ✅ **Usuarios** (`/api/users`) - Gestión de usuarios y perfiles
- ✅ **Datos** (`/api/data`) - Carga y gestión de datos
- ✅ **Chat** (`/api/matches/{id}/chat`) - Sistema de chat por partida
- ✅ **Analytics** (`/api/analytics`) - Dashboard y datos históricos
- ✅ **Uploads** (`/api/upload`) - Subida de archivos DEM y videos
- ✅ **Utilidades** (`/api/maps`, `/api/weapons`, `/ping`, `/health`) - Endpoints auxiliares

### Schemas Definidos

- `Match` - Estructura de partida
- `MatchResponse` - Respuesta de estado de partida
- `Kill` - Estructura de kill
- `KillAnalysis` - Análisis completo de kills
- `User` - Estructura de usuario
- `UserProfile` - Perfil completo de usuario
- `ChatMessage` - Mensaje de chat
- `DashboardStats` - Estadísticas del dashboard
- `AnalyticsData` - Datos históricos
- `ErrorResponse` - Respuesta de error
- `SuccessResponse` - Respuesta de éxito

## 🔄 Actualización

Esta especificación se actualiza manualmente cuando:

1. Se agregan nuevos endpoints al backend
2. Se modifican estructuras de datos (DTOs)
3. Se cambian parámetros o respuestas de endpoints existentes

**Proceso de actualización**:

1. Modificar `swagger.yaml` con los nuevos endpoints
2. Verificar que la sintaxis YAML sea válida
3. Probar en Swagger UI que todos los endpoints funcionen
4. Actualizar la documentación en `docs/api/endpoints.md` si es necesario

## 📝 Notas Importantes

- **Formato**: OpenAPI 3.0.3 (YAML)
- **Versión de API**: 1.0.0
- **Última actualización**: Noviembre 2024
- **Cobertura**: ~50+ endpoints documentados

## 🔗 Enlaces Relacionados

- [Documentación de Endpoints](../docs/api/endpoints.md) - Documentación detallada en Markdown
- [Ejemplos de Respuesta](../docs/api/examples.md) - Ejemplos de uso
- [Swagger UI](http://localhost:8080/swagger-ui.html) - Interfaz interactiva (cuando el backend está corriendo)

## 🛠️ Validación

Para validar que el archivo YAML sea correcto:

```bash
# Con npm/yarn
npx swagger-cli validate docs-swagger/swagger.yaml

# Con Python
pip install openapi-spec-validator
openapi-spec-validator docs-swagger/swagger.yaml

# Con Docker
docker run --rm -v $(pwd):/data \
  openapitools/openapi-validator-cli \
  validate -i /data/docs-swagger/swagger.yaml
```

---

**Última actualización**: Noviembre 2024
