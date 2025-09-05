#!/bin/bash

# Script para probar la API de análisis de kills
BASE_URL="http://localhost:8080"

echo "=== Probando API de Análisis de Kills ==="
echo

# Esperar a que la aplicación esté lista
echo "Esperando a que la aplicación esté lista..."
sleep 5

# Probar estado de datos
echo "1. Verificando estado de datos..."
curl -s -X GET "$BASE_URL/api/data/status" | jq .
echo

# Probar análisis general
echo "2. Obteniendo análisis general..."
curl -s -X GET "$BASE_URL/api/analysis/overview" | jq .
echo

# Probar análisis de jugador específico
echo "3. Analizando jugador 'makazze'..."
curl -s -X GET "$BASE_URL/api/analysis/player/makazze" | jq .
echo

# Probar análisis de ronda específica
echo "4. Analizando ronda 1..."
curl -s -X GET "$BASE_URL/api/analysis/round/1" | jq .
echo

# Probar análisis de otro jugador
echo "5. Analizando jugador 'broky'..."
curl -s -X GET "$BASE_URL/api/analysis/player/broky" | jq .
echo

# Probar análisis de otra ronda
echo "6. Analizando ronda 2..."
curl -s -X GET "$BASE_URL/api/analysis/round/2" | jq .
echo

echo "=== Pruebas completadas ==="
