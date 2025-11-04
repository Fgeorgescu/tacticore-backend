# Propuesta de Implementación: Waiting Feedback Feature

## Objetivo
Permitir que el frontend muestre una fila en la lista de matches mientras el archivo DEM se está procesando. El procesamiento puede tardar hasta 2 minutos, por lo que necesitamos:
1. Crear el match inmediatamente con estado "processing"
2. Simular el delay del procesamiento (hasta 2 minutos)
3. Actualizar el match cuando el procesamiento termine

## Análisis del Estado Actual

### Componentes Existentes
1. **MatchController** (`/api/matches` POST):
   - Ya crea el match con estado "processing" ✅
   - Ya procesa asíncronamente con `CompletableFuture` ✅
   - Delay actual: 2-5 segundos (necesita ajuste)

2. **DatabaseMatchService**:
   - Ya maneja estados: "processing", "completed", "failed" ✅
   - Ya convierte `MatchEntity` a `MatchDto` ✅
   - **FALTA**: Incluir campo `status` en el `MatchDto`

3. **MatchDto**:
   - **FALTA**: Campo `status` para que el frontend sepa el estado

4. **ApiController** (`/api/matches` GET):
   - Ya retorna la lista de matches ✅
   - Los matches con estado "processing" se muestran con valores por defecto ✅

## Cambios Necesarios

### 1. Agregar campo `status` al `MatchDto`

**Archivo**: `src/main/java/com/tacticore/lambda/model/dto/MatchDto.java`

```java
@JsonProperty("status")
private String status;

public String getStatus() { return status; }
public void setStatus(String status) { this.status = status; }
```

### 2. Modificar `DatabaseMatchService.convertToDto()`

**Archivo**: `src/main/java/com/tacticore/lambda/service/DatabaseMatchService.java`

En el método `convertToDto()`:
```java
private MatchDto convertToDto(MatchEntity entity) {
    MatchDto dto = new MatchDto();
    // ... código existente ...
    dto.setStatus(entity.getStatus()); // Agregar esta línea
    // ...
    return dto;
}
```

Y en `convertToDtoForUser()`:
```java
private MatchDto convertToDtoForUser(MatchEntity entity, String user) {
    MatchDto dto = new MatchDto();
    // ... código existente ...
    dto.setStatus(entity.getStatus()); // Agregar esta línea
    // ...
    return dto;
}
```

### 3. Ajustar delay en `MatchController.simulateAsyncProcessing()`

**Archivo**: `src/main/java/com/tacticore/lambda/controller/MatchController.java`

Cambiar el delay de 2-5 segundos a 0-120 segundos (hasta 2 minutos):

**ANTES:**
```java
Thread.sleep(2000 + (int)(Math.random() * 3000)); // 2-5 segundos
```

**DESPUÉS:**
```java
// Simular delay de procesamiento (0-120 segundos, hasta 2 minutos)
int delaySeconds = (int)(Math.random() * 120000); // 0-120 segundos en milisegundos
Thread.sleep(delaySeconds);
```

**OPCIÓN CONFIGURABLE** (recomendada):
```java
@Value("${match.processing.min-delay-ms:0}")
private int minProcessingDelayMs;

@Value("${match.processing.max-delay-ms:120000}")
private int maxProcessingDelayMs;

// En simulateAsyncProcessing():
int delayMs = minProcessingDelayMs + 
    (int)(Math.random() * (maxProcessingDelayMs - minProcessingDelayMs));
Thread.sleep(delayMs);
```

Y agregar en `application.properties`:
```properties
# Match processing simulation delays (in milliseconds)
match.processing.min-delay-ms=0
match.processing.max-delay-ms=120000
```

### 4. Verificar comportamiento actual

El flujo actual ya funciona correctamente:
1. ✅ `MatchController.uploadMatch()` crea el match con `status="processing"`
2. ✅ Retorna inmediatamente `MatchResponse.processing(matchId)`
3. ✅ El match aparece en `/api/matches` con estado "processing"
4. ✅ `simulateAsyncProcessing()` ejecuta en background
5. ✅ Al terminar, actualiza el match con `status="completed"` y datos reales

## Firma del Endpoint

### POST `/api/matches`
**Request:**
```http
POST /api/matches
Content-Type: multipart/form-data

demFile: <archivo.dem>
videoFile: <archivo.mp4> (opcional)
metadata: <JSON string> (opcional)
```

**Response (Inmediata):**
```json
{
  "id": "match_1234567890",
  "status": "processing",
  "message": "Match uploaded successfully and is being processed"
}
```

### GET `/api/matches`
**Response:**
```json
{
  "matches": [
    {
      "id": "match_1234567890",
      "fileName": "mirage.dem",
      "hasVideo": false,
      "map": "Unknown",
      "gameType": "Ranked",
      "kills": 0,
      "deaths": 0,
      "goodPlays": 0,
      "badPlays": 0,
      "duration": "00:00",
      "score": 0.0,
      "date": "2025-11-02T22:57:03.565063",
      "status": "processing"  // ← NUEVO CAMPO
    },
    {
      "id": "match_1234567891",
      "fileName": "inferno.dem",
      // ... otros campos ...
      "status": "completed"  // ← Cuando termine el procesamiento
    }
  ]
}
```

### GET `/api/matches/{matchId}/status`
**Response:**
```json
{
  "id": "match_1234567890",
  "status": "processing",  // "processing" | "completed" | "failed"
  "message": "Match is being processed"
}
```

## Flujo Completo

1. **Frontend sube archivo** → `POST /api/matches`
2. **Backend responde inmediatamente** → `MatchResponse.processing(matchId)`
3. **Backend guarda match en DB** → `status="processing"`, valores por defecto
4. **Backend inicia procesamiento asíncrono** → `CompletableFuture.runAsync()`
5. **Frontend hace polling** → `GET /api/matches` muestra match con `status="processing"`
6. **Backend simula delay** → 0-120 segundos
7. **Backend procesa archivo** → Llama a `MLServiceClient.analyzeDemoFile()`
8. **Backend actualiza match** → `status="completed"`, datos reales
9. **Frontend refresca lista** → `GET /api/matches` muestra match con `status="completed"` y datos reales

## Consideraciones Adicionales

### Manejo de Errores
- Si el procesamiento falla, el match se actualiza con `status="failed"`
- El frontend puede mostrar un mensaje de error

### Polling vs WebSockets
- **Opción actual**: Frontend hace polling de `GET /api/matches` cada X segundos
- **Opción futura**: Usar WebSockets para notificar cuando el match termine

### Testing
- Verificar que matches con `status="processing"` aparecen en la lista
- Verificar que los valores por defecto se muestran correctamente
- Verificar que después del procesamiento, el match se actualiza correctamente

## Archivos a Modificar

1. `src/main/java/com/tacticore/lambda/model/dto/MatchDto.java` - Agregar campo `status`
2. `src/main/java/com/tacticore/lambda/service/DatabaseMatchService.java` - Incluir `status` en conversión
3. `src/main/java/com/tacticore/lambda/controller/MatchController.java` - Ajustar delay
4. `src/main/resources/application.properties` - Agregar configuración de delays (opcional)

## Beneficios

1. ✅ Mejor UX: El usuario ve inmediatamente que su archivo se está procesando
2. ✅ Feedback visual: El frontend puede mostrar indicadores de carga
3. ✅ No bloqueante: El backend no espera, responde inmediatamente
4. ✅ Escalable: Puede procesar múltiples matches simultáneamente
5. ✅ Manejo de errores: Los errores se reflejan en el estado del match

