#!/bin/bash

# Script para crear JAR plano optimizado para Lambda

echo "🏗️  Creando JAR plano para Lambda..."

# Limpiar y crear directorio temporal
rm -rf target/lambda-jar
mkdir -p target/lambda-jar
cd target/lambda-jar

echo "📦 Extrayendo Spring Boot JAR..."
# Extraer el contenido del Spring Boot JAR
jar -xf ../tacticore-backend-1.0.0.jar

echo "🔧 Reorganizando estructura..."
# Mover clases de BOOT-INF/classes al root
if [ -d "BOOT-INF/classes" ]; then
    cp -r BOOT-INF/classes/* .
    rm -rf BOOT-INF/classes
fi

# Mover librerías de BOOT-INF/lib al root (para classpath plano)
if [ -d "BOOT-INF/lib" ]; then
    mkdir -p lib
    cp BOOT-INF/lib/*.jar lib/
    rm -rf BOOT-INF/lib
fi

# Limpiar directorios innecesarios
rm -rf BOOT-INF
rm -rf org/springframework/boot/loader

echo "📝 Creando MANIFEST.MF optimizado..."
# Crear nuevo MANIFEST.MF
mkdir -p META-INF
cat > META-INF/MANIFEST.MF << 'EOF'
Manifest-Version: 1.0
Main-Class: com.tacticore.lambda.SpringBootLambdaHandler
Class-Path: lib/

EOF

echo "🔧 Creando JAR plano..."
# Crear JAR plano
jar cfm ../tacticore-lambda-flat.jar META-INF/MANIFEST.MF .

# Volver al directorio padre
cd ..

# Limpiar directorio temporal
rm -rf lambda-jar

# Mostrar información del JAR
echo "✅ JAR plano creado:"
ls -lh tacticore-lambda-flat.jar

echo ""
echo "🔍 Verificando contenido..."
jar -tf tacticore-lambda-flat.jar | grep SpringBootLambdaHandler

echo ""
echo "🎯 Configuración para Lambda:"
echo "   Handler: com.tacticore.lambda.SpringBootLambdaHandler::handleRequest"
echo "   Runtime: Java 17"
echo "   Memory: 1024 MB"
echo "   Timeout: 30 seconds"
echo "   Archivo: target/tacticore-lambda-flat.jar"
