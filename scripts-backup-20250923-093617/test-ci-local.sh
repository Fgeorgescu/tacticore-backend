#!/bin/bash

# Script para probar el pipeline de CI localmente
# Simula los mismos pasos que ejecuta GitHub Actions

echo "🚀 Iniciando pruebas de CI local..."
echo "=================================="

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para imprimir con color
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    print_error "No se encontró pom.xml. Ejecuta este script desde la raíz del proyecto."
    exit 1
fi

# Paso 1: Validar configuración Maven
echo "📋 Paso 1: Validando configuración Maven..."
if mvn validate; then
    print_status "Configuración Maven válida"
else
    print_error "Error en configuración Maven"
    exit 1
fi

# Paso 2: Compilar código fuente
echo "🔨 Paso 2: Compilando código fuente..."
if mvn clean compile; then
    print_status "Compilación exitosa"
else
    print_error "Error en compilación"
    exit 1
fi

# Paso 3: Ejecutar tests unitarios
echo "🧪 Paso 3: Ejecutando tests unitarios..."
if mvn test; then
    print_status "Tests unitarios pasaron"
else
    print_error "Tests unitarios fallaron"
    exit 1
fi

# Paso 4: Generar reporte de cobertura
echo "📊 Paso 4: Generando reporte de cobertura..."
if mvn jacoco:report; then
    print_status "Reporte de cobertura generado"
    if [ -f "target/site/jacoco/index.html" ]; then
        echo "   📄 Reporte disponible en: target/site/jacoco/index.html"
    fi
else
    print_warning "No se pudo generar reporte de cobertura"
fi

# Paso 5: Análisis de calidad con SpotBugs
echo "🔍 Paso 5: Análisis de calidad con SpotBugs..."
if mvn spotbugs:check; then
    print_status "Análisis de SpotBugs completado sin errores críticos"
else
    print_warning "SpotBugs encontró algunos problemas (revisar reporte)"
    echo "   📄 Reporte disponible en: target/spotbugsXml.xml"
fi

# Paso 6: Verificación de dependencias (opcional - puede fallar por conectividad)
echo "🔒 Paso 6: Verificando dependencias..."
if mvn org.owasp:dependency-check-maven:check; then
    print_status "Verificación de dependencias completada"
else
    print_warning "Verificación de dependencias falló (posible problema de conectividad)"
    echo "   ℹ️  Esto es normal en entornos con restricciones de red"
fi

# Paso 7: Empaquetar aplicación
echo "📦 Paso 7: Empaquetando aplicación..."
if mvn package -DskipTests; then
    print_status "Aplicación empaquetada exitosamente"
    if [ -f "target/tacticore-backend-1.0.0.jar" ]; then
        echo "   📦 JAR disponible en: target/tacticore-backend-1.0.0.jar"
    fi
else
    print_warning "Error empaquetando aplicación (posible problema con shade plugin)"
    echo "   ℹ️  Intentando empaquetado básico sin shade..."
    if mvn jar:jar -DskipTests; then
        print_status "JAR básico creado exitosamente"
    else
        print_error "Error creando JAR básico"
        exit 1
    fi
fi

# Paso 8: Tests de integración (opcional)
echo "🔗 Paso 8: Tests de integración..."
if mvn verify -P integration-tests; then
    print_status "Tests de integración pasaron"
else
    print_warning "Tests de integración fallaron o no están configurados"
fi

# Resumen final
echo ""
echo "🎉 Resumen de CI Local:"
echo "======================"
print_status "Compilación: ✅"
print_status "Tests unitarios: ✅"
print_status "Empaquetado: ✅"

if [ -f "target/site/jacoco/index.html" ]; then
    print_status "Cobertura de código: ✅"
else
    print_warning "Cobertura de código: ⚠️"
fi

echo ""
echo "📋 Próximos pasos:"
echo "1. Revisar reportes generados en target/site/"
echo "2. Corregir cualquier warning o error encontrado"
echo "3. Hacer commit y push para ejecutar CI en GitHub Actions"
echo ""
echo "🔗 Para ver el pipeline completo en GitHub Actions:"
echo "   https://github.com/Fgeorgescu/tacticore-backend/actions"
