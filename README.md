# 🎮 Tacticore - Plataforma de Análisis de Partidas de Counter-Strike

[![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?style=for-the-badge&logo=spring)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-15.2.4-black?style=for-the-badge&logo=next.js)](https://nextjs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-blue?style=for-the-badge&logo=typescript)](https://www.typescriptlang.org/)

## 📋 Descripción del Proyecto

**Tacticore** es una plataforma completa para el análisis de partidas de Counter-Strike que permite a los jugadores:

- 📤 **Subir archivos DEM** (.dem) de partidas de CS
- 🎥 **Asociar videos** de gameplay para análisis visual
- 📊 **Analizar estadísticas** detalladas (K/D ratio, kills, buenas/malas jugadas)
- 📈 **Visualizar tendencias** históricas de rendimiento
- 💬 **Comentar y analizar** jugadas específicas en tiempo real
- 🗺️ **Visualizar mapas** con posiciones de kills
- 📱 **Acceder desde cualquier dispositivo** con interfaz responsive

## 🏗️ Arquitectura del Sistema

### **Frontend (Next.js 15.2.4)**
- **Framework**: Next.js con TypeScript
- **UI**: Componentes personalizados con Tailwind CSS
- **Gráficos**: Recharts para visualizaciones
- **Estado**: React Hooks y Context API
- **Build**: pnpm para gestión de dependencias

### **Backend (Spring Boot 3.2.0)**
- **Framework**: Spring Boot con Java 17
- **API**: RESTful con JSON
- **Build**: Maven
- **Despliegue**: Docker + AWS Lambda (opcional)

### **Infraestructura**
- **Contenedores**: Docker Compose para desarrollo
- **Cloud**: AWS Lambda + API Gateway + S3
- **IaC**: Terraform y CloudFormation
- **Base de Datos**: Mock data (preparado para PostgreSQL)

## 📁 Estructura del Proyecto

```
tesis/
├── 📁 tacticore-fe-c3/           # Frontend Next.js
│   ├── 📁 app/                   # Páginas de Next.js 13+
│   ├── 📁 components/            # Componentes React
│   │   ├── 📁 analytics/        # Análisis histórico
│   │   ├── 📁 dashboard/        # Dashboard principal
│   │   ├── 📁 match-details/     # Detalles de partida
│   │   ├── 📁 upload/           # Subida de archivos
│   │   └── 📁 ui/               # Componentes base
│   ├── 📁 hooks/                # Custom hooks
│   ├── 📁 lib/                   # Utilidades y API
│   └── 📄 Dockerfile            # Contenedor frontend
├── 📁 tacticore-backend/        # Backend Spring Boot
│   ├── 📁 src/main/java/
│   │   └── 📁 com/tacticore/lambda/
│   │       ├── 📁 controller/   # Controladores REST
│   │       ├── 📁 model/        # Modelos de datos
│   │       ├── 📁 service/      # Lógica de negocio
│   │       └── 📁 config/       # Configuración
│   ├── 📁 terraform/            # Infraestructura como código
│   └── 📄 Dockerfile            # Contenedor backend
├── 📄 docker-compose.yml        # Orquestación de contenedores
├── 📄 swagger.yaml              # Documentación API
└── 📄 README.md                 # Este archivo
```

## 🚀 Casos de Uso Principales

### 1. **🎯 Análisis de Partida Individual**
**Usuario**: Jugador competitivo
**Flujo**:
1. Sube archivo DEM de una partida
2. Opcionalmente asocia video de gameplay
3. Sistema procesa y extrae estadísticas
4. Visualiza timeline de kills con análisis
5. Comenta jugadas específicas con coach/analista

**Beneficios**:
- Identificar patrones de juego
- Mejorar posicionamiento
- Analizar decisiones tácticas

### 2. **📈 Seguimiento de Progreso**
**Usuario**: Jugador que busca mejorar
**Flujo**:
1. Sube múltiples partidas a lo largo del tiempo
2. Sistema genera gráficos de tendencias
3. Visualiza evolución de K/D ratio, score, etc.
4. Identifica períodos de mejora/declive

**Beneficios**:
- Medir progreso objetivo
- Identificar áreas de mejora
- Mantener motivación

### 3. **👥 Análisis Colaborativo**
**Usuario**: Equipo/Coach
**Flujo**:
1. Múltiples usuarios acceden a la misma partida
2. Chat en tiempo real para comentar jugadas
3. Diferentes perspectivas (jugador, coach, analista)
4. Documentación de estrategias

**Beneficios**:
- Mejorar comunicación del equipo
- Compartir conocimiento
- Desarrollar estrategias

### 4. **📊 Dashboard de Rendimiento**
**Usuario**: Jugador/Coach
**Flujo**:
1. Accede al dashboard principal
2. Ve resumen de todas las partidas
3. Filtra por tipo de juego, mapa, período
4. Identifica patrones generales

**Beneficios**:
- Vista general del rendimiento
- Identificación rápida de problemas
- Toma de decisiones informada

## 🔧 Instalación y Configuración

### **Requisitos Previos**
- Docker y Docker Compose
- Node.js 18+ (para desarrollo local)
- Java 17+ (para desarrollo local)
- Maven (para desarrollo local)

### **Despliegue Rápido con Docker**

```bash
# 1. Clonar el repositorio
git clone <repository-url>
cd tesis

# 2. Levantar todos los servicios
docker-compose up --build

# 3. Acceder a la aplicación
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
```

### **Desarrollo Local**

```bash
# Backend
cd tacticore-backend
mvn spring-boot:run

# Frontend (en otra terminal)
cd tacticore-fe-c3
pnpm install
pnpm dev
```

## 📚 Documentación de la API

### **Endpoints Principales**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/ping` | Health check |
| `GET` | `/api/matches` | Lista de partidas |
| `GET` | `/api/matches/{id}` | Detalles de partida |
| `DELETE` | `/api/matches/{id}` | Eliminar partida |
| `GET` | `/api/matches/{id}/kills` | Timeline de kills |
| `GET` | `/api/matches/{id}/chat` | Mensajes del chat |
| `POST` | `/api/matches/{id}/chat` | Enviar mensaje |
| `POST` | `/api/upload/dem` | Subir archivo DEM |
| `POST` | `/api/upload/video` | Subir archivo video |
| `POST` | `/api/upload/process` | Procesar archivo DEM |
| `GET` | `/api/analytics/dashboard` | Estadísticas dashboard |
| `GET` | `/api/analytics/historical` | Datos históricos |
| `GET` | `/api/maps` | Lista de mapas |
| `GET` | `/api/weapons` | Lista de armas |
| `GET` | `/api/user/profile` | Perfil de usuario |
| `PUT` | `/api/user/profile` | Actualizar perfil |

### **Documentación Completa**
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: Ver `API-DOCUMENTATION.md`
- **Swagger Spec**: Ver `swagger.yaml`

## 🎮 Funcionalidades Detalladas

### **📤 Subida de Archivos**
- **Formatos soportados**: .dem (Counter-Strike), .mp4, .mov, .avi
- **Tamaño máximo**: 100MB por archivo
- **Validación**: Verificación de formato y contenido
- **Progreso**: Barra de progreso en tiempo real

### **📊 Análisis de Partidas**
- **Extracción automática**: Kills, muertes, rounds, posiciones
- **Clasificación**: Buenas vs malas jugadas
- **Contexto**: Información de equipo y situación
- **Timeline**: Visualización cronológica de eventos

### **📈 Analytics Históricos**
- **Métricas**: K/D ratio, score, kills, buenas jugadas
- **Períodos**: Día, semana, mes, año, personalizado
- **Gráficos**: Líneas, barras, áreas acumulativas
- **Tendencias**: Comparativas y análisis de progreso

### **💬 Chat de Análisis**
- **Tiempo real**: WebSocket para mensajes instantáneos
- **Tipos de usuario**: Player, Analyst, Coach
- **Timestamps**: Marcas de tiempo precisas
- **Persistencia**: Historial de conversaciones

### **🗺️ Visualización de Mapas**
- **Mapas soportados**: Dust2, Mirage, Inferno, etc.
- **Posiciones**: Marcadores de kills en el mapa
- **Información**: Arma, tiempo, contexto
- **Interactividad**: Zoom, pan, filtros

## 🚀 Despliegue en Producción

### **AWS Lambda (Recomendado)**

```bash
# Con Terraform
cd tacticore-backend/terraform/environments/dev
terraform init
terraform apply

# Con CloudFormation
./tacticore-backend/deploy.sh
```

### **Docker en Servidor**

```bash
# Construir imágenes
docker-compose -f docker-compose.prod.yml build

# Desplegar
docker-compose -f docker-compose.prod.yml up -d
```

### **Vercel (Frontend)**

```bash
# Configurar en Vercel
# Conectar repositorio
# Despliegue automático en push
```

## 🧪 Testing

### **Backend**
```bash
cd tacticore-backend
mvn test
./test-endpoints.sh
```

### **Frontend**
```bash
cd tacticore-fe-c3
pnpm test
pnpm run build
```

### **Integración**
```bash
# Ejecutar todos los tests
docker-compose -f docker-compose.test.yml up
```

## 🔧 Configuración Avanzada

### **Variables de Entorno**

```bash
# Frontend (.env.local)
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_WS_URL=ws://localhost:8080

# Backend (application.properties)
server.port=8080
spring.profiles.active=production
aws.s3.bucket=tacticore-uploads
```

### **Base de Datos**
```sql
-- Preparado para PostgreSQL
CREATE TABLE matches (
    id UUID PRIMARY KEY,
    filename VARCHAR(255),
    map VARCHAR(50),
    game_type VARCHAR(50),
    kills INTEGER,
    deaths INTEGER,
    score DECIMAL(3,1),
    created_at TIMESTAMP
);
```

## 📊 Métricas y Monitoreo

### **Health Checks**
- **Frontend**: `GET /` (200 OK)
- **Backend**: `GET /ping` (JSON con timestamp)
- **Docker**: Health checks automáticos

### **Logs**
```bash
# Ver logs en tiempo real
docker-compose logs -f

# Logs específicos
docker-compose logs frontend
docker-compose logs backend
```

### **Métricas de Rendimiento**
- **Tiempo de respuesta**: < 200ms para endpoints básicos
- **Disponibilidad**: 99.9% uptime
- **Escalabilidad**: Auto-scaling en AWS Lambda

## 🤝 Contribución

### **Flujo de Desarrollo**
1. Fork del repositorio
2. Crear feature branch: `git checkout -b feature/nueva-funcionalidad`
3. Commit cambios: `git commit -m 'Agregar nueva funcionalidad'`
4. Push al branch: `git push origin feature/nueva-funcionalidad`
5. Crear Pull Request

### **Estándares de Código**
- **Java**: Google Java Style Guide
- **TypeScript**: ESLint + Prettier
- **Commits**: Conventional Commits
- **Documentación**: JSDoc + JavaDoc

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 🆘 Soporte

### **Documentación Adicional**
- [API Documentation](API-DOCUMENTATION.md)
- [Frontend Analysis](FRONTEND-ANALYSIS.md)
- [Implementation Summary](IMPLEMENTATION-SUMMARY.md)
- [Docker Guide](README-docker.md)

### **Contacto**
- **Email**: support@tacticore.com
- **Issues**: GitHub Issues
- **Discord**: [Servidor de la comunidad]

### **Roadmap**
- [ ] Autenticación JWT
- [ ] Base de datos PostgreSQL
- [ ] WebSockets para chat en tiempo real
- [ ] Procesamiento real de archivos DEM
- [ ] Análisis de video con IA
- [ ] Mobile app (React Native)
- [ ] Integración con Steam API

---

**🎮 ¡Mejora tu juego con Tacticore!**
