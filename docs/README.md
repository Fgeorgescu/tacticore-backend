# TactiCore Backend - Documentación

## 📚 Bienvenido a la Documentación

Esta es la documentación completa del backend de TactiCore, un proyecto Spring Boot con AWS Lambda para análisis de partidas de Counter-Strike.

## 🚀 Acceso Rápido

- **Documentación Local**: `make docs-serve` (http://localhost:3000)
- **GitHub Pages**: https://fgeorgescu.github.io/tacticore-backend/
- **API Swagger**: http://localhost:8080/swagger-ui.html

## 📋 Estructura de la Documentación

### 🔌 API
- **[Endpoints](./api/endpoints.md)** - Documentación completa de endpoints
- **[Ejemplos](./api/examples.md)** - Ejemplos de uso y respuestas
- **[Interfaces TypeScript](./api/typescript.md)** - Tipos para frontend
- **[Análisis de Kills](./api/kill-analysis.md)** - Algoritmos y métricas

### 🏗️ Infraestructura
- **[Terraform](./infrastructure/terraform.md)** - Infraestructura como código
- **[AWS Lambda](./infrastructure/lambda.md)** - Configuración de Lambda
- **[Base de Datos](./infrastructure/database.md)** - H2 y JPA
- **[Docker](./infrastructure/docker.md)** - Contenedores

### 💻 Desarrollo
- **[Configuración](./development/setup.md)** - Setup del entorno
- **[Estructura](./development/structure.md)** - Arquitectura del proyecto
- **[Makefile](./development/makefile.md)** - Comandos disponibles
- **[Testing](./development/testing.md)** - Pruebas y CI

### 🔄 CI/CD
- **[GitHub Actions](./ci-cd/github-actions.md)** - Pipelines de CI/CD
- **[Calidad](./ci-cd/quality.md)** - Análisis de código
- **[Despliegue](./ci-cd/deployment.md)** - Estrategias de deploy
- **[GitHub Pages](./ci-cd/github-pages.md)** - Documentación web

### 📖 Referencias
- **[Tecnologías](./references/technologies.md)** - Stack tecnológico
- **[Enlaces](./references/links.md)** - Recursos útiles
- **[Solución de Problemas](./references/troubleshooting.md)** - FAQ y fixes

## 🛠️ Comandos Útiles

```bash
# Servir documentación localmente
make docs-serve

# Construir documentación
make docs-build

# Abrir documentación en navegador
make docs-open

# Abrir GitHub Pages
make docs-github

# Ejecutar aplicación
make run

# Ejecutar tests
make test

# Análisis de calidad
make quality
```

## 🔧 Configuración

### Requisitos
- Java 17+
- Maven 3.8+
- Python 3+ (para documentación)

### Variables de Entorno
```bash
# Base de datos H2
export SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
export SPRING_DATASOURCE_USERNAME=sa
export SPRING_DATASOURCE_PASSWORD=password

# Puerto de la aplicación
export SERVER_PORT=8080
```

## 📊 Características Principales

- ✅ **API REST** completa para análisis de kills
- ✅ **Base de datos H2** en memoria
- ✅ **Análisis en tiempo real** de partidas
- ✅ **Filtrado por usuario** y rondas
- ✅ **Documentación interactiva** con Docsify
- ✅ **CI/CD** con GitHub Actions
- ✅ **Infraestructura** con Terraform
- ✅ **Despliegue** en AWS Lambda

## 🤝 Contribución

1. Fork el repositorio
2. Crea un branch: `git checkout -b feature/nueva-funcionalidad`
3. Commit cambios: `git commit -m 'Agregar nueva funcionalidad'`
4. Push al branch: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

## 📞 Soporte

- **Issues**: [GitHub Issues](https://github.com/Fgeorgescu/tacticore-backend/issues)
- **Documentación**: Esta documentación
- **API**: Swagger UI en `/swagger-ui.html`

---

**Última actualización**: Enero 2024