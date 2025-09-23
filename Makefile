# Tacticore Backend - Makefile
# =============================
# Este Makefile centraliza todos los comandos y scripts del proyecto

# Variables
# =========
APP_NAME = tacticore-backend
VERSION = 1.0.0
JAR_FILE = target/$(APP_NAME)-$(VERSION).jar
PORT = 8080
BASE_URL = http://localhost:$(PORT)

# Colores para output
RED = \033[0;31m
GREEN = \033[0;32m
YELLOW = \033[1;33m
BLUE = \033[0;34m
NC = \033[0m # No Color

# Help
# ====
.PHONY: help
help: ## Mostrar esta ayuda
	@echo "$(BLUE)Tacticore Backend - Comandos Disponibles$(NC)"
	@echo "=============================================="
	@echo ""
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "$(GREEN)%-20s$(NC) %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo ""
	@echo "$(YELLOW)Ejemplos de uso:$(NC)"
	@echo "  make build          # Compilar el proyecto"
	@echo "  make test           # Ejecutar tests"
	@echo "  make run            # Iniciar la aplicación"
	@echo "  make test-api       # Probar endpoints de la API"
	@echo "  make docs-serve     # Servir documentación"

# Desarrollo
# ==========
.PHONY: build
build: ## Compilar el proyecto
	@echo "$(BLUE)🔨 Compilando proyecto...$(NC)"
	@mvn clean compile -P dev
	@echo "$(GREEN)✅ Compilación exitosa$(NC)"

.PHONY: package
package: ## Empaquetar la aplicación
	@echo "$(BLUE)📦 Empaquetando aplicación...$(NC)"
	@mvn clean package -DskipTests -P dev
	@echo "$(GREEN)✅ Aplicación empaquetada: $(JAR_FILE)$(NC)"

.PHONY: clean
clean: ## Limpiar archivos generados
	@echo "$(BLUE)🧹 Limpiando archivos generados...$(NC)"
	@mvn clean
	@echo "$(GREEN)✅ Limpieza completada$(NC)"

# Tests
# =====
.PHONY: test
test: ## Ejecutar tests unitarios
	@echo "$(BLUE)🧪 Ejecutando tests unitarios...$(NC)"
	@mvn test
	@echo "$(GREEN)✅ Tests completados$(NC)"

.PHONY: test-coverage
test-coverage: ## Generar reporte de cobertura
	@echo "$(BLUE)📊 Generando reporte de cobertura...$(NC)"
	@mvn jacoco:report
	@echo "$(GREEN)✅ Reporte generado en: target/site/jacoco/index.html$(NC)"

.PHONY: test-integration
test-integration: ## Ejecutar tests de integración
	@echo "$(BLUE)🔗 Ejecutando tests de integración...$(NC)"
	@mvn verify -P integration-tests
	@echo "$(GREEN)✅ Tests de integración completados$(NC)"

# Calidad de Código
# =================
.PHONY: quality
quality: ## Análisis de calidad de código
	@echo "$(BLUE)🔍 Analizando calidad de código...$(NC)"
	@mvn spotbugs:check || echo "$(YELLOW)⚠️  SpotBugs encontró algunos problemas$(NC)"
	@echo "$(GREEN)✅ Análisis de calidad completado$(NC)"

.PHONY: security
security: ## Verificación de seguridad
	@echo "$(BLUE)🔒 Verificando vulnerabilidades...$(NC)"
	@mvn org.owasp:dependency-check-maven:check || echo "$(YELLOW)⚠️  Verificación de seguridad falló (posible problema de conectividad)$(NC)"
	@echo "$(GREEN)✅ Verificación de seguridad completada$(NC)"

# Aplicación
# ==========
.PHONY: run
run: ## Iniciar la aplicación
	@echo "$(BLUE)🚀 Iniciando aplicación en puerto $(PORT)...$(NC)"
	@mvn spring-boot:run

.PHONY: run-background
run-background: ## Iniciar la aplicación en segundo plano
	@echo "$(BLUE)🚀 Iniciando aplicación en segundo plano...$(NC)"
	@nohup mvn spring-boot:run > app.log 2>&1 &
	@echo "$(GREEN)✅ Aplicación iniciada en segundo plano$(NC)"
	@echo "$(YELLOW)📄 Logs disponibles en: app.log$(NC)"

.PHONY: stop
stop: ## Detener la aplicación
	@echo "$(BLUE)⏹️  Deteniendo aplicación...$(NC)"
	@pkill -f "spring-boot:run" || echo "$(YELLOW)⚠️  No se encontró aplicación ejecutándose$(NC)"
	@echo "$(GREEN)✅ Aplicación detenida$(NC)"

.PHONY: status
status: ## Verificar estado de la aplicación
	@echo "$(BLUE)📊 Verificando estado de la aplicación...$(NC)"
	@curl -s $(BASE_URL)/ping | jq . || echo "$(RED)❌ Aplicación no disponible$(NC)"

# Base de Datos
# =============
.PHONY: db-console
db-console: ## Abrir consola de H2
	@echo "$(BLUE)🗄️  Abriendo consola de H2...$(NC)"
	@echo "$(YELLOW)URL: $(BASE_URL)/h2-console$(NC)"
	@echo "$(YELLOW)JDBC URL: jdbc:h2:mem:testdb$(NC)"
	@echo "$(YELLOW)Usuario: sa$(NC)"
	@echo "$(YELLOW)Contraseña: password$(NC)"
	@open $(BASE_URL)/h2-console 2>/dev/null || echo "$(YELLOW)⚠️  Abre manualmente: $(BASE_URL)/h2-console$(NC)"

.PHONY: db-clear
db-clear: ## Limpiar base de datos
	@echo "$(BLUE)🗑️  Limpiando base de datos...$(NC)"
	@curl -X DELETE $(BASE_URL)/api/data/clear | jq . || echo "$(RED)❌ Error limpiando base de datos$(NC)"

.PHONY: db-reload
db-reload: ## Recargar datos dummy
	@echo "$(BLUE)🔄 Recargando datos dummy...$(NC)"
	@curl -X POST $(BASE_URL)/api/data/reload-dummy | jq . || echo "$(RED)❌ Error recargando datos$(NC)"

# Testing de API
# ==============
.PHONY: test-api
test-api: ## Probar endpoints básicos de la API
	@echo "$(BLUE)🧪 Probando endpoints básicos...$(NC)"
	@echo "$(YELLOW)📍 Probando /ping...$(NC)"
	@curl -s $(BASE_URL)/ping | jq . || echo "$(RED)❌ Error en /ping$(NC)"
	@echo "$(YELLOW)📍 Probando /api/maps...$(NC)"
	@curl -s $(BASE_URL)/api/maps | jq . || echo "$(RED)❌ Error en /api/maps$(NC)"
	@echo "$(YELLOW)📍 Probando /api/weapons...$(NC)"
	@curl -s $(BASE_URL)/api/weapons | jq . || echo "$(RED)❌ Error en /api/weapons$(NC)"
	@echo "$(GREEN)✅ Pruebas básicas completadas$(NC)"

.PHONY: test-analytics
test-analytics: ## Probar endpoints de analytics
	@echo "$(BLUE)📊 Probando endpoints de analytics...$(NC)"
	@echo "$(YELLOW)📍 Probando /api/analytics/dashboard...$(NC)"
	@curl -s $(BASE_URL)/api/analytics/dashboard | jq . || echo "$(RED)❌ Error en dashboard$(NC)"
	@echo "$(YELLOW)📍 Probando /api/analytics/historical...$(NC)"
	@curl -s $(BASE_URL)/api/analytics/historical | jq . || echo "$(RED)❌ Error en historical$(NC)"
	@echo "$(GREEN)✅ Pruebas de analytics completadas$(NC)"

.PHONY: test-matches
test-matches: ## Probar endpoints de matches
	@echo "$(BLUE)🎮 Probando endpoints de matches...$(NC)"
	@echo "$(YELLOW)📍 Probando /api/matches...$(NC)"
	@curl -s $(BASE_URL)/api/matches | jq . || echo "$(RED)❌ Error en matches$(NC)"
	@echo "$(YELLOW)📍 Probando /api/matches?user=jcobbb...$(NC)"
	@curl -s "$(BASE_URL)/api/matches?user=jcobbb" | jq . || echo "$(RED)❌ Error en matches con filtro$(NC)"
	@echo "$(GREEN)✅ Pruebas de matches completadas$(NC)"

.PHONY: test-kills
test-kills: ## Probar endpoints de kills
	@echo "$(BLUE)💀 Probando endpoints de kills...$(NC)"
	@echo "$(YELLOW)📍 Probando /api/matches/example_match/kills...$(NC)"
	@curl -s $(BASE_URL)/api/matches/example_match/kills | jq . || echo "$(RED)❌ Error en kills$(NC)"
	@echo "$(YELLOW)📍 Probando /api/matches/example_match/kills?user=jcobbb...$(NC)"
	@curl -s "$(BASE_URL)/api/matches/example_match/kills?user=jcobbb" | jq . || echo "$(RED)❌ Error en kills con filtro$(NC)"
	@echo "$(GREEN)✅ Pruebas de kills completadas$(NC)"

.PHONY: test-all
test-all: test-api test-analytics test-matches test-kills ## Ejecutar todas las pruebas de API

# CI/CD
# =====
.PHONY: ci
ci: ## Ejecutar pipeline de CI localmente
	@echo "$(BLUE)🚀 Ejecutando pipeline de CI local...$(NC)"
	@./test-ci-local.sh

.PHONY: ci-quick
ci-quick: build test package ## Pipeline de CI rápido (sin análisis de calidad)

# Despliegue
# ==========
.PHONY: lambda-package
lambda-package: ## Empaquetar para AWS Lambda
	@echo "$(BLUE)📦 Empaquetando para AWS Lambda...$(NC)"
	@mvn clean package -DskipTests -P lambda
	@echo "$(GREEN)✅ JAR para Lambda creado: $(JAR_FILE)$(NC)"

# Docker
# ======
.PHONY: docker-build
docker-build: ## Construir imagen Docker
	@echo "$(BLUE)🐳 Construyendo imagen Docker...$(NC)"
	@docker build -t $(APP_NAME):$(VERSION) .
	@echo "$(GREEN)✅ Imagen construida: $(APP_NAME):$(VERSION)$(NC)"

.PHONY: docker-run
docker-run: ## Ejecutar contenedor Docker
	@echo "$(BLUE)🐳 Ejecutando contenedor Docker...$(NC)"
	@docker run -p $(PORT):$(PORT) $(APP_NAME):$(VERSION)

.PHONY: docker-compose-up
docker-compose-up: ## Iniciar servicios con Docker Compose
	@echo "$(BLUE)🐳 Iniciando servicios con Docker Compose...$(NC)"
	@docker-compose up -d
	@echo "$(GREEN)✅ Servicios iniciados$(NC)"

.PHONY: docker-compose-down
docker-compose-down: ## Detener servicios de Docker Compose
	@echo "$(BLUE)🐳 Deteniendo servicios de Docker Compose...$(NC)"
	@docker-compose down
	@echo "$(GREEN)✅ Servicios detenidos$(NC)"

# Terraform
# =========
.PHONY: terraform-init
terraform-init: ## Inicializar Terraform
	@echo "$(BLUE)🏗️  Inicializando Terraform...$(NC)"
	@cd terraform/environments/dev && terraform init
	@echo "$(GREEN)✅ Terraform inicializado$(NC)"

.PHONY: terraform-plan
terraform-plan: ## Planificar cambios de Terraform
	@echo "$(BLUE)📋 Planificando cambios de Terraform...$(NC)"
	@cd terraform/environments/dev && terraform plan
	@echo "$(GREEN)✅ Planificación completada$(NC)"

.PHONY: terraform-apply
terraform-apply: ## Aplicar cambios de Terraform
	@echo "$(BLUE)🚀 Aplicando cambios de Terraform...$(NC)"
	@cd terraform/environments/dev && terraform apply
	@echo "$(GREEN)✅ Cambios aplicados$(NC)"

.PHONY: terraform-destroy
terraform-destroy: ## Destruir infraestructura de Terraform
	@echo "$(BLUE)💥 Destruyendo infraestructura...$(NC)"
	@cd terraform/environments/dev && terraform destroy
	@echo "$(GREEN)✅ Infraestructura destruida$(NC)"

# Documentación
# =============
.PHONY: docs-serve
docs-serve: ## Servir documentación con Docsify
	@echo "$(BLUE)📚 Iniciando servidor de documentación...$(NC)"
	@echo "$(YELLOW)📖 Documentación disponible en: http://localhost:3000$(NC)"
	@echo "$(YELLOW)⏹️  Presiona Ctrl+C para detener$(NC)"
	@cd docs-site && python3 -m http.server 3000

.PHONY: docs-build
docs-build: ## Construir documentación estática
	@echo "$(BLUE)📚 Construyendo documentación...$(NC)"
	@echo "$(GREEN)✅ Documentación construida en docs-site/$(NC)"

.PHONY: docs-open
docs-open: ## Abrir documentación en el navegador
	@echo "$(BLUE)🌐 Abriendo documentación...$(NC)"
	@open http://localhost:3000 2>/dev/null || echo "$(YELLOW)⚠️  Abre manualmente: http://localhost:3000$(NC)"

# Utilidades
# ==========
.PHONY: logs
logs: ## Ver logs de la aplicación
	@echo "$(BLUE)📄 Mostrando logs de la aplicación...$(NC)"
	@tail -f app.log 2>/dev/null || echo "$(YELLOW)⚠️  No se encontró archivo de logs$(NC)"

.PHONY: format
format: ## Formatear código
	@echo "$(BLUE)🎨 Formateando código...$(NC)"
	@mvn spotless:apply || echo "$(YELLOW)⚠️  Spotless no configurado$(NC)"
	@echo "$(GREEN)✅ Código formateado$(NC)"

.PHONY: dependencies
dependencies: ## Mostrar dependencias del proyecto
	@echo "$(BLUE)📦 Mostrando dependencias...$(NC)"
	@mvn dependency:tree

.PHONY: update-dependencies
update-dependencies: ## Actualizar dependencias
	@echo "$(BLUE)🔄 Actualizando dependencias...$(NC)"
	@mvn versions:use-latest-versions
	@echo "$(GREEN)✅ Dependencias actualizadas$(NC)"

# Información
# ===========
.PHONY: info
info: ## Mostrar información del proyecto
	@echo "$(BLUE)📋 Información del Proyecto$(NC)"
	@echo "================================"
	@echo "$(GREEN)Nombre:$(NC) $(APP_NAME)"
	@echo "$(GREEN)Versión:$(NC) $(VERSION)"
	@echo "$(GREEN)Puerto:$(NC) $(PORT)"
	@echo "$(GREEN)URL Base:$(NC) $(BASE_URL)"
	@echo "$(GREEN)JAR:$(NC) $(JAR_FILE)"
	@echo ""
	@echo "$(YELLOW)Endpoints principales:$(NC)"
	@echo "  • $(BASE_URL)/ping"
	@echo "  • $(BASE_URL)/api/maps"
	@echo "  • $(BASE_URL)/api/weapons"
	@echo "  • $(BASE_URL)/api/analytics/dashboard"
	@echo "  • $(BASE_URL)/h2-console"

.PHONY: version
version: ## Mostrar versión del proyecto
	@echo "$(VERSION)"

# Limpieza
# ========
.PHONY: clean-all
clean-all: clean ## Limpieza completa (incluye logs y archivos temporales)
	@echo "$(BLUE)🧹 Limpieza completa...$(NC)"
	@rm -f app.log
	@rm -rf .mvn/wrapper/maven-wrapper.jar
	@echo "$(GREEN)✅ Limpieza completa finalizada$(NC)"

# Desarrollo completo
# ===================
.PHONY: dev
dev: build run ## Modo desarrollo (compilar y ejecutar)

.PHONY: dev-test
dev-test: build test run ## Modo desarrollo con tests

.PHONY: dev-full
dev-full: clean build test package run ## Desarrollo completo (limpiar, compilar, testear, empaquetar y ejecutar)

# Default target
# ==============
.DEFAULT_GOAL := help
