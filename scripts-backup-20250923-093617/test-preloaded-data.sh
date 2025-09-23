#!/bin/bash

# Script para probar los datos precargados
BASE_URL="http://localhost:8080"

echo "=== Probando Datos Precargados ==="
echo

# Esperar a que la aplicación esté lista
echo "Esperando a que la aplicación esté lista..."
sleep 5

# Probar estado de datos
echo "1. Verificando estado de datos..."
curl -s -X GET "$BASE_URL/api/data/status" | jq .
echo

# Probar lista de matches
echo "2. Obteniendo lista de matches..."
curl -s -X GET "$BASE_URL/api/matches" | jq .
echo

# Probar match específico
echo "3. Obteniendo match específico (ID: 1)..."
curl -s -X GET "$BASE_URL/api/matches/1" | jq .
echo

# Probar chat de match
echo "4. Obteniendo chat del match 1..."
curl -s -X GET "$BASE_URL/api/matches/1/chat" | jq .
echo

# Probar agregar mensaje de chat
echo "5. Agregando mensaje de chat..."
curl -s -X POST "$BASE_URL/api/matches/1/chat" \
  -H "Content-Type: application/json" \
  -d '{"user": "TestUser", "message": "Este es un mensaje de prueba"}' | jq .
echo

# Verificar que el mensaje se agregó
echo "6. Verificando que el mensaje se agregó..."
curl -s -X GET "$BASE_URL/api/matches/1/chat" | jq .
echo

# Probar match que no existe
echo "7. Probando match que no existe..."
curl -s -X GET "$BASE_URL/api/matches/999" | jq .
echo

# Probar análisis de kills (debería seguir funcionando)
echo "8. Probando análisis de kills..."
curl -s -X GET "$BASE_URL/api/analysis/overview" | jq '.total_kills, .headshot_rate, .top_players[0]'
echo

echo "=== Pruebas completadas ==="
